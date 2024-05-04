package juanfran.um

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import uschema.UschemaPackage
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import uschema.USchema
import documentschema.DocumentSchema
import documentschema.DocumentschemaPackage

class Main {
	static val INPUT_XMI_PATH = "input-files/USchema_2.xmi"
	static val OUTPUT_XMI_PATH = "output-files/Document_2.xmi"
	
	def static USchema loadSchema() {
		var ResourceSet resourceSet
		var Resource uscResource
		var URI uscUri = URI.createFileURI(INPUT_XMI_PATH)
		
		UschemaPackage.eINSTANCE.eClass()
		
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		
		uscResource = resourceSet.getResource(uscUri, true)
		
		return uscResource.contents.head as USchema
	}
	
	def static void saveSchema(DocumentSchema ds) {
		var ResourceSet resourceSet
		var Resource docResource
		var URI docUri = URI.createFileURI(OUTPUT_XMI_PATH)
		
		DocumentschemaPackage.eINSTANCE.eClass()
		
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		
		docResource = resourceSet.createResource(docUri)
		docResource.contents.add(ds)
		docResource.save(null)
	}
	
	def static void main(String[] args) {
		val USchema us = loadSchema()
		val MappingUschema2Document mapping = new MappingUschema2Document()
		val DocumentSchema ds = mapping.USchema2DocumentSchema(us)
		saveSchema(ds)
	}
}