package pan.sharpen

import groovy.transform.ToString
import groovy.util.slurpersupport.GPathResult
import joms.oms.DataInfo
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader

@ToString( includeNames = true )
class ImageMetadata
{
	Geometry geometry
	String srs
	int numberOfBands
	
	GPathResult oms
	
	ImageMetadata( String imageFilename )
	{
		oms = new XmlSlurper().parseText( DataInfo.readInfo( imageFilename ) )
		
		def rasterEntryNode = oms.dataSets.RasterDataSet.rasterEntries.RasterEntry
		def wkt = rasterEntryNode.groundGeom.text()
		def wktReader = new WKTReader()
		
		srs = rasterEntryNode.groundGeom.@srs.text()
		geometry = wktReader.read( wkt )
		
		numberOfBands = rasterEntryNode.numberOfBands.text().toInteger()
	}
	
	def getExtent( Integer entryId = 0 )
	{
		def envelope = geometry.envelopeInternal
		
		[ envelope.minX, envelope.minY, envelope.maxX, envelope.maxY ]
	}
	
/*
	def getRgbBands( Integer entryId = 0 )
	{
		metadata["image${ entryId }.rgb_bands"]
	}
*/
}
