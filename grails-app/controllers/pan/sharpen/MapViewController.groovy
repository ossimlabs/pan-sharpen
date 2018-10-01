package pan.sharpen

class MapViewController
{
	def mapViewService
	def openLayersConfig
	
	def index()
	{
//		def multiSpectralImageFilename = '/data/psm-test/5V090205M0001912264B220000100072M_001508507.ntf'
		def multiSpectralImageFilename = '/data/I00000116845_01/I00000116845_01_P001_MUL/18AUG30143528-M1BS_R2C1-I00000116845_01_P001.TIF'
		
		def extent = new ImageMetadata( multiSpectralImageFilename ).extent
		
		println extent
		
		[ mapViewParams: [
			
			extent: extent,
			openLayersConfig: openLayersConfig
		] ]
	}
}
