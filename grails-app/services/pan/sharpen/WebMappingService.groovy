package pan.sharpen

import org.springframework.util.FastByteArrayOutputStream

import javax.imageio.ImageIO

class WebMappingService
{

	def getMap( def params )
	{
		def width = params.find { it.key.toUpperCase() == 'WIDTH' }?.value?.toInteger()
		def height = params.find { it.key.toUpperCase() == 'HEIGHT' }?.value?.toInteger()
		def bbox = params.find { it.key.toUpperCase() == 'BBOX' }?.value
		def srs = params.find { it.key.toUpperCase() == 'SRS' }?.value

		def inputFile = '/data/psm-test/5V090205M0001912264B220000100072M_001508507.ntf'
		def entryId = 0
		def bands = '3,2,1'

		def outputFile = File.createTempFile( 'oms', '.png', '/tmp' as File )

		def cmd = [ 'ossim-chipper', '--op', 'ortho',
				'--cut-width', width,
				'--cut-height', height,
				'--cut-wms-bbox', bbox,
				'--srs', srs,
				'--bands', bands,
				'--histogram-op', 'auto-minmax',
				'--output-radiometry', 'U8',
				inputFile,
				'--entry', entryId,
				outputFile
		]

//		println cmd.join( ' ' )

		def start = System.currentTimeMillis()
		def p = cmd.execute()

		p.consumeProcessOutput()

		def exitCode = p.waitFor()
		def stop = System.currentTimeMillis()

//		println exitCode
//		println "${ stop - start }"

		def image = ImageIO.read( outputFile )
		def ostream = new FastByteArrayOutputStream( width * height * 4 )

		ImageIO.write( image, 'png', ostream )

		outputFile.delete()

		[ contentType: 'image/png', file: ostream.inputStream ]
	}
}
