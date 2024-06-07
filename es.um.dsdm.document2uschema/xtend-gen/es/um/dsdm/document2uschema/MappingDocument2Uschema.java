package es.um.dsdm.document2uschema;

import com.google.common.base.Objects;
import documentschema.Aggregate;
import documentschema.Array;
import documentschema.Attribute;
import documentschema.DocumentMMPackage;
import documentschema.DocumentSchema;
import documentschema.EntityType;
import documentschema.PrimitiveType;
import documentschema.Property;
import documentschema.Reference;
import documentschema.Type;
import es.um.dsdm.trace.Trace;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import uschema.DataType;
import uschema.Key;
import uschema.PList;
import uschema.USchema;
import uschema.UschemaMMFactory;

@SuppressWarnings("all")
public class MappingDocument2Uschema {
  private USchema uSchema;

  private DocumentSchema documentSchema;

  private final UschemaMMFactory uschemaFactory;

  private final Trace trace;

  public MappingDocument2Uschema() {
    this.uschemaFactory = UschemaMMFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public USchema transformacion() {
    this.documentSchema2USchema();
    final Consumer<EntityType> _function = (EntityType e) -> {
      this.entityType2entityType(e);
    };
    this.documentSchema.getEntities().forEach(_function);
    final Consumer<EntityType> _function_1 = (EntityType e) -> {
      final Consumer<Property> _function_2 = (Property p) -> {
        boolean _matched = false;
        if (p instanceof Attribute) {
          _matched=true;
          this.attribute2attribute(((Attribute) p));
        }
        if (!_matched) {
          if (p instanceof Reference) {
            _matched=true;
            this.reference2reference(((Reference) p));
          }
        }
      };
      e.getProperties().forEach(_function_2);
    };
    this.documentSchema.getEntities().forEach(_function_1);
    final Consumer<EntityType> _function_2 = (EntityType e) -> {
      final Consumer<Property> _function_3 = (Property p) -> {
        if ((p instanceof Aggregate)) {
          this.aggregate2entityType(((Aggregate) p));
        }
      };
      e.getProperties().forEach(_function_3);
    };
    this.documentSchema.getEntities().forEach(_function_2);
    this.trace.printDirectTraceTypes();
    return this.uSchema;
  }

  public void documentSchema2USchema() {
    this.uSchema = this.uschemaFactory.createUSchema();
    this.uSchema.setName(this.documentSchema.getName());
    this.trace.addTrace(this.documentSchema.getName(), this.documentSchema, this.uSchema.getName(), this.uSchema);
  }

  public uschema.EntityType entityType2entityType(final EntityType d) {
    boolean _containsKey = this.trace.getDirectTraceString().containsKey(d.getName());
    if (_containsKey) {
      Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(d.getName()));
      return ((uschema.EntityType) _head);
    }
    final uschema.EntityType ed = this.uschemaFactory.createEntityType();
    ed.setName(d.getName());
    ed.setRoot(true);
    this.trace.addTrace(d.getName(), d, ed.getName(), ed);
    this.uSchema.getEntities().add(ed);
    return ed;
  }

