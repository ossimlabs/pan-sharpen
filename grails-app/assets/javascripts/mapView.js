//= require jquery-2.2.0.min.js
//= require webjars/openlayers/4.6.5/ol.js
//= require webjars/github-com-walkermatt-ol-layerswitcher/2.0.0/ol-layerswitcher.js
//= require_self

var MapView = (function() {
    function init( params ) {
      var layers = [
        new ol.layer.Group({
            title: 'Base Maps',
            layers: [
                new ol.layer.Tile({
                  title: 'OMAR Base Map',
                  type: 'base',
                  source: new ol.source.TileWMS({
                    url: 'https://omar-dev.ossim.io/omar-mapproxy/service',
                    params: {
                      'LAYERS': 'o2-basemap-basic',
                      'FORMAT': 'image/jpeg'
                    }
                  })
                })
            ]
        }),
        new ol.layer.Group({
            title: 'Overlays',
            layers: [
                new ol.layer.Tile({
                  title: 'MultiSpectral',
                  source: new ol.source.TileWMS({
                    url: '/wms/getMap',
                    params: {
                      'LAYERS': '/data/psm-test/5V090205M0001912264B220000100072M_001508507.ntf',
                      'VERSION': '1.1.1',
                      'FORMAT': 'image/png'
                    }
                  })
                }),
                new ol.layer.Tile({
                  title: 'Panchromatic',
                  source: new ol.source.TileWMS({
                    url: '/wms/getMap',
                    params: {
                      'LAYERS': '/data/psm-test/5V090205P0001912264B220000100282M_001508507.ntf',
                      'VERSION': '1.1.1',
                      'FORMAT': 'image/png'
                    }
                  })
                })
            ]
        })
      ];

      var map = new ol.Map({
        controls: ol.control.defaults().extend([
          new ol.control.ScaleLine({
            units: 'degrees'
          })
        ]),
        layers: layers,
        target: 'map',
        view: new ol.View({
          projection: 'EPSG:4326',
          center: [0, 0],
          zoom: 2
        })
      });

      var extent = params.extent;
      map.getView().fit(extent, map.getSize());

      var layerSwitcher = new ol.control.LayerSwitcher({
            tipLabel: 'Légende' // Optional label for button
      });
      map.addControl(layerSwitcher);
    }
    return {
        init: init
    };
})();