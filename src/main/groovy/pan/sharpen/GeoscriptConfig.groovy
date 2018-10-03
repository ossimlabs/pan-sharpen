package pan.sharpen

import groovy.transform.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter


@ToString( includeNames = true )
@ConfigurationProperties( prefix = "geoscript.config", ignoreInvalidFields = true )
class GeoscriptConfig
{
	List<NamespaceInfo> namespaces
	List<WorkspaceInfo> workspaces
	List<LayerInfo> layers
	
	@ToString( includeNames = true )
	static class NamespaceInfo
	{
		String prefix
		String uri
	}
	
	@ToString( includeNames = true )
	static class WorkspaceInfo
	{
		String name
		String namespaceId
		Map<String, Object> params
	}
	
	@ToString( includeNames = true )
	static class LayerInfo
	{
		String name
		String title
		String description
		List<String> keywords
		String workspaceId
	}
	
	@ConfigurationPropertiesBinding
	static class GeoscriptNamespaceInfoConverter implements Converter<Map<String, Object>, NamespaceInfo>
	{
		@Override
		NamespaceInfo convert( Map<String, Object> map )
		{
			return new NamespaceInfo( map )
		}
	}
	
	@ConfigurationPropertiesBinding
	static class GeoscriptWorkspaceInfoConverter implements Converter<Map<String, Object>, WorkspaceInfo>
	{
		@Override
		WorkspaceInfo convert( Map<String, Object> map )
		{
			return new WorkspaceInfo( map )
		}
	}
	
	@ConfigurationPropertiesBinding
	static class GeoscriptLayerInfoConverter implements Converter<Map<String, Object>, LayerInfo>
	{
		@Override
		LayerInfo convert( Map<String, Object> map )
		{
			return new LayerInfo( map )
		}
	}
}
