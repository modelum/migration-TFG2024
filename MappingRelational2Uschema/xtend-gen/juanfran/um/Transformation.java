package juanfran.um;

import com.google.common.base.Objects;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import relationalschema.Column;
import relationalschema.FKey;
import relationalschema.Key;
import relationalschema.RelationalSchema;
import relationalschema.RelationalschemaPackage;
import relationalschema.Table;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.EntityType;
import uschema.Feature;
import uschema.PrimitiveType;
import uschema.RelationshipType;
import uschema.SchemaType;
import uschema.USchema;
import uschema.UschemaFactory;
import uschema.UschemaPackage;

@SuppressWarnings("all")
public class Transformation {
  private static final String INPUT_XMI_PATH = "modelos/RelationalTienda.xmi";

  private static final String OUTPUT_XMI_PATH = "modelos/USchemaTienda.xmi";

  public static String typeConversionRelToUsc(final String dataType) {
    if (dataType != null) {
      switch (dataType) {
        case "VARCHAR":
          return "String";
        case "INT":
          return "int";
        case "DOUBLE":
          return "double";
        case "BOOLEAN":
          return "boolean";
      }
    }
    return null;
  }

  public static Table weakCondition(final Table table) {
    final Function1<Key, Boolean> _function = (Key key) -> {
      boolean _isIsPK = key.isIsPK();
      return Boolean.valueOf((_isIsPK == true));
    };
    final Key pk = IterableExtensions.<Key>findFirst(table.getKeys(), _function);
    if ((pk == null)) {
      return null;
    }
    EList<FKey> _fks = table.getFks();
    for (final FKey fk : _fks) {
      boolean _containsAll = pk.getColumns().containsAll(fk.getColumns());
      if (_containsAll) {
        return fk.getRefsTo().getOwner();
      }
    }
    return null;
  }

