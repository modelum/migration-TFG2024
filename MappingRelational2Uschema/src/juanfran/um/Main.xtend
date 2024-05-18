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
	static val INPUT_XMI_PATH = "input-files/Relational_6-1_N.xmi"
	static val OUTPUT_XMI_PATH = "output-files/USchema_6-1_N.xmi"
	
	def static RelationalSchema loadSchema() {
		var ResourceSet resourceSet
		var Resource relResource
		var URI relUri = URI.createFileURI(INPUT_XMI_PATH)
		
		RelationalschemaPackage.eINSTANCE.eClass()
		
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		
		relResource = resourceSet.getResource(relUri, true)
		
		return relResource.contents.head as RelationalSchema
	}
	
	def static void saveSchema(USchema us) {
		var ResourceSet resourceSet
		var Resource uscResource
		var URI uscUri = URI.createFileURI(OUTPUT_XMI_PATH)
		
		UschemaPackage.eINSTANCE.eClass()
		
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		
		uscResource = resourceSet.createResource(uscUri)
		uscResource.contents.add(us)
		uscResource.save(null)
	}
	
	def static void main(String[] args) {
		val RelationalSchema rs = loadSchema()
		val MappingRelational2Uschema mapping = new MappingRelational2Uschema()
		val USchema us = mapping.relationalSchema2USchema(rs)
		saveSchema(us)
	}
}