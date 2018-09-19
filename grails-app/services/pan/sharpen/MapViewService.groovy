package pan.sharpen


class MapViewService
{

	def getExtent( String imageFilename, Integer entryId = 0 )
	{
		def cmd = "ossim-info ${ imageFilename }"
		def results = cmd.execute().text
		def properties = new Properties()

		results.eachLine { line ->
			if ( line )
			{
				def keyValue = line.split( ':' )
				properties[ keyValue[ 0 ].trim() ] = keyValue[ 1 ]
			}
		}

		[ properties[ "image${ entryId }.geometry.ll_lon" ].toDouble(),
				properties[ "image${ entryId }.geometry.ll_lat" ].toDouble(),
				properties[ "image${ entryId }.geometry.ur_lon" ].toDouble(),
				properties[ "image${ entryId }.geometry.ur_lat" ].toDouble() ]
	}
}
