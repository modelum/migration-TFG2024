package juanfran.um

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import relationalschema.RelationalschemaPackage
import uschema.UschemaPackage
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import uschema.USchema
import relationalschema.RelationalSchema

class Main {
	static val INPUT_XMI_PATH = "input-files/Relational_0.xmi"
	static val OUTPUT_XMI_PATH = "output-files/USchema_0.xmi"
	
	def static void main(String[] args) {
		val MappingRelational2Uschema mapping = new MappingRelational2Uschema()
		mapping.loadSchema(INPUT_XMI_PATH)
		mapping.executeMapping()
		mapping.saveSchema(OUTPUT_XMI_PATH)
	}
}