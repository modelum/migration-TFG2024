package juanfran.um;

import documentschema.Array;
import documentschema.DataType;
import documentschema.DocumentSchema;
import documentschema.DocumentschemaFactory;
import documentschema.PrimitiveType;
import documentschema.Property;
import documentschema.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import juanfran.um.trace.Trace;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.EntityType;
import uschema.Feature;
import uschema.Key;
import uschema.PList;
import uschema.PMap;
import uschema.PSet;
import uschema.PTuple;
import uschema.Reference;
import uschema.RelationshipType;
import uschema.SchemaType;
import uschema.USchema;
import uschema.UschemaPackage;

@SuppressWarnings("all")
public class MappingUschema2Document {
  private USchema uSchema;

  private DocumentSchema documentSchema;

  private final HashMap<DataType, PrimitiveType> docTypes;

  private final HashMap<DataType, Array> docArrayTypes;

  private final DocumentschemaFactory dsFactory;

  private final Trace trace;

  public MappingUschema2Document() {
    HashMap<DataType, PrimitiveType> _hashMap = new HashMap<DataType, PrimitiveType>();
    this.docTypes = _hashMap;
    HashMap<DataType, Array> _hashMap_1 = new HashMap<DataType, Array>();
    this.docArrayTypes = _hashMap_1;
    this.dsFactory = DocumentschemaFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public DocumentSchema executeMapping() {
    this.uSchema2DocumentSchema();
    EList<EntityType> _entities = this.uSchema.getEntities();
    for (final EntityType e : _entities) {
      boolean _isRoot = e.isRoot();
      if (_isRoot) {
        this.entityType2EntityType(e);
      }
    }
    EList<documentschema.EntityType> _entities_1 = this.documentSchema.getEntities();
    for (final documentschema.EntityType d : _entities_1) {
      {
        Object _head = IterableExtensions.<Object>head(this.trace.getSourceInstance(d.getName()));
        final EntityType uet = ((EntityType) _head);
        EList<Feature> _features = uet.getFeatures();
        for (final Feature f : _features) {
          boolean _matched = false;
          if (f instanceof Attribute) {
            _matched=true;
            this.attribute2Attribute(((Attribute)f), d);
          }
        }
        EList<Feature> _features_1 = uet.getFeatures();
        for (final Feature f_1 : _features_1) {
          boolean _matched_1 = false;
          if (f_1 instanceof Key) {
            _matched_1=true;
            boolean _isIsID = ((Key)f_1).isIsID();
            if (_isIsID) {
              this.key2Attribute(((Key)f_1), d);
            }
          }
        }
      }
    }
    EList<documentschema.EntityType> _entities_2 = this.documentSchema.getEntities();
    for (final documentschema.EntityType d_1 : _entities_2) {
      {
        Object _head = IterableExtensions.<Object>head(this.trace.getSourceInstance(d_1.getName()));
        final EntityType uet = ((EntityType) _head);
        EList<Feature> _features = uet.getFeatures();
        for (final Feature f : _features) {
          boolean _matched = false;
          if (f instanceof Aggregate) {
            _matched=true;
            this.aggregate2Aggregate(((Aggregate)f), d_1);
          }
          if (!_matched) {
            if (f instanceof Reference) {
              _matched=true;
              this.reference2Reference(((Reference)f), d_1);
            }
          }
        }
      }
    }
    EList<RelationshipType> _relationships = this.uSchema.getRelationships();
    for (final RelationshipType rt : _relationships) {
      this.relationshipType2EntityType(rt);
    }
    this.trace.printDirectTraceTypes();
    return this.documentSchema;
  }

  public void uSchema2DocumentSchema() {
    this.documentSchema = this.dsFactory.createDocumentSchema();
    this.createPrimitiveTypes();
    this.createArrays();
    this.documentSchema.setName(this.uSchema.getName());
    this.trace.addTrace(this.uSchema.getName(), this.uSchema, this.documentSchema.getName(), this.documentSchema);
  }

  public void entityType2EntityType(final EntityType e) {
    final documentschema.EntityType d = this.dsFactory.createEntityType();
    d.setName(e.getName());
    this.documentSchema.getEntities().add(d);
    this.trace.addTrace(e.getName(), e, d.getName(), d);
  }

  public void attribute2Attribute(final Attribute f_at, final documentschema.EntityType d) {
    final documentschema.Attribute p_at = this.dsFactory.createAttribute();
    p_at.setName(f_at.getName());
    p_at.setType(this.datatype2Type(f_at.getType()));
    d.getProperties().add(p_at);
    if ((((f_at.getKey() != null) && (f_at.getKey().getAttributes().size() == 1)) && f_at.getKey().isIsID())) {
      String _name = p_at.getName();
      String _plus = (_name + "_id");
      p_at.setName(_plus);
      p_at.setIsKey(true);
    }
    String _name_1 = f_at.getOwner().getName();
    String _plus_1 = (_name_1 + ".");
    String _name_2 = f_at.getName();
    String _plus_2 = (_plus_1 + _name_2);
    String _name_3 = p_at.getOwner().getName();
    String _plus_3 = (_name_3 + ".");
    String _name_4 = p_at.getName();
    String _plus_4 = (_plus_3 + _name_4);
    this.trace.addTrace(_plus_2, f_at, _plus_4, p_at);
  }

  public void attribute2Attribute(final Attribute f_at, final documentschema.Aggregate ag) {
    final documentschema.Attribute p_at = this.dsFactory.createAttribute();
    p_at.setName(f_at.getName());
    p_at.setType(this.datatype2Type(f_at.getType()));
    ag.getAggregates().add(p_at);
    if ((((f_at.getKey() != null) && (f_at.getKey().getAttributes().size() == 1)) && f_at.getKey().isIsID())) {
      String _name = p_at.getName();
      String _plus = (_name + "_id");
      p_at.setName(_plus);
      p_at.setIsKey(true);
    }
    String _aggregateRecursiveTraceName = this.getAggregateRecursiveTraceName(ag);
    String _plus_1 = (_aggregateRecursiveTraceName + ".");
    String _name_1 = p_at.getName();
    String p_atTraceName = (_plus_1 + _name_1);
    String _name_2 = f_at.getOwner().getName();
    String _plus_2 = (_name_2 + ".");
    String _name_3 = f_at.getName();
    String _plus_3 = (_plus_2 + _name_3);
    this.trace.addTrace(_plus_3, f_at, p_atTraceName, p_at);
  }

  public void aggregate2Aggregate(final Aggregate f_ag, final documentschema.EntityType d) {
    final documentschema.Aggregate p_ag = this.dsFactory.createAggregate();
    SchemaType _specifiedBy = f_ag.getSpecifiedBy();
    final EntityType uet = ((EntityType) _specifiedBy);
    d.getProperties().add(p_ag);
    p_ag.setName(f_ag.getName());
    if (((f_ag.getUpperBound() > 1) || (f_ag.getUpperBound() == (-1)))) {
      p_ag.setIsMany(true);
    } else {
      int _upperBound = f_ag.getUpperBound();
      boolean _equals = (_upperBound == 1);
      if (_equals) {
        p_ag.setIsMany(false);
      }
    }
    String _name = f_ag.getOwner().getName();
    String _plus = (_name + ".");
    String _name_1 = f_ag.getName();
    String _plus_1 = (_plus + _name_1);
    String _name_2 = d.getName();
    String _plus_2 = (_name_2 + ".");
    String _name_3 = p_ag.getName();
    String _plus_3 = (_plus_2 + _name_3);
    this.trace.addTrace(_plus_1, f_ag, _plus_3, p_ag);
    this.features2Properties(uet, p_ag);
  }

  public void aggregate2Aggregate(final Aggregate f_ag, final documentschema.Aggregate ag) {
    final documentschema.Aggregate p_ag = this.dsFactory.createAggregate();
    SchemaType _specifiedBy = f_ag.getSpecifiedBy();
    final EntityType uet = ((EntityType) _specifiedBy);
    ag.getAggregates().add(p_ag);
    p_ag.setName(f_ag.getName());
    if (((f_ag.getUpperBound() > 1) || (f_ag.getUpperBound() == (-1)))) {
      p_ag.setIsMany(true);
    } else {
      int _upperBound = f_ag.getUpperBound();
      boolean _equals = (_upperBound == 1);
      if (_equals) {
        p_ag.setIsMany(false);
      }
    }
    final String p_agTraceName = this.getAggregateRecursiveTraceName(p_ag);
    String _name = f_ag.getOwner().getName();
    String _plus = (_name + ".");
    String _name_1 = f_ag.getName();
    String _plus_1 = (_plus + _name_1);
    this.trace.addTrace(_plus_1, f_ag, p_agTraceName, p_ag);
    this.features2Properties(uet, p_ag);
  }

  public void key2Attribute(final Key f_key, final documentschema.EntityType d) {
    int _size = f_key.getAttributes().size();
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      final documentschema.Attribute p_at = this.dsFactory.createAttribute();
      String _name = d.getName();
      String _plus = (_name + "_id");
      p_at.setName(_plus);
      p_at.setType(this.docTypes.get(DataType.STRING));
      p_at.setIsKey(true);
      d.getProperties().add(p_at);
      EList<Attribute> _attributes = f_key.getAttributes();
      for (final Attribute uAt : _attributes) {
        String _name_1 = uAt.getOwner().getName();
        String _plus_1 = (_name_1 + ".");
        String _name_2 = uAt.getName();
        String _plus_2 = (_plus_1 + _name_2);
        String _name_3 = p_at.getOwner().getName();
        String _plus_3 = (_name_3 + ".");
        String _name_4 = p_at.getName();
        String _plus_4 = (_plus_3 + _name_4);
        this.trace.addTrace(_plus_2, uAt, _plus_4, p_at);
      }
    }
  }

