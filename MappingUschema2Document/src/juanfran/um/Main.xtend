package juanfran.um

class Main {
	static val INPUT_XMI_PATH = "input-files/USchema_3.xmi"
	static val OUTPUT_XMI_PATH = "output-files/Document_3.xmi"
	
	def static void main(String[] args) {
		val MappingUschema2Document mapping = new MappingUschema2Document()
		mapping.loadSchema(INPUT_XMI_PATH)
		mapping.executeMapping()
		mapping.saveSchema(OUTPUT_XMI_PATH)
	}
}