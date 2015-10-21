<html>
<head>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<?php
    require_once('init.php');
    
    // Elastic search
    if (isset($_GET['title']) and isset($_GET['text'])) {
        $title = $_GET['title'];
        $text = $_GET['text'];
        $subject = $_GET['subject'];
        
        $params['index'] = 'telegraaf';
        $params['type'] = 'doc';
        $params['body']['size'] = 100;
        
        $params['body']['query']['bool']['should'] = array();
        $params['body']['query']['bool']['must'] = array();
        if (!empty($title)) {
            array_push($params['body']['query']['bool']['should'], array('match' => array('title' => array('query' => $title, 'operator' => 'and', 'boost' => 2) )));
        }
        if (!empty($text)) {
            array_push($params['body']['query']['bool']['should'], array('match' => array('text' => array('query' => $text, 'operator' => 'and', 'boost' => 1) )));
        }
        if (!empty($subject)) {
            //array_push($params['body']['query']['bool']['must'], array('match' => array('subject' => $subject)));
        }
        
        $response = $client->search($params);

        if ($response['hits']['total'] > 0) {
            $results = $response['hits']['hits'];
        }
	}
    
    // Facets
    if (isset($results)) {
        $facets = array();
        foreach ($results as $r) {
            $sub = $r['_source']['subject'];
            if (!isset($facets[$sub])) {
                $facets[$sub] = 0;
            }
            $facets[$sub]++;
        }
    }
    
    // Plot timeline
    if (isset($results)) {
        $fp = fopen('years', 'w');
        $years = array();
        foreach ($results as $r) {
            // Filter results based on facet
            if ($r['_source']['subject'] === $subject) {
                $year = explode('-', $r['_source']['date'])[0];
                fwrite($fp, $year . PHP_EOL);
            }
        }
        fclose($fp);

        system("python timeline.py", $return);
    }
?>

<center><a href="index.php"><img src="logo-telegraaf.png" /></a></center>

<div id="cform">
    <form action="index.php" method="get">
        Title: <input type="text" name="title" placeholder="Title"></input></br>
        Text: <input type="text" name="text" placeholder="Text"></input></br>
        <center><input type="submit" value="Submit" /></center>
    </form>
</div>

<?php
if (isset($results)) {
    ?>
    <ul><?php
    foreach ($facets as $fsubject => $count) {
        echo sprintf("<li><a href='http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]&subject=%s'>%s (%d)</a></li>", $fsubject, $fsubject, $count);
    }
    ?>
    </ul><?php
    foreach ($results as $r) {
        // Filter results based on facet
        if ($r['_source']['subject'] === $subject) {
        ?>
            <div class="result">
                <?php
                if (!empty($r['_source']['title'])) {
                    echo "<a href=\"" .$r['_source']['source']. "\">" .$r['_source']['title'] . " | " . $r['_source']['date'] . "</a>";
                } else {
                    echo "<a href=\"" .$r['_source']['source']. "\">" . $r['_source']['subject'] . " | " . $r['_source']['date'] . "</a>";
                }
                ?>
                <p><?php echo $r['_source']['text']; ?></p>
            </div>
        <?php
        }
    }
    echo '<center><img src="timeline.png" /></center>';
} else {
    echo "<center>No results found.</center>";
}
?>
</br></br></br>
</body>
</html>
