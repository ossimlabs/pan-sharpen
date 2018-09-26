package pan.sharpen

class ImageMetadata
{
	Properties metadata = new Properties()
	
	ImageMetadata( String imageFilename )
	{
		def cmd = "ossim-info ${ imageFilename }"
		def results = cmd.execute().text
		
		results.eachLine { line ->
			if ( line )
			{
				def keyValue = line.split( ':' )
				metadata[keyValue[0].trim()] = keyValue[1]
			}
		}
	}
	
	def getExtent( Integer entryId = 0 )
	{
		def llLon = metadata["image${ entryId }.geometry.ll_lon"].toDouble()
		def llLat = metadata["image${ entryId }.geometry.ll_lat"].toDouble()
		def urLon = metadata["image${ entryId }.geometry.ur_lon"].toDouble()
		def urLat = metadata["image${ entryId }.geometry.ur_lat"].toDouble()
		
		[ Math.min( llLon, urLon ),
			Math.min( llLat, urLat ),
			Math.max( llLon, urLon ),
			Math.max( llLat, urLat ) ]
	}
	
	def getRgbBands( Integer entryId = 0 )
	{
		metadata["image${ entryId }.rgb_bands"]
	}
	
	def getNumberOfBands( Integer entryId = 0 )
	{
		metadata["image${ entryId }.number_output_bands"]?.toInteger()
	}
}
