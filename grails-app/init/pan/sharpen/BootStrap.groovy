package pan.sharpen

import joms.oms.Init
import org.geotools.factory.Hints

class BootStrap
{
	def bucketService
	def openLayersConfig
	
	def init = { servletContext ->
		Init.instance().initialize()
		Hints.putSystemDefault( Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE )
		
//		Thread.start {
//			bucketService.serviceMethod()
//		}
		
		println openLayersConfig
	}
	def destroy = {
	}
}
