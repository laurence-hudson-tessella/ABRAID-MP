<#--
    The atlas page for display of model results.
    This content will reside in an IFrame, on the ABRAID-MP public site and TGHN website.
    Copyright (c) 2014 University of Oxford
-->
<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>MAP</title>

    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/css/bootstrap.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/css/bootstrap-theme.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.min.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/leaflet.markercluster/0.2/MarkerCluster.css">
    <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Source+Sans+Pro:600'  type='text/css'>
    <link rel="stylesheet" href="<@spring.url "/css/markers.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/L.Control.Zoomslider.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/flipclock.css" />">
    <link rel="stylesheet" href="<@spring.url "/css/dataValidation.css" />">
</head>
<body>
    <div id="map">
        <#include "layerselector.ftl"/>
        <#include "atlasInformation.ftl"/>
        <div class="leaflet-bottom leaflet-left">
            <div class="legend leaflet-bar leaflet-control">
                <i style="background-color:#a44883;"></i><span>1</span><br>
                <i style="background-color:#a44883; background-image: -webkit-gradient(linear, left top, left bottom, from(#a44883), to(#cf93ba)); background-image: -webkit-linear-gradient(top, #a44883, #cf93ba); background-image: -moz-linear-gradient(top, #a44883, #cf93ba); background-image: -o-linear-gradient(top, #a44883, #cf93ba); background-image: linear-gradient(to bottom, #a44883, #cf93ba);"></i><br>
                <i style="background-color:#cf93ba; background-image: -webkit-gradient(linear, left top, left bottom, from(#cf93ba), to(#ffffcb)); background-image: -webkit-linear-gradient(top, #cf93ba, #ffffcb); background-image: -moz-linear-gradient(top, #cf93ba, #ffffcb); background-image: -o-linear-gradient(top, #cf93ba, #ffffcb); background-image: linear-gradient(to bottom, #cf93ba, #ffffcb);"></i><br>
                <i style="background-color:#ffffcb; background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffcb), to(#c3d4bb)); background-image: -webkit-linear-gradient(top, #ffffcb, #c3d4bb); background-image: -moz-linear-gradient(top, #ffffcb, #c3d4bb); background-image: -o-linear-gradient(top, #ffffcb, #c3d4bb); background-image: linear-gradient(to bottom, #ffffcb, #c3d4bb);"></i><br>
                <i style="background-color:#c3d4bb; background-image: -webkit-gradient(linear, left top, left bottom, from(#c3d4bb), to(#91ab84)); background-image: -webkit-linear-gradient(top, #c3d4bb, #91ab84); background-image: -moz-linear-gradient(top, #c3d4bb, #91ab84); background-image: -o-linear-gradient(top, #c3d4bb, #91ab84); background-image: linear-gradient(to bottom, #c3d4bb, #91ab84);"></i><br>
                <i style="background-color:#91ab84;"></i><span>0</span><br>
            </div>
        </div>
    </div>

    <!-- Base url -->
    <script>
        var baseUrl = "<@spring.url '/'/>";
    </script>

    <!-- Bootstrapped JS data for KO view models -->
    <script>
        var data = {
            wmsUrl: "http://localhost:8081/geoserver/abraid/wms",
            layers: ${layers}
        };
    </script>

    <!-- Require -->
    <script type="text/javascript" data-main="<@spring.url '/js/kickstart/atlas/content' />" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.js"></script>
</body>
</html>
