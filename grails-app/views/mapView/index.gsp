<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>Pan Sharpen</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <asset:stylesheet src="mapView.css"/>
</head>
<body>
<div id="map" class="map"></div>
<asset:javascript src="mapView.js"/>
<asset:script>
    $(document).ready(function() {
        var params = ${raw(mapViewParams?.encodeAsJSON()?.toString())};
        MapView.init(params);
    } );
</asset:script>
<asset:deferredScripts/>
</body>
</html>