  public void key2Attribute(final Key f_key, final documentschema.Aggregate ag) {
    int _size = f_key.getAttributes().size();
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      final documentschema.Attribute p_at = this.dsFactory.createAttribute();
      String _name = ag.getName();
      String _plus = (_name + "_id");
      p_at.setName(_plus);
      p_at.setType(this.docTypes.get(DataType.STRING));
      p_at.setIsKey(true);
      ag.getAggregates().add(p_at);
      String _aggregateRecursiveTraceName = this.getAggregateRecursiveTraceName(ag);
      String _plus_1 = (_aggregateRecursiveTraceName + ".");
      String _name_1 = p_at.getName();
      String p_atTraceName = (_plus_1 + _name_1);
      EList<Attribute> _attributes = f_key.getAttributes();
      for (final Attribute uAt : _attributes) {
        String _name_2 = uAt.getOwner().getName();
        String _plus_2 = (_name_2 + ".");
        String _name_3 = uAt.getName();
        String _plus_3 = (_plus_2 + _name_3);
        this.trace.addTrace(_plus_3, uAt, p_atTraceName, p_at);
      }
    }
  }

  public void reference2Reference(final Reference f_ref, final documentschema.EntityType d) {
    RelationshipType _isFeaturedBy = f_ref.getIsFeaturedBy();
    boolean _tripleNotEquals = (_isFeaturedBy != null);
    if (_tripleNotEquals) {
      return;
    }
    final documentschema.Reference p_ref = this.dsFactory.createReference();
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(f_ref.getRefsTo().getName()));
    final documentschema.EntityType target = ((documentschema.EntityType) _head);
    Type _type = this.findAttributeKey(target).getType();
    final PrimitiveType primitiveType = ((PrimitiveType) _type);
    p_ref.setName(f_ref.getName());
    p_ref.setTarget(target);
    int _upperBound = f_ref.getUpperBound();
    boolean _equals = (_upperBound == 1);
    if (_equals) {
      p_ref.setType(primitiveType);
    } else {
      if (((f_ref.getUpperBound() == (-1)) || (f_ref.getUpperBound() > 1))) {
        final Array array = this.docArrayTypes.get(primitiveType.getDatatype());
        array.setType(primitiveType);
        p_ref.setType(array);
      }
    }
    d.getProperties().add(p_ref);
    String _name = f_ref.getOwner().getName();
    String _plus = (_name + ".");
    String _name_1 = f_ref.getName();
    String _plus_1 = (_plus + _name_1);
    String _name_2 = p_ref.getOwner().getName();
    String _plus_2 = (_name_2 + ".");
    String _name_3 = p_ref.getName();
    String _plus_3 = (_plus_2 + _name_3);
    this.trace.addTrace(_plus_1, f_ref, _plus_3, p_ref);
  }

  public void reference2Reference(final Reference f_ref, final documentschema.Aggregate ag) {
    RelationshipType _isFeaturedBy = f_ref.getIsFeaturedBy();
    boolean _tripleNotEquals = (_isFeaturedBy != null);
    if (_tripleNotEquals) {
      return;
    }
    final documentschema.Reference p_ref = this.dsFactory.createReference();
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(f_ref.getRefsTo().getName()));
    final documentschema.EntityType target = ((documentschema.EntityType) _head);
    Type _type = this.findAttributeKey(target).getType();
    final PrimitiveType primitiveType = ((PrimitiveType) _type);
    p_ref.setName(f_ref.getName());
    p_ref.setTarget(target);
    int _upperBound = f_ref.getUpperBound();
    boolean _equals = (_upperBound == 1);
    if (_equals) {
      p_ref.setType(primitiveType);
    } else {
      if (((f_ref.getUpperBound() == (-1)) || (f_ref.getUpperBound() > 1))) {
        final Array array = this.docArrayTypes.get(primitiveType.getDatatype());
        array.setType(primitiveType);
        p_ref.setType(array);
      }
    }
    ag.getAggregates().add(p_ref);
    String _aggregateRecursiveTraceName = this.getAggregateRecursiveTraceName(ag);
    String _plus = (_aggregateRecursiveTraceName + ".");
    String _name = p_ref.getName();
    final String p_refTraceName = (_plus + _name);
    String _name_1 = f_ref.getOwner().getName();
    String _plus_1 = (_name_1 + ".");
    String _name_2 = f_ref.getName();
    String _plus_2 = (_plus_1 + _name_2);
    this.trace.addTrace(_plus_2, f_ref, p_refTraceName, p_ref);
  }

  public void relationshipType2EntityType(final RelationshipType rt) {
    final documentschema.EntityType c = this.dsFactory.createEntityType();
    c.setName(rt.getName());
    this.documentSchema.getEntities().add(c);
    this.trace.addTrace(rt.getName(), rt, c.getName(), c);
    EList<Feature> _features = rt.getFeatures();
    for (final Feature f : _features) {
      boolean _matched = false;
      if (f instanceof Attribute) {
        _matched=true;
        this.attribute2Attribute(((Attribute)f), c);
      }
    }
    final documentschema.Attribute at = this.dsFactory.createAttribute();
    String _name = c.getName();
    String _plus = (_name + "_id");
    at.setName(_plus);
    at.setIsKey(true);
    at.setType(this.docTypes.get(DataType.STRING));
    c.getProperties().add(at);
    String _name_1 = rt.getName();
    String _name_2 = at.getOwner().getName();
    String _plus_1 = (_name_2 + ".");
    String _name_3 = at.getName();
    String _plus_2 = (_plus_1 + _name_3);
    this.trace.addTrace(_name_1, rt, _plus_2, at);
    EList<Reference> _reference = rt.getReference();
    for (final Reference r : _reference) {
      {
        final documentschema.Reference rf = this.dsFactory.createReference();
        Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(r.getRefsTo().getName()));
        final documentschema.EntityType refsTo = ((documentschema.EntityType) _head);
        rf.setName(r.getName());
        rf.setTarget(refsTo);
        Type _type = this.findAttributeKey(rf.getTarget()).getType();
        rf.setType(((PrimitiveType) _type));
        c.getProperties().add(rf);
        String _name_4 = r.getOwner().getName();
        String _plus_3 = (_name_4 + ".");
        String _name_5 = r.getName();
        String _plus_4 = (_plus_3 + _name_5);
        String _name_6 = rf.getOwner().getName();
        String _plus_5 = (_name_6 + ".");
        String _name_7 = rf.getName();
        String _plus_6 = (_plus_5 + _name_7);
        this.trace.addTrace(_plus_4, r, _plus_6, rf);
      }
    }
  }

  public Type datatype2Type(final uschema.DataType dt) {
    if ((dt instanceof uschema.PrimitiveType)) {
      return this.primitiveTypeConversionUsc2Doc(((uschema.PrimitiveType)dt));
    } else {
      uschema.PrimitiveType upt = null;
      boolean _matched = false;
      if (dt instanceof PList) {
        _matched=true;
        uschema.DataType _elementType = ((PList)dt).getElementType();
        upt = ((uschema.PrimitiveType) _elementType);
      }
      if (!_matched) {
        if (dt instanceof PSet) {
          _matched=true;
          uschema.DataType _elementType = ((PSet)dt).getElementType();
          upt = ((uschema.PrimitiveType) _elementType);
        }
      }
      if (!_matched) {
        if (dt instanceof PTuple) {
          _matched=true;
          uschema.DataType _head = IterableExtensions.<uschema.DataType>head(((PTuple)dt).getElements());
          upt = ((uschema.PrimitiveType) _head);
        }
      }
      if (!_matched) {
        if (dt instanceof PMap) {
          _matched=true;
          uschema.DataType _valueType = ((PMap)dt).getValueType();
          upt = ((uschema.PrimitiveType) _valueType);
        }
      }
      final PrimitiveType pt = this.primitiveTypeConversionUsc2Doc(upt);
      return this.docArrayTypes.get(pt.getDatatype());
    }
  }

  public void features2Properties(final EntityType uet, final documentschema.Aggregate ag) {
    EList<Feature> _features = uet.getFeatures();
    for (final Feature f : _features) {
      boolean _matched = false;
      if (f instanceof Attribute) {
        _matched=true;
        this.attribute2Attribute(((Attribute)f), ag);
      }
      if (!_matched) {
        if (f instanceof Aggregate) {
          _matched=true;
          this.aggregate2Aggregate(((Aggregate)f), ag);
        }
      }
    }
    EList<Feature> _features_1 = uet.getFeatures();
    for (final Feature f_1 : _features_1) {
      boolean _matched_1 = false;
      if (f_1 instanceof Key) {
        _matched_1=true;
        boolean _isIsID = ((Key)f_1).isIsID();
        if (_isIsID) {
          this.key2Attribute(((Key)f_1), ag);
        }
      }
    }
    EList<Feature> _features_2 = uet.getFeatures();
    for (final Feature f_2 : _features_2) {
      boolean _matched_2 = false;
      if (f_2 instanceof Reference) {
        _matched_2=true;
        this.reference2Reference(((Reference)f_2), ag);
      }
    }
  }

  public String getAggregateRecursiveTraceName(final documentschema.Aggregate g) {
    documentschema.Aggregate ag = g;
    String docAgTraceName = ag.getName();
    boolean exit = false;
    while ((!exit)) {
      documentschema.EntityType _owner = ag.getOwner();
      boolean _tripleNotEquals = (_owner != null);
      if (_tripleNotEquals) {
        String _docAgTraceName = docAgTraceName;
        String _name = ag.getOwner().getName();
        String _plus = (_name + ".");
        String _plus_1 = (_plus + docAgTraceName);
        docAgTraceName = (_docAgTraceName + _plus_1);
        exit = true;
      } else {
        String _docAgTraceName_1 = docAgTraceName;
        String _name_1 = ag.getAggregatedBy().getName();
        String _plus_2 = (_name_1 + ".");
        String _plus_3 = (_plus_2 + docAgTraceName);
        docAgTraceName = (_docAgTraceName_1 + _plus_3);
        ag = ag.getAggregatedBy();
      }
    }
    return docAgTraceName;
  }

  public PrimitiveType createPrimitiveTypes() {
    PrimitiveType _xblockexpression = null;
    {
      final PrimitiveType string = this.dsFactory.createPrimitiveType();
      final PrimitiveType integer = this.dsFactory.createPrimitiveType();
      final PrimitiveType doubl = this.dsFactory.createPrimitiveType();
      final PrimitiveType bool = this.dsFactory.createPrimitiveType();
      string.setDatatype(DataType.STRING);
      integer.setDatatype(DataType.INTEGER);
      doubl.setDatatype(DataType.DOUBLE);
      bool.setDatatype(DataType.BOOLEAN);
      this.documentSchema.getTypes().addAll(List.<PrimitiveType>of(string, integer, doubl, bool));
      this.docTypes.put(string.getDatatype(), string);
      this.docTypes.put(integer.getDatatype(), integer);
      this.docTypes.put(doubl.getDatatype(), doubl);
      _xblockexpression = this.docTypes.put(bool.getDatatype(), bool);
    }
    return _xblockexpression;
  }

  public Array createArrays() {
    Array _xblockexpression = null;
    {
      final PrimitiveType string = this.docTypes.get(DataType.STRING);
      final PrimitiveType integer = this.docTypes.get(DataType.INTEGER);
      final PrimitiveType doubl = this.docTypes.get(DataType.DOUBLE);
      final PrimitiveType bool = this.docTypes.get(DataType.BOOLEAN);
      final Array arrayString = this.dsFactory.createArray();
      final Array arrayInteger = this.dsFactory.createArray();
      final Array arrayDouble = this.dsFactory.createArray();
      final Array arrayBoolean = this.dsFactory.createArray();
      arrayString.setType(string);
      arrayInteger.setType(integer);
      arrayDouble.setType(doubl);
      arrayBoolean.setType(bool);
      this.documentSchema.getTypes().addAll(List.<Array>of(arrayString, arrayInteger, arrayDouble, arrayBoolean));
      this.docArrayTypes.put(string.getDatatype(), arrayString);
      this.docArrayTypes.put(integer.getDatatype(), arrayInteger);
      this.docArrayTypes.put(doubl.getDatatype(), arrayDouble);
      _xblockexpression = this.docArrayTypes.put(bool.getDatatype(), arrayBoolean);
    }
    return _xblockexpression;
  }

  public PrimitiveType primitiveTypeConversionUsc2Doc(final uschema.PrimitiveType uDt) {
    DataType docDt = null;
    final String uDtUp = uDt.getName().toUpperCase();
    if (uDtUp != null) {
      switch (uDtUp) {
        case "STRING":
          docDt = DataType.STRING;
          break;
        case "INT":
          docDt = DataType.INTEGER;
          break;
        case "DOUBLE":
          docDt = DataType.DOUBLE;
          break;
        case "BOOLEAN":
          docDt = DataType.BOOLEAN;
          break;
        case "DATE":
          docDt = DataType.STRING;
          break;
      }
    }
    return this.docTypes.get(docDt);
  }

  public documentschema.Attribute findAttributeKey(final documentschema.EntityType et) {
    final Function1<Property, Boolean> _function = (Property p) -> {
      return Boolean.valueOf(((p instanceof documentschema.Attribute) && 
        ((documentschema.Attribute) p).isIsKey()));
    };
    Property _findFirst = IterableExtensions.<Property>findFirst(et.getProperties(), _function);
    return ((documentschema.Attribute) _findFirst);
  }

  public void loadSchema(final String path) {
    ResourceSet resourceSet = null;
    Resource uscResource = null;
    URI uscUri = URI.createFileURI(path);
    UschemaPackage.eINSTANCE.eClass();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    resourceSet = _resourceSetImpl;
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    uscResource = resourceSet.getResource(uscUri, true);
    EObject _head = IterableExtensions.<EObject>head(uscResource.getContents());
    this.uSchema = ((USchema) _head);
  }

  public void saveSchema(final String output) {
    try {
      if ((this.documentSchema != null)) {
        ResourceSet resourceSet = null;
        Resource docResource = null;
        URI docUri = URI.createFileURI(output);
        UschemaPackage.eINSTANCE.eClass();
        ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
        resourceSet = _resourceSetImpl;
        Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
        XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
        _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
        docResource = resourceSet.createResource(docUri);
        docResource.getContents().add(this.documentSchema);
        docResource.save(null);
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public USchema getUSchema() {
    return this.uSchema;
  }

  public DocumentSchema getDocumentSchema() {
    return this.documentSchema;
  }

  public Trace getTrace() {
    return this.trace;
  }

  public HashMap<DataType, Array> getDocArrayTypes() {
    return this.docArrayTypes;
  }
}
