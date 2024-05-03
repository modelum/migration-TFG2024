package juanfran.um;

import documentschema.DocumentSchema;
import documentschema.DocumentschemaPackage;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import uschema.USchema;
import uschema.UschemaPackage;

@SuppressWarnings("all")
public class Main {
  private static final String INPUT_XMI_PATH = "input-files/USchema_1.xmi";

  private static final String OUTPUT_XMI_PATH = "output-files/Document_1.xmi";

  public static USchema loadSchema() {
    ResourceSet resourceSet = null;
    Resource uscResource = null;
    URI uscUri = URI.createFileURI(Main.INPUT_XMI_PATH);
    UschemaPackage.eINSTANCE.eClass();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    resourceSet = _resourceSetImpl;
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    uscResource = resourceSet.getResource(uscUri, true);
    EObject _head = IterableExtensions.<EObject>head(uscResource.getContents());
    return ((USchema) _head);
  }

  public static void saveSchema(final DocumentSchema ds) {
    try {
      ResourceSet resourceSet = null;
      Resource docResource = null;
      URI docUri = URI.createFileURI(Main.OUTPUT_XMI_PATH);
      DocumentschemaPackage.eINSTANCE.eClass();
      ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
      resourceSet = _resourceSetImpl;
      Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
      XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
      _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
      docResource = resourceSet.createResource(docUri);
      docResource.getContents().add(ds);
      docResource.save(null);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public static void main(final String[] args) {
    final USchema us = Main.loadSchema();
    final MappingUschema2Document mapping = new MappingUschema2Document();
    final DocumentSchema ds = mapping.USchema2DocumentSchema(us);
    Main.saveSchema(ds);
  }
}
