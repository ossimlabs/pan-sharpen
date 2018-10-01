package pan.sharpen

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.GetObjectMetadataRequest
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import static groovyx.gpars.GParsPool.withPool

class BucketService
{
	
	def serviceMethod()
	{
		
		
		String bucketName = "dg-1b-3090-t1"
		String profileName = "svc_radiant_omar_1b"
		String startAfter = 'I00000140895_01'
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
			.withCredentials( new ProfileCredentialsProvider( profileName ) )
//			.withRegion( clientRegion )
			.build()
		
		ListObjectsV2Request request = new ListObjectsV2Request()
			.withBucketName( bucketName )
			.withStartAfter( startAfter )
			.withDelimiter( '/' )
		
		ListObjectsV2Result result
		
		def start = System.currentTimeMillis()
		
		def mountPoint = "/data/${ bucketName }"
		
		new File( '/tmp/prefixes.txt' ).withWriter { out ->
			withPool {
				while ( ( result = s3Client.listObjectsV2( request ) )?.isTruncated() )
				{
					result?.commonPrefixes?.eachParallel { prefix ->
						def key = "${ prefix }${ prefix[0..<-1] }_README.XML"
						def fullObject
						try
						{
							def getObjectMetadataRequest = new GetObjectMetadataRequest( bucketName, key )
							def objectMetadata = s3Client.getObjectMetadata( getObjectMetadataRequest )
							
							if ( !objectMetadata?.storageClass )
							{
								fullObject = s3Client.getObject( new GetObjectRequest( bucketName, key ) )
								
								def readMe = new XmlSlurper().parse( fullObject?.objectContent )
								
								out.println "${ prefix },${ readMe.NWLONG.text() },${ readMe.SELAT.text() },${ readMe.SELONG.text() },${ readMe.NWLAT.text() },${ readMe.COLLECTIONSTART.text() },${ readMe.COLLECTIONSTOP.text() }"
							}
							
						}
						catch ( e )
						{
							System.err.println "${ key } ${ e.message }"
						}
						finally
						{
							fullObject?.close()
						}
					}
					
					request?.continuationToken = result.nextContinuationToken
				}
			}
			out.flush()
		}
		
		def stop = System.currentTimeMillis()
		
		println "${ stop - start }"
		
	}
}
