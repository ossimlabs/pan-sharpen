package pan.sharpen

import org.springframework.util.FastByteArrayOutputStream


import geoscript.workspace.PostGIS
import geoscript.feature.Schema
import geoscript.geom.Bounds

import geoscript.render.Map as GeoScriptMap
import geoscript.layer.Shapefile
import static geoscript.style.Symbolizers.*

class CollectsService
{
	
	def getTile( def params )
	{
		Integer width = params?.find { it.key.toUpperCase() == 'WIDTH' }?.value?.toInteger()
		Integer height = params?.find { it.key.toUpperCase() == 'HEIGHT' }?.value?.toInteger()
		List<Double> bbox = params?.find { it.key.toUpperCase() == 'BBOX' }?.value?.split( ',' )*.toDouble()
		def ostream = new FastByteArrayOutputStream( width * height * 4 )
		
		
		def postgis = new PostGIS( 'dg-bucket-db-prod', user: 'postgres' )
		def layer = postgis['collects']
		
		layer.style = stroke( color: 'blue' ) + fill( opacity: 0 )
		
		def map = new GeoScriptMap(
			width: width,
			height: height,
			bounds: bbox,
			proj: 'epsg:4326',
			layers: [
				layer
			]
		)
		
		map.render( ostream )
		map.close()
		
		postgis?.close()
		
		[ contentType: 'image/png', file: ostream.inputStream ]
	}
}
