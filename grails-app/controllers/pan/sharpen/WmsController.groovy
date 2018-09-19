package pan.sharpen

import grails.async.web.AsyncController

class WmsController implements AsyncController
{
	def webMappingService
	
	def getMap()
	{
		def ctx = startAsync()
		ctx.start {
			render webMappingService.getMap( params )
			ctx.complete()
		}
	}
	
	def getPSM()
	{
		def ctx = startAsync()
		ctx.start {
			render webMappingService.getPanSharpen( params )
			ctx.complete()
		}
	}
	
}
