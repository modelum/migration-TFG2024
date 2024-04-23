package juanfran.um;

import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import relationalschema.RelationalSchema;
import relationalschema.RelationalschemaPackage;
import uschema.USchema;
import uschema.UschemaPackage;

@SuppressWarnings("all")
public class Main {
  private static final String INPUT_XMI_PATH = "input-files/Relational_6-1_N.xmi";

  private static final String OUTPUT_XMI_PATH = "output-files/USchema_6-1_N.xmi";

  public static RelationalSchema loadSchema() {
    ResourceSet resourceSet = null;
    Resource relResource = null;
    URI relUri = URI.createFileURI(Main.INPUT_XMI_PATH);
    RelationalschemaPackage.eINSTANCE.eClass();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    resourceSet = _resourceSetImpl;
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    relResource = resourceSet.getResource(relUri, true);
    EObject _head = IterableExtensions.<EObject>head(relResource.getContents());
    return ((RelationalSchema) _head);
  }

  public static void saveSchema(final USchema us) {
    try {
      ResourceSet resourceSet = null;
      Resource uscResource = null;
      URI uscUri = URI.createFileURI(Main.OUTPUT_XMI_PATH);
      UschemaPackage.eINSTANCE.eClass();
      ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
      resourceSet = _resourceSetImpl;
      Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
      XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
      _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
      uscResource = resourceSet.createResource(uscUri);
      uscResource.getContents().add(us);
      uscResource.save(null);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public static void main(final String[] args) {
    final RelationalSchema rs = Main.loadSchema();
    final MappingRelational2Uschema mapping = new MappingRelational2Uschema();
    final USchema us = mapping.relationalSchema2USchema(rs);
    Main.saveSchema(us);
  }
}
