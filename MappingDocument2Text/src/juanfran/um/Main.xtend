package juanfran.um

class Main {
	static val INPUT_XMI_PATH = "input-files/Document_integration.xmi"
	static val OUTPUT_JS_PATH = "output-files/Mongoose_integration.js"
	
	def static void main(String[] args) {
		val mapping2Text = new MappingDocument2Text
		
		mapping2Text.loadSchema(INPUT_XMI_PATH)
		
		println("Starting the mapping process from DocumentSchema to JavaScript file...")
		mapping2Text.mappingDocument2Text(OUTPUT_JS_PATH)
		println("Mapping successful! JavaScript file has been created at '" + OUTPUT_JS_PATH + "'")
	}
}