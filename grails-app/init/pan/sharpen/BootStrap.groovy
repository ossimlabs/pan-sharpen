package pan.sharpen

import joms.oms.Init

class BootStrap
{

	def init = { servletContext ->
		Init.instance().initialize()
	}
	def destroy = {
	}
}
