package es.um.dsdm.document2uschema.main

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import documentschema.DocumentSchema
import java.util.Collections
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.ecore.resource.Resource
import documentschema.DocumentMMPackage
import java.io.IOException
import org.eclipse.emf.common.util.URI
import USchema.UschemaMMPackage
import USchema.USchemaClass
import es.um.dsdm.document2uschema.MappingDocument2Uschema

class Main {
	
	val static String USER_DIR = System.getProperty("user.dir")
	val static String INPUTSDIR = "/inputs/"
	val static String OUTPUTS_DIR = "/outputs/"
	val static String FILE_EXTESION = ".xmi";
	
	def DocumentSchema readModel(String inputFileName) {
		val rSet = new ResourceSetImpl()
		rSet.getPackageRegistry().put(DocumentMMPackage.eNS_URI, DocumentMMPackage.eINSTANCE)
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl())
		//val r = rSet.getResource(URI.createURI(inputFileName), true)
		val uri = 'file:///'+inputFileName
		val r = rSet.getResource(URI.createURI(uri),true)
		return r.getContents().get(0) as DocumentSchema
	}
	
	def writeModel(USchemaClass uschema, String outputUri) throws IOException {
		val reg = Resource.Factory.Registry.INSTANCE;
		val map = reg.getExtensionToFactoryMap();
		map.put("xmi", new XMIResourceFactoryImpl());

		val resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(UschemaMMPackage.eNS_URI, UschemaMMPackage.eINSTANCE);

		val resource = resourceSet.createResource(URI.createFileURI(outputUri));
		resource.getContents().add(uschema);

		resource.save(Collections.EMPTY_MAP);
	}
	
	def static void main(String[] args) {
		val inputFileName = "DocumentSchema-Regla4";
		val outputFileName = "USchema-Regla4";
		
		val main2 = new Main()
		
		println("Loading DocumentSchema from " + USER_DIR + INPUTSDIR + inputFileName + FILE_EXTESION)
		val entitiesModel = main2.readModel(USER_DIR + INPUTSDIR + inputFileName + FILE_EXTESION)
		
		println("Performing transformation m2m document2uschema")
		var entities2sql = new MappingDocument2Uschema;
		val sqlModel = entities2sql.document2uschema(entitiesModel)
		
		println("Writing USchema Model to " + USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
		main2.writeModel(sqlModel, USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
		
		
		println("USchema Model created at " + USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
	}
}