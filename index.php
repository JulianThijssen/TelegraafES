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
        
        $params['index'] = 'telegraaf';
        $params['type'] = 'doc';
        $params['body']['query']['bool']['should'] = array();
        if (!empty($title)) {
            array_push($params['body']['query']['bool']['should'], array('match' => array('title' => $title)));
        }
        if (!empty($text)) {
            array_push($params['body']['query']['bool']['should'], array('match' => array('text' => $text)));
        }
        
        $response = $client->search($params);

        if ($response['hits']['total'] > 0) {
            $results = $response['hits']['hits'];
        }
	}
    
    // Plot timeline
    if (isset($results)) {
        $fp = fopen('years', 'w');
        $years = array();
        foreach ($results as $r) {
            $year = explode('-', $r['_source']['date'])[0];
            fwrite($fp, $year . PHP_EOL);
        }
        fclose($fp);

        system("python timeline.py", $return);
    }
?>

<center><img src="logo-telegraaf.png" /></center>

<div id="cform">
    <form action="index.php" method="get">
        Title: <input type="text" name="title" placeholder="Title"></input></br>
        Text: <input type="text" name="text" placeholder="Text"></input></br>
        <center><input type="submit" value="Submit" /></center>
    </form>
</div>

<?php
if (isset($results)) {
    foreach ($results as $r) {
    ?>
        <div class="result">
            <?php
            if (!empty($r['_source']['title'])) {
                echo "<a href=\"" .$r['_source']['source']. "\">" .$r['_source']['title'] . " | " . $r['_source']['date'] . "</a>";
            } else {
                echo "<a href=\"" .$r['_source']['source']. "\">Advertentie"  . " | " . $r['_source']['date'] . "</a>";
            }
            ?>
            <p><?php echo $r['_source']['text']; ?></p>
        </div>
    <?php
    }
    echo '<center><img src="timeline.png" /></center>';
} else {
    echo "<center>No results found.</center>";
}
?>
</br></br></br>
</body>
</html>
