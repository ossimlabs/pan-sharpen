package pan.sharpen

import joms.oms.Init
import org.geotools.factory.Hints

class BootStrap
{

	def init = { servletContext ->
		Init.instance().initialize()
		Hints.putSystemDefault( Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE )
	}
	def destroy = {
	}
}
