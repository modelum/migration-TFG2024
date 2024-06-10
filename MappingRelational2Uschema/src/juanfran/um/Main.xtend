package juanfran.um

class Main {
	static val INPUT_XMI_PATH = "test-input-files/Relational_6-1_N.xmi"
	static val OUTPUT_XMI_PATH = "output-files/USchema_6-1_N.xmi"
	
	def static void main(String[] args) {
		val MappingRelational2Uschema mapping = new MappingRelational2Uschema()
		mapping.loadSchema(INPUT_XMI_PATH)
		mapping.executeMapping()
		mapping.saveSchema(OUTPUT_XMI_PATH)
	}
}