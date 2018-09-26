package pan.sharpen

class CollectsController
{
	CollectsService collectsService
	
	def index()
	{
		[ collectsParams: [ extent: [-180, -90, 180, 90]] ]
	}
	
	def getTile()
	{
		render collectsService.getTile( params )
	}
}
