package pan.sharpen

class MapViewController
{
	def mapViewService
	
	def index()
	{
		def multiSpectralImageFilename = '/data/psm-test/5V090205M0001912264B220000100072M_001508507.ntf'
		def extent = new ImageMetadata( multiSpectralImageFilename ).extent
		
		[ mapViewParams: [
			extent: extent
		] ]
	}
}
