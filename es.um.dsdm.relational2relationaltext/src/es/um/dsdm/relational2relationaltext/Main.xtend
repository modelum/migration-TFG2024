package es.um.dsdm.relational2relationaltext

import relationalschema.RelationalSchema
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.common.util.URI
import relationalschema.RelationalMMPackage
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

class Main {

	val static String USER_DIR = System.getProperty("user.dir")
	val static String INPUTSDIR = "/inputs/"
	val static String OUTPUTS_DIR = "/outputs/"
	val static String FILE_EXTESION = ".xmi";
	val static String OUTPUT_FILE_EXTESION = ".sql";

	def RelationalSchema readModel(String inputFileName) {
		val rSet = new ResourceSetImpl()
		rSet.getPackageRegistry().put(RelationalMMPackage.eNS_URI, RelationalMMPackage.eINSTANCE)
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl())
		val uri = 'file:///' + inputFileName
		val r = rSet.getResource(URI.createURI(uri), true)
		return r.getContents().get(0) as RelationalSchema
	}

	def static void main(String[] args) {
		val inputFileName = "RelationalSchema";
		val outputFileName = "InlineCodeCreation";

		println("Loading Relational Model from " + USER_DIR + INPUTSDIR + inputFileName + FILE_EXTESION)
		val main2 = new Main
		val relationalSchema = main2.readModel(USER_DIR + INPUTSDIR + inputFileName + FILE_EXTESION)

		println("Performing transformation m2t")
		val relational2relationalText = new Relational2RelationalText
		relational2relationalText.relational2RelationalText(relationalSchema,
			USER_DIR + OUTPUTS_DIR + outputFileName + OUTPUT_FILE_EXTESION)

		println("SQl File created at " + USER_DIR + OUTPUTS_DIR + outputFileName + FILE_EXTESION)
	}
}
