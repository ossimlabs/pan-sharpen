import pan.sharpen.OpenLayersConfig
import pan.sharpen.GeoscriptConfig

// Place your Spring DSL code here
beans = {
	openLayersConfig( OpenLayersConfig )
	openLayersLayerConverter( OpenLayersConfig.OpenLayersLayerConverter )
	
	geoscriptConfig( GeoscriptConfig )
	geoscriptNamespaceInfoConverter( GeoscriptConfig.GeoscriptNamespaceInfoConverter )
	geoscriptWorkspaceInfoConverter( GeoscriptConfig.GeoscriptWorkspaceInfoConverter )
	geoscriptLayerInfoConverter( GeoscriptConfig.GeoscriptLayerInfoConverter )
}