  public void attribute2attribute(final Attribute a) {
    final uschema.Attribute ea = this.uschemaFactory.createAttribute();
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(a.getOwner().getName()));
    final uschema.EntityType ed = ((uschema.EntityType) _head);
    ea.setName(a.getName());
    ea.setType(this.type2datatype(a.getType()));
    boolean _isIsKey = a.isIsKey();
    if (_isIsKey) {
      Key k = this.uschemaFactory.createKey();
      k.setName(a.getName());
      k.setIsID(true);
      k.getAttributes().add(ea);
      ea.setKey(k);
      ed.getFeatures().add(k);
      String _name = a.getOwner().getName();
      String _plus = (_name + ".");
      String _name_1 = a.getName();
      String _plus_1 = (_plus + _name_1);
      String _name_2 = k.getOwner().getName();
      String _plus_2 = (_name_2 + ".");
      String _name_3 = k.getName();
      String _plus_3 = (_plus_2 + _name_3);
      String _plus_4 = (_plus_3 + ".key");
      this.trace.addTrace(_plus_1, a, _plus_4, k);
    }
    ed.getFeatures().add(ea);
    String _name_4 = a.getOwner().getName();
    String _plus_5 = (_name_4 + ".");
    String _name_5 = a.getName();
    String _plus_6 = (_plus_5 + _name_5);
    String _name_6 = ea.getOwner().getName();
    String _plus_7 = (_name_6 + ".");
    String _name_7 = ea.getName();
    String _plus_8 = (_plus_7 + _name_7);
    String _plus_9 = (_plus_8 + ".attribute");
    this.trace.addTrace(_plus_6, a, _plus_9, ea);
  }

  public void reference2reference(final Reference r) {
    final uschema.Reference er = this.uschemaFactory.createReference();
    final uschema.Attribute ea = this.uschemaFactory.createAttribute();
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(r.getOwner().getName()));
    final uschema.EntityType ed = ((uschema.EntityType) _head);
    er.setName(r.getName());
    ea.setName(r.getName());
    er.setLowerBound(1);
    int _xifexpression = (int) 0;
    Type _type = r.getType();
    if ((_type instanceof PrimitiveType)) {
      _xifexpression = 1;
    } else {
      _xifexpression = (-1);
    }
    er.setUpperBound(_xifexpression);
    er.getAttributes().add(ea);
    ea.getReferences().add(er);
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(r.getTarget().getName()));
    er.setRefsTo(((uschema.EntityType) _head_1));
    ed.getFeatures().add(er);
    String _name = r.getOwner().getName();
    String _plus = (_name + ".");
    String _name_1 = r.getName();
    String _plus_1 = (_plus + _name_1);
    String _name_2 = er.getOwner().getName();
    String _plus_2 = (_name_2 + ".");
    String _name_3 = er.getName();
    String _plus_3 = (_plus_2 + _name_3);
    String _plus_4 = (_plus_3 + ".reference");
    this.trace.addTrace(_plus_1, r, _plus_4, er);
    ed.getFeatures().add(ea);
    String _name_4 = r.getOwner().getName();
    String _plus_5 = (_name_4 + ".");
    String _name_5 = r.getName();
    String _plus_6 = (_plus_5 + _name_5);
    String _name_6 = ea.getOwner().getName();
    String _plus_7 = (_name_6 + ".");
    String _name_7 = ea.getName();
    String _plus_8 = (_plus_7 + _name_7);
    String _plus_9 = (_plus_8 + ".attribute");
    this.trace.addTrace(_plus_6, r, _plus_9, ea);
  }

  public void aggregate2entityType(final Aggregate g) {
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(g.getOwner().getName()));
    final uschema.EntityType ed = ((uschema.EntityType) _head);
    uschema.EntityType _xifexpression = null;
    boolean _containsKey = this.trace.getDirectTraceString().containsKey(g.getName());
    if (_containsKey) {
      Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(g.getName()));
      _xifexpression = ((uschema.EntityType) _head_1);
    } else {
      _xifexpression = this.uschemaFactory.createEntityType();
    }
    final uschema.EntityType eg = _xifexpression;
    eg.setName(g.getName());
    eg.setRoot(false);
    boolean _containsKey_1 = this.trace.getDirectTraceString().containsKey(g.getName());
    boolean _not = (!_containsKey_1);
    if (_not) {
      this.uSchema.getEntities().add(eg);
      String _name = g.getOwner().getName();
      String _plus = (_name + ".");
      String _name_1 = g.getName();
      String _plus_1 = (_plus + _name_1);
      this.trace.addTrace(_plus_1, g, eg.getName(), eg);
    }
    this.addAggregateFeatures(g, eg);
    final uschema.Aggregate ag = this.uschemaFactory.createAggregate();
    ag.setName(g.getName());
    ag.setLowerBound(1);
    int _xifexpression_1 = (int) 0;
    boolean _isIsMany = g.isIsMany();
    if (_isIsMany) {
      _xifexpression_1 = (-1);
    } else {
      _xifexpression_1 = 1;
    }
    ag.setUpperBound(_xifexpression_1);
    ag.setSpecifiedBy(eg);
    ed.setRoot(true);
    ed.getFeatures().add(ag);
    String _name_2 = g.getOwner().getName();
    String _plus_2 = (_name_2 + ".");
    String _name_3 = g.getName();
    String _plus_3 = (_plus_2 + _name_3);
    String _name_4 = ag.getOwner().getName();
    String _plus_4 = (_name_4 + ".");
    String _name_5 = ag.getName();
    String _plus_5 = (_plus_4 + _name_5);
    String _plus_6 = (_plus_5 + ".aggregate");
    this.trace.addTrace(_plus_3, g, _plus_6, ag);
  }

  public DataType type2datatype(final Type type) {
    boolean _matched = false;
    if (Objects.equal(type, PrimitiveType.class)) {
      _matched=true;
      return this.createPrimitiveType(((PrimitiveType) type));
    }
    if (!_matched) {
      if (Objects.equal(type, Array.class)) {
        _matched=true;
        return this.createArrayType(((Array) type));
      }
    }
    return null;
  }

  private void addAggregateFeatures(final Aggregate g, final uschema.EntityType eg) {
    Property _aggregates = g.getAggregates();
    boolean _tripleNotEquals = (_aggregates != null);
    if (_tripleNotEquals) {
      Property _aggregates_1 = g.getAggregates();
      boolean _matched = false;
      if (_aggregates_1 instanceof Attribute) {
        _matched=true;
        String _name = g.getAggregates().getOwner().getName();
        String _plus = (_name + ".");
        String _name_1 = g.getAggregates().getName();
        String _plus_1 = (_plus + _name_1);
        final Function1<Object, Boolean> _function = (Object e) -> {
          return Boolean.valueOf((e instanceof uschema.Attribute));
        };
        Object _findFirst = IterableExtensions.<Object>findFirst(this.trace.getTargetInstance(_plus_1), _function);
        final uschema.Attribute atributo = ((uschema.Attribute) _findFirst);
        eg.getFeatures().add(atributo);
      }
      if (!_matched) {
        if (_aggregates_1 instanceof Reference) {
          _matched=true;
          String _name = g.getAggregates().getOwner().getName();
          String _plus = (_name + ".");
          String _name_1 = g.getAggregates().getName();
          String _plus_1 = (_plus + _name_1);
          final Function1<Object, Boolean> _function = (Object e) -> {
            return Boolean.valueOf((e instanceof uschema.Reference));
          };
          Object _findFirst = IterableExtensions.<Object>findFirst(this.trace.getTargetInstance(_plus_1), _function);
          final uschema.Reference referencia = ((uschema.Reference) _findFirst);
          eg.getFeatures().add(referencia);
        }
      }
      if (!_matched) {
        if (_aggregates_1 instanceof Aggregate) {
          _matched=true;
          Property _aggregates_2 = g.getAggregates();
          this.aggregate2entityType(((Aggregate) _aggregates_2));
        }
      }
    }
  }

  private uschema.PrimitiveType createPrimitiveType(final PrimitiveType type) {
    final uschema.PrimitiveType et = this.uschemaFactory.createPrimitiveType();
    et.setName(this.mapPrimitiveTypeName(type.getDatatype()));
    this.trace.addTrace(type.getDatatype().getName(), type, et.getName(), et);
    return et;
  }

  private PList createArrayType(final Array type) {
    final PList plist = this.uschemaFactory.createPList();
    plist.setElementType(this.type2datatype(type.getType()));
    DataType _elementType = plist.getElementType();
    final uschema.PrimitiveType primitivo = ((uschema.PrimitiveType) _elementType);
    this.trace.addTrace(type.getType().getDatatype().getName(), type, primitivo.getName(), plist);
    return plist;
  }

  private String mapPrimitiveTypeName(final documentschema.DataType datatype) {
    if (datatype != null) {
      switch (datatype) {
        case BOOLEAN:
          return "BOOLEAN";
        case INTEGER:
          return "INTEGER";
        case DOUBLE:
          return "DOUBLE";
        case STRING:
          return "STRING";
        default:
          break;
      }
    }
    return null;
  }

  public void loadSchema(final String path) {
    ResourceSet resourceSet = null;
    Resource docResource = null;
    URI docUri = URI.createFileURI(path);
    DocumentMMPackage.eINSTANCE.eClass();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    resourceSet = _resourceSetImpl;
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    docResource = resourceSet.getResource(docUri, true);
    EObject _head = IterableExtensions.<EObject>head(docResource.getContents());
    this.documentSchema = ((DocumentSchema) _head);
  }

  public void imprimirTraza() {
    this.trace.printDirectTraceTypes();
  }

  public USchema getUSchema() {
    return this.uSchema;
  }

  public DocumentSchema getDocumentSchema() {
    return this.documentSchema;
  }
}
