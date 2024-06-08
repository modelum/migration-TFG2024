package es.um.dsdm.relational2relationaltext;

import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.InputOutput;
import relationalschema.RelationalMMPackage;
import relationalschema.RelationalSchema;

@SuppressWarnings("all")
public class Main {
  private static final String USER_DIR = System.getProperty("user.dir");

  private static final String INPUTSDIR = "/inputs/";

  private static final String OUTPUTS_DIR = "/outputs/";

  private static final String FILE_EXTESION = ".xmi";

  private static final String OUTPUT_FILE_EXTESION = ".sql";

  public RelationalSchema readModel(final String inputFileName) {
    final ResourceSetImpl rSet = new ResourceSetImpl();
    rSet.getPackageRegistry().put(RelationalMMPackage.eNS_URI, RelationalMMPackage.eINSTANCE);
    Map<String, Object> _extensionToFactoryMap = rSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    final String uri = ("file:///" + inputFileName);
    final Resource r = rSet.getResource(URI.createURI(uri), true);
    EObject _get = r.getContents().get(0);
    return ((RelationalSchema) _get);
  }

  public static void main(final String[] args) {
    final String inputFileName = "RelationalSchema";
    final String outputFileName = "InlineCodeCreation";
    InputOutput.<String>println((((("Loading Relational Model from " + Main.USER_DIR) + Main.INPUTSDIR) + inputFileName) + Main.FILE_EXTESION));
    final Main main2 = new Main();
    final RelationalSchema relationalSchema = main2.readModel((((Main.USER_DIR + Main.INPUTSDIR) + inputFileName) + Main.FILE_EXTESION));
    InputOutput.<String>println("Performing transformation m2t");
    final Relational2RelationalText relational2relationalText = new Relational2RelationalText();
    relational2relationalText.relational2RelationalText(relationalSchema, 
      (((Main.USER_DIR + Main.OUTPUTS_DIR) + outputFileName) + Main.OUTPUT_FILE_EXTESION));
    InputOutput.<String>println((((("SQl File created at " + Main.USER_DIR) + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
  }
}
