package pan.sharpen

import org.springframework.beans.factory.annotation.Value
import org.springframework.util.FastByteArrayOutputStream


import geoscript.workspace.PostGIS
import geoscript.feature.Schema
import geoscript.geom.Bounds

import geoscript.render.Map as GeoScriptMap
import geoscript.layer.Shapefile
import static geoscript.style.Symbolizers.*

class CollectsService
{
	GeoscriptConfig geoscriptConfig
	
	@Value('${collects.databaseName}')
	String databaseName
	
	def getTile( def params )
	{
		Integer width = params?.find { it.key.toUpperCase() == 'WIDTH' }?.value?.toInteger()
		Integer height = params?.find { it.key.toUpperCase() == 'HEIGHT' }?.value?.toInteger()
		List<Double> bbox = params?.find { it.key.toUpperCase() == 'BBOX' }?.value?.split( ',' )*.toDouble()
		String srs =  params?.find { it.key.toUpperCase() == 'SRS' }?.value
		String layers =  params?.find { it.key.toUpperCase() == 'LAYERS' }?.value
		
		def ostream = new FastByteArrayOutputStream( width * height * 4 )
		
		
		def postgis = new PostGIS( databaseName, user: 'postgres' )
		def layer = postgis[layers]
		
		layer.style = stroke( color: 'blue' ) + fill( opacity: 0 )
		
		def map = new GeoScriptMap(
			width: width,
			height: height,
			bounds: bbox,
			proj: srs,
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
