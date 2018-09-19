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
		[ metadata["image${ entryId }.geometry.ll_lon"].toDouble(),
			metadata["image${ entryId }.geometry.ll_lat"].toDouble(),
			metadata["image${ entryId }.geometry.ur_lon"].toDouble(),
			metadata["image${ entryId }.geometry.ur_lat"].toDouble() ]
	}
	
	def getRgbBands( Integer entryId = 0 )
	{
		metadata["image${ entryId }.rgb_bands"]
	}
}
