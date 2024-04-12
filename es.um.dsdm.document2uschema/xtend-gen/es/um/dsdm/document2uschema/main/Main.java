package es.um.dsdm.document2uschema.main;

import USchema.USchemaClass;
import USchema.UschemaMMPackage;
import documentschema.DocumentMMPackage;
import documentschema.DocumentSchema;
import es.um.dsdm.document2uschema.MappingDocument2Uschema;
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

@SuppressWarnings("all")
public class Main {
  private static final String USER_DIR = System.getProperty("user.dir");

  private static final String INPUTSDIR = "/inputs/";

  private static final String OUTPUTS_DIR = "/outputs/";

  private static final String FILE_EXTESION = ".xmi";

  public DocumentSchema readModel(final String inputFileName) {
    final ResourceSetImpl rSet = new ResourceSetImpl();
    rSet.getPackageRegistry().put(DocumentMMPackage.eNS_URI, DocumentMMPackage.eINSTANCE);
    Map<String, Object> _extensionToFactoryMap = rSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    final String uri = ("file:///" + inputFileName);
    final Resource r = rSet.getResource(URI.createURI(uri), true);
    EObject _get = r.getContents().get(0);
    return ((DocumentSchema) _get);
  }

  public void writeModel(final USchemaClass uschema, final String outputUri) throws IOException {
    final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    final Map<String, Object> map = reg.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    map.put("xmi", _xMIResourceFactoryImpl);
    final ResourceSetImpl resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(UschemaMMPackage.eNS_URI, UschemaMMPackage.eINSTANCE);
    final Resource resource = resourceSet.createResource(URI.createFileURI(outputUri));
    resource.getContents().add(uschema);
    resource.save(Collections.EMPTY_MAP);
  }

  public static void main(final String[] args) {
    try {
      final String inputFileName = "DocumentSchema-Regla4";
      final String outputFileName = "USchema-Regla4";
      final Main main2 = new Main();
      InputOutput.<String>println((((("Loading DocumentSchema from " + Main.USER_DIR) + Main.INPUTSDIR) + inputFileName) + Main.FILE_EXTESION));
      final DocumentSchema entitiesModel = main2.readModel((((Main.USER_DIR + Main.INPUTSDIR) + inputFileName) + Main.FILE_EXTESION));
      InputOutput.<String>println("Performing transformation m2m document2uschema");
      MappingDocument2Uschema entities2sql = new MappingDocument2Uschema();
      final USchemaClass sqlModel = entities2sql.document2uschema(entitiesModel);
      InputOutput.<String>println((((("Writing USchema Model to " + Main.USER_DIR) + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
      main2.writeModel(sqlModel, (((Main.USER_DIR + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
      InputOutput.<String>println((((("USchema Model created at " + Main.USER_DIR) + Main.OUTPUTS_DIR) + outputFileName) + Main.FILE_EXTESION));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
