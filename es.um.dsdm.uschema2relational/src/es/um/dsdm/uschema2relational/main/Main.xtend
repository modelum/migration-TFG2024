package es.um.dsdm.uschema2relational.main
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import java.util.Collections
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.ecore.resource.Resource
import java.io.IOException
import org.eclipse.emf.common.util.URI
import relationalschema.RelationalSchema
import es.um.dsdm.uschema2relational.MappingUSchema2Relational
import uschema.USchema
import uschema.UschemaMMPackage

class Main {
	
	val static String USER_DIR = System.getProperty("user.dir")
	val static String INPUTSDIR = "/inputs/"
	val static String OUTPUTS_DIR = "/outputs/"
	val static String FILE_EXTESION = ".xmi";
	
	def USchema readModel(String inputFileName) {
		val rSet = new ResourceSetImpl()
		rSet.getPackageRegistry().put(UschemaMMPackage.eNS_URI, UschemaMMPackage.eINSTANCE)
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl())
		//val r = rSet.getResource(URI.createURI(inputFileName), true)
		val uri = 'file:///'+inputFileName
		val r = rSet.getResource(URI.createURI(uri),true)
		return r.getContents().get(0) as USchema
	}
	
	def writeModel(RelationalSchema relationalschema, String outputUri) throws IOException {
		val reg = Resource.Factory.Registry.INSTANCE;
		val map = reg.getExtensionToFactoryMap();
		map.put("xmi", new XMIResourceFactoryImpl());

		val resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(UschemaMMPackage.eNS_URI, UschemaMMPackage.eINSTANCE);

		val resource = resourceSet.createResource(URI.createFileURI(outputUri));
		resource.getContents().add(relationalschema);

		resource.save(Collections.EMPTY_MAP);
	}
	
	def static void main(String[] args) {
		val inputFileName = "USchema";
		val outputFileName = "Relational";
		
		val main2 = new Main()
		
		println("Loading USchema from " + USER_DIR + INPUTSDIR + inputFileName + FILE_EXTESION)
		//val entitiesModel = main2.readModel(USER_DIR + INPUTSDIR + inputFileName + FILE_EXTESION)
		
		println("Performing transformation m2m uschema2relational")
		var entities2sql = new MappingUSchema2Relational;
		entities2sql.loadSchema("inputs/USchema.xmi")
		val relationalModel = entities2sql.transformacion()
		//val sqlModel = entities2sql.uschema2relational(entitiesModel)
		//entities2sql.imprimirTraza();
		println("Writing Relational Model to " + USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
		main2.writeModel(relationalModel, USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
		
		
		println("Relational Model created at " + USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
	}
}