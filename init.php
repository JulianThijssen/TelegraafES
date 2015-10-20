<?php
    require 'vendor/autoload.php';
    
    use Elasticsearch\ClientBuilder;
    
    $client = ClientBuilder::create()->build();