  public static EntityType findEntityType(final Table table, final USchema uSchema) {
    final Function1<EntityType, Boolean> _function = (EntityType et) -> {
      String _name = et.getName();
      String _name_1 = table.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    return IterableExtensions.<EntityType>findFirst(uSchema.getEntities(), _function);
  }

  public static boolean r0(final Resource relResource, final Resource uscResource) {
    boolean _xblockexpression = false;
    {
      InputOutput.<String>println("Starting R0...");
      EObject _head = IterableExtensions.<EObject>head(relResource.getContents());
      RelationalSchema relationalSchema = ((RelationalSchema) _head);
      USchema uSchema = UschemaFactory.eINSTANCE.createUSchema();
      uSchema.setName(relationalSchema.getName());
      _xblockexpression = uscResource.getContents().add(uSchema);
    }
    return _xblockexpression;
  }

  public static void r2(final Table table, final SchemaType schemaType) {
    InputOutput.<String>println("Starting R2...");
    EList<Column> _columns = table.getColumns();
    for (final Column column : _columns) {
      {
        Attribute attribute = UschemaFactory.eINSTANCE.createAttribute();
        attribute.setName(column.getName());
        PrimitiveType primitiveType = UschemaFactory.eINSTANCE.createPrimitiveType();
        primitiveType.setName(Transformation.typeConversionRelToUsc(column.getDatatype()));
        attribute.setType(primitiveType);
        attribute.setOptional(column.isNullable());
        schemaType.getFeatures().add(attribute);
      }
    }
  }

  public static String r3(final Table table, final SchemaType schemaType) {
    String _xblockexpression = null;
    {
      InputOutput.<String>println("Starting R3...");
      EList<Key> _keys = table.getKeys();
      for (final Key rKey : _keys) {
        {
          uschema.Key uKey = UschemaFactory.eINSTANCE.createKey();
          uKey.setName(rKey.getConstraintname());
          uKey.setIsID(rKey.isIsPK());
          final Function1<Feature, Boolean> _function = (Feature f) -> {
            return Boolean.valueOf((f instanceof Attribute));
          };
          final Iterable<Feature> attributes = IterableExtensions.<Feature>filter(schemaType.getFeatures(), _function);
          EList<Column> _columns = rKey.getColumns();
          for (final Column column : _columns) {
            {
              final Function1<Feature, Boolean> _function_1 = (Feature feature) -> {
                String _name = feature.getName();
                String _name_1 = column.getName();
                return Boolean.valueOf(Objects.equal(_name, _name_1));
              };
              Feature _findFirst = IterableExtensions.<Feature>findFirst(attributes, _function_1);
              final Attribute attribute = ((Attribute) _findFirst);
              if ((attribute != null)) {
                uKey.getAttributes().add(attribute);
              }
            }
          }
          schemaType.getFeatures().add(uKey);
        }
      }
      InputOutput.<String>print("final features: [");
      final Consumer<Feature> _function = (Feature c) -> {
        String _name = c.getName();
        String _plus = (_name + ", ");
        InputOutput.<String>print(_plus);
      };
      schemaType.getFeatures().forEach(_function);
      _xblockexpression = InputOutput.<String>println("]");
    }
    return _xblockexpression;
  }

  public static void r4(final Table weakTable, final Table strongTable, final EntityType weakEntityType, final USchema uSchema) {
    Aggregate aggregate = UschemaFactory.eINSTANCE.createAggregate();
    String _name = weakTable.getName();
    String _plus = (_name + "s");
    aggregate.setName(_plus);
    aggregate.setLowerBound(0);
    aggregate.setUpperBound(3);
    aggregate.setAggregates(weakEntityType);
    EntityType strongEntityType = Transformation.findEntityType(strongTable, uSchema);
    strongEntityType.getFeatures().add(aggregate);
    weakEntityType.setRoot(false);
    strongEntityType.setRoot(true);
  }

  public static void r1(final Resource relResource, final Resource uscResource) {
    InputOutput.<String>println("Starting R1...");
    EObject _head = IterableExtensions.<EObject>head(relResource.getContents());
    RelationalSchema relationalSchema = ((RelationalSchema) _head);
    EObject _head_1 = IterableExtensions.<EObject>head(uscResource.getContents());
    USchema uSchema = ((USchema) _head_1);
    EList<Table> _tables = relationalSchema.getTables();
    for (final Table table : _tables) {
      {
        String _name = table.getName();
        String _plus = (_name + ": [");
        InputOutput.<String>print(_plus);
        final Consumer<Column> _function = (Column c) -> {
          String _name_1 = c.getName();
          String _plus_1 = (_name_1 + ", ");
          InputOutput.<String>print(_plus_1);
        };
        table.getColumns().forEach(_function);
        InputOutput.<String>println("]");
        SchemaType schemaType = null;
        if (false) {
          schemaType = UschemaFactory.eINSTANCE.createRelationshipType();
          uSchema.getRelationships().add(((RelationshipType) schemaType));
        } else {
          schemaType = UschemaFactory.eINSTANCE.createEntityType();
          uSchema.getEntities().add(((EntityType) schemaType));
        }
        schemaType.setName(table.getName());
        Transformation.r2(table, schemaType);
        Transformation.r3(table, schemaType);
        if ((schemaType instanceof EntityType)) {
          Table strongTable = Transformation.weakCondition(table);
          if ((strongTable != null)) {
            Transformation.r4(table, strongTable, ((EntityType)schemaType), uSchema);
          }
        }
      }
    }
  }

  public static void main(final String[] args) {
    try {
      ResourceSet resourceSet = null;
      Resource relResource = null;
      Resource uscResource = null;
      RelationalschemaPackage.eINSTANCE.eClass();
      UschemaPackage.eINSTANCE.eClass();
      ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
      resourceSet = _resourceSetImpl;
      Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
      XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
      _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
      URI relUri = URI.createFileURI(Transformation.INPUT_XMI_PATH);
      URI uscUri = URI.createFileURI(Transformation.OUTPUT_XMI_PATH);
      relResource = resourceSet.getResource(relUri, true);
      uscResource = resourceSet.createResource(uscUri);
      Transformation.r0(relResource, uscResource);
      Transformation.r1(relResource, uscResource);
      uscResource.save(null);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
