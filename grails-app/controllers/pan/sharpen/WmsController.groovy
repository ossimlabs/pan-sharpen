package pan.sharpen

class WmsController
{
	def webMappingService

	def getMap()
	{
		render webMappingService.getMap( params )
	}
}
