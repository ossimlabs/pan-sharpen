package pan.sharpen

class CollectsController
{
	CollectsService collectsService
	OpenLayersConfig openLayersConfig
	
	def index()
	{
		[ collectsParams: [
			openLayersConfig: openLayersConfig,
			extent: [-180, -90, 180, 90]]
		]
	}
	
	def getTile()
	{
		render collectsService.getTile( params )
	}
}
