package es.um.dsdm.uschema2relational.main;

import es.um.dsdm.uschema2relational.MappingUSchema2Relational;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import relationalschema.RelationalSchema;
import uschema.USchema;
import uschema.UschemaMMPackage;

@SuppressWarnings("all")
public class Main {
  private static final String USER_DIR = System.getProperty("user.dir");

  private static final String INPUTSDIR = "/inputs/";

  private static final String OUTPUTS_DIR = "/outputs/";

  private static final String FILE_EXTESION = ".xmi";

  public USchema readModel(final String inputFileName) {
    final ResourceSetImpl rSet = new ResourceSetImpl();
    rSet.getPackageRegistry().put(UschemaMMPackage.eNS_URI, UschemaMMPackage.eINSTANCE);
    Map<String, Object> _extensionToFactoryMap = rSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    final String uri = ("file:///" + inputFileName);
    final Resource r = rSet.getResource(URI.createURI(uri), true);
    EObject _get = r.getContents().get(0);
    return ((USchema) _get);
  }

  public void writeModel(final RelationalSchema relationalschema, final String outputUri) throws IOException {
    final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    final Map<String, Object> map = reg.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    map.put("xmi", _xMIResourceFactoryImpl);
    final ResourceSetImpl resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(UschemaMMPackage.eNS_URI, UschemaMMPackage.eINSTANCE);
    final Resource resource = resourceSet.createResource(URI.createFileURI(outputUri));
    resource.getContents().add(relationalschema);
    resource.save(Collections.EMPTY_MAP);
  }

  public static void main(final String[] args) {
    try {
      final String inputFileName = "USchema";
      final String outputFileName = "Relational";
      final Main main2 = new Main();
      InputOutput.<String>println((((("Loading USchema from " + Main.USER_DIR) + Main.INPUTSDIR) + inputFileName) + Main.FILE_EXTESION));
      InputOutput.<String>println("Performing transformation m2m uschema2relational");
      MappingUSchema2Relational entities2sql = new MappingUSchema2Relational();
      entities2sql.loadSchema("inputs/USchema.xmi");
      final RelationalSchema relationalModel = entities2sql.transformacion();
      InputOutput.<String>println((((("Writing Relational Model to " + Main.USER_DIR) + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
      main2.writeModel(relationalModel, (((Main.USER_DIR + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
      InputOutput.<String>println((((("Relational Model created at " + Main.USER_DIR) + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
