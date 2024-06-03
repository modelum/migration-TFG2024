package juanfran.um

class Main {
	static val INPUT_XMI_PATH = "input-files/Relational_5.xmi"
	static val OUTPUT_XMI_PATH = "output-files/USchema_5.xmi"
	
	def static void main(String[] args) {
		val MappingRelational2Uschema mapping = new MappingRelational2Uschema()
		mapping.loadSchema(INPUT_XMI_PATH)
		mapping.executeMapping()
		mapping.saveSchema(OUTPUT_XMI_PATH)
	}
}