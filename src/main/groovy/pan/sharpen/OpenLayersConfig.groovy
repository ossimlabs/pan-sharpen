package pan.sharpen

import groovy.transform.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter


@ToString( includeNames = true )
@ConfigurationProperties( prefix = "openlayers.config", ignoreInvalidFields = true )
class OpenLayersConfig
{
	List<OpenLayersLayer> baseMaps
	List<OpenLayersLayer> overlays
	Integer zoomFilterChangeLevel
	
	@ToString( includeNames = true )
	static class OpenLayersLayer
	{
		String layerType
		String title
		String url
		Map<String, Object> params
		Map<String, Object> options
	}
	
	@ConfigurationPropertiesBinding
	static class OpenLayersLayerConverter implements Converter<Map<String, Object>, OpenLayersLayer>
	{
		@Override
		OpenLayersLayer convert( Map<String, Object> map )
		{
			return new OpenLayersLayer( map )
		}
	}
}
