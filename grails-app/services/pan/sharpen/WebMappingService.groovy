package pan.sharpen

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel
import org.springframework.util.FastByteArrayOutputStream

import javax.imageio.ImageIO
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

import org.ossim.oms.util.TransparentFilter
import org.ossim.oms.image.omsRenderedImage
import org.ossim.oms.image.omsImageSource

import joms.oms.Chipper
import joms.oms.ossimMemoryImageSource

class WebMappingService
{
	def getMap( def params )
	{
		def width = params.find { it.key.toUpperCase() == 'WIDTH' }?.value?.toInteger()
		def height = params.find { it.key.toUpperCase() == 'HEIGHT' }?.value?.toInteger()
		def outputImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB )
		def bbox = params.find { it.key.toUpperCase() == 'BBOX' }?.value
		def srs = params.find { it.key.toUpperCase() == 'SRS' }?.value
		def layers = params.find { it.key.toUpperCase() == 'LAYERS' }?.value
		
		def inputFile = layers
		def imageMetadata = new ImageMetadata( inputFile )
		def entryId = 0
		def bands = imageMetadata.rgbBands ? '3,2,1' : 1
//		def bands = 'default'
		
		def extent = imageMetadata.extent
		def coords = bbox?.split( ',' )*.toDouble()
		
		def geom1 = createGeom( coords, srs )
		def geom2 = createGeom( extent, 'epsg:4326' )
		
		if ( geom1.intersects( geom2 ) )
		{
			Map<String, String> options = [
				bands: bands,
				cut_height: width as String,
				cut_width: height as String,
				cut_wms_bbox: bbox,
				hist_op: 'auto-minmax',
				operation: 'ortho',
				output_radiometry: 'U8',
				resampler_filter: 'neighbor',
				srs: srs?.split( ':' )?.last()
			]
			
			layers?.split( ',' ).eachWithIndex { layer, i ->
				options["image${ i }.file"] = layer
			}
			
//			def image = runChipperCLI( options )
			def image = runChipperJNI( options )
			
			image = TransparentFilter.fixTransparency( new TransparentFilter(), image )
			
			def g2d = outputImage.createGraphics()
			
			g2d.drawRenderedImage( image, new AffineTransform() )
			g2d.dispose()
		}
		
		def ostream = new FastByteArrayOutputStream( width * height * 4 )
		
		ImageIO.write( outputImage, 'png', ostream )
		
		
		[ contentType: 'image/png', file: ostream.inputStream ]
	}
	
	def getPanSharpen( def params )
	{
		def width = params.find { it.key.toUpperCase() == 'WIDTH' }?.value?.toInteger()
		def height = params.find { it.key.toUpperCase() == 'HEIGHT' }?.value?.toInteger()
		def outputImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB )
		def bbox = params.find { it.key.toUpperCase() == 'BBOX' }?.value
		def srs = params.find { it.key.toUpperCase() == 'SRS' }?.value
		def layers = params.find { it.key.toUpperCase() == 'LAYERS' }?.value
		
		def imageMetadata = layers?.split( ',' )?.inject( [ : ] ) { a, b ->
			a[b] = new ImageMetadata( b )
			a
		}
		
		def bands = '3,2,1'
//		def bands = 'default'
		def extent = imageMetadata.values().first().extent

//		println "extent: ${ extent }"
		
		def coords = bbox?.split( ',' )*.toDouble()
		
		def geom1 = createGeom( coords, srs )
		def geom2 = createGeom( extent, 'epsg:4326' )
		
		if ( geom1.intersects( geom2 ) )
		{
			Map<String, String> options = [
				bands: bands,
				cut_height: width as String,
				cut_width: height as String,
				cut_wms_bbox: bbox,
				hist_op: 'auto-minmax',
				operation: 'psm',
				output_radiometry: 'U8',
				resampler_filter: 'gaussian',
				srs: srs?.split( ':' )?.last()
			]
			
			layers?.split( ',' ).eachWithIndex { layer, i ->
				options["image${ i }.file"] = layer
			}

//			def image = runChipperCLI( options )
			def image = runChipperJNI( options )
			
			image = TransparentFilter.fixTransparency( new TransparentFilter(), image )
			
			def g2d = outputImage.createGraphics()
			
			g2d.drawRenderedImage( image, new AffineTransform() )
			g2d.dispose()
		}
		
		def ostream = new FastByteArrayOutputStream( width * height * 4 )
		
		ImageIO.write( outputImage, 'png', ostream )
		
		[ contentType: 'image/png', file: ostream.inputStream ]
	}
	
	private def createGeom( coords, srs )
	{
		def points = [
			[ coords[0], coords[1] ],
			[ coords[0], coords[3] ],
			[ coords[2], coords[3] ],
			[ coords[2], coords[1] ],
			[ coords[0], coords[1] ]
		] as Coordinate[]
		
		def code = srs?.split( ':' )?.last()?.toInteger()
		def geometryFactory = new GeometryFactory( new PrecisionModel( PrecisionModel.FLOATING ), code )
		def geom1 = geometryFactory.createPolygon( points )
		
		geom1
	}
	
	private BufferedImage runChipperJNI( Map<String, String> options )
	{
//		println options
		
		def bufferedImage
		def chipper = new Chipper()
		def start = System.currentTimeMillis()
		
		if ( chipper.initialize( options ) )
		{
//			println 'initialized'
			def imageData = chipper.getChip( options )

//    [ 'width', 'height', 'numberOfBands', 'scalarSizeInBytes',
//        'sizePerBandInBytes', 'dataSizeInBytes' ].each {
//        println "${it}: ${imageData[it]}"
//    }
			
			if ( ( imageData != null ) && ( imageData.get() != null ) )
			{
				def cacheSource = new ossimMemoryImageSource()
				cacheSource?.setImage( imageData )
				
				def renderedImage = new omsRenderedImage( new omsImageSource( cacheSource ) )
//        def sampleModel = renderedImage.sampleModel
				
				bufferedImage = new BufferedImage(
					renderedImage.colorModel, renderedImage.data, true, null )
				
				renderedImage = null
				cacheSource?.delete(); cacheSource = null
				imageData?.delete(); imageData = null
				chipper?.delete(); chipper = null
			}
		}
		else
		{
			System.err.println 'Error initializing!'
		}
		
		def stop = System.currentTimeMillis()
		
//		println "${ stop - start }"
		chipper?.delete()

//		if ( bufferedImage != null ) {
//			ImageIO.write(bufferedImage, 'png', '/tmp/blah.png' as File)
//		}
		
		bufferedImage
	}
	
	
	private BufferedImage runChipperCLI( Map<String, String> options )
	{
//		println options
		
		def outputFile = File.createTempFile( 'oms', '.png', '/tmp' as File )
		def files = options.findAll { it.key ==~ /image\d+\.file/ }?.collect { it.value }?.join(' ')
		
//		def msFile = files[0]
//		def panFile = files[1]
		
		def cmd = [ 'ossim-chipper', '--op', options.operation,
			'--cut-width', options.cut_width,
			'--cut-height', options.cut_height,
			'--cut-wms-bbox', options.cut_wms_bbox,
			'--srs', options.srs,
			'--bands', options.bands,
			'--histogram-op', 'auto-minmax',
			'--output-radiometry', 'U8',
			'--writer-prop', 'create_external_geometry=false',
			'--resample-filter', options.resampler_filter ?: 'neighbor',
			files,
//			msFile,
//			panFile,
//    '--entry', entryId,
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
		
		outputFile.delete()
		image
	}
}
