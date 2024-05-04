package juanfran.um;

import documentschema.Array;
import documentschema.DocumentSchema;
import documentschema.DocumentschemaFactory;
import documentschema.PrimitiveType;
import documentschema.Type;
import juanfran.um.trace.Trace;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import uschema.Attribute;
import uschema.DataType;
import uschema.EntityType;
import uschema.Feature;
import uschema.PList;
import uschema.PMap;
import uschema.PSet;
import uschema.PTuple;
import uschema.USchema;

@SuppressWarnings("all")
public class MappingUschema2Document {
  private final DocumentschemaFactory dsFactory;

  private final Trace trace;

  public MappingUschema2Document() {
    this.dsFactory = DocumentschemaFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public DocumentSchema USchema2DocumentSchema(final USchema us) {
    final DocumentSchema ds = this.dsFactory.createDocumentSchema();
    ds.setName(us.getName());
    EList<EntityType> _entities = us.getEntities();
    for (final EntityType e : _entities) {
      boolean _isRoot = e.isRoot();
      if (_isRoot) {
        this.entityType2EntityType(e, ds);
      }
    }
    this.trace.addTrace(us.getName(), us, ds.getName(), ds);
    this.trace.printDirectTraceTypes();
    return ds;
  }

  public void entityType2EntityType(final EntityType e, final DocumentSchema ds) {
    final documentschema.EntityType d = this.dsFactory.createEntityType();
    ds.getEntities().add(d);
    d.setName(e.getName());
    EList<Feature> _features = e.getFeatures();
    for (final Feature f : _features) {
      this.attribute2Attribute(((Attribute) f), d, ds);
    }
    this.trace.addTrace(e.getName(), e, d.getName(), d);
  }

  public void attribute2Attribute(final Attribute f_at, final documentschema.EntityType d, final DocumentSchema ds) {
    final documentschema.Attribute p_at = this.dsFactory.createAttribute();
    d.getProperties().add(p_at);
    p_at.setName(f_at.getName());
    p_at.setType(this.datatype2Type(f_at.getType(), ds));
    InputOutput.<Type>println(p_at.getType());
    this.trace.addTrace(
      MappingUschema2Document.dot(f_at.getOwner().getName(), f_at.getName()), f_at, 
      MappingUschema2Document.dot(d.getName(), p_at.getName()), 
      p_at.getType());
    this.trace.addTrace(
      MappingUschema2Document.dot(f_at.getOwner().getName(), f_at.getName()), f_at, 
      MappingUschema2Document.dot(d.getName(), p_at.getName()), p_at);
  }

  public Type datatype2Type(final DataType dt, final DocumentSchema ds) {
    final PrimitiveType pt = this.dsFactory.createPrimitiveType();
    ds.getTypes().add(pt);
    boolean _matched = false;
    if (dt instanceof uschema.PrimitiveType) {
      _matched=true;
      pt.setDatatype(MappingUschema2Document.typeConversionUsc2Doc(((uschema.PrimitiveType)dt)));
      return pt;
    }
    if (!_matched) {
      if (dt instanceof PList) {
        _matched=true;
        final Array ar = this.dsFactory.createArray();
        DataType _elementType = ((PList)dt).getElementType();
        final uschema.PrimitiveType upt = ((uschema.PrimitiveType) _elementType);
        pt.setDatatype(MappingUschema2Document.typeConversionUsc2Doc(upt));
        ar.setType(pt);
        ds.getTypes().add(ar);
        return ar;
      }
    }
    if (!_matched) {
      if (dt instanceof PSet) {
        _matched=true;
        final Array ar = this.dsFactory.createArray();
        DataType _elementType = ((PSet)dt).getElementType();
        final uschema.PrimitiveType upt = ((uschema.PrimitiveType) _elementType);
        pt.setDatatype(MappingUschema2Document.typeConversionUsc2Doc(upt));
        ar.setType(pt);
        ds.getTypes().add(ar);
        return ar;
      }
    }
    if (!_matched) {
      if (dt instanceof PTuple) {
        _matched=true;
        final Array ar = this.dsFactory.createArray();
        DataType _head = IterableExtensions.<DataType>head(((PTuple)dt).getElements());
        final uschema.PrimitiveType upt = ((uschema.PrimitiveType) _head);
        pt.setDatatype(MappingUschema2Document.typeConversionUsc2Doc(upt));
        ar.setType(pt);
        ds.getTypes().add(ar);
        return ar;
      }
    }
    if (!_matched) {
      if (dt instanceof PMap) {
        _matched=true;
        final Array ar = this.dsFactory.createArray();
        DataType _valueType = ((PMap)dt).getValueType();
        final uschema.PrimitiveType upt = ((uschema.PrimitiveType) _valueType);
        pt.setDatatype(MappingUschema2Document.typeConversionUsc2Doc(upt));
        ar.setType(pt);
        ds.getTypes().add(ar);
        return ar;
      }
    }
    return null;
  }

  public static documentschema.DataType typeConversionUsc2Doc(final uschema.PrimitiveType dt) {
    final String dtUp = dt.getName().toUpperCase();
    if (dtUp != null) {
      switch (dtUp) {
        case "STRING":
          return documentschema.DataType.STRING;
        case "INT":
          return documentschema.DataType.INTEGER;
        case "DOUBLE":
          return documentschema.DataType.DOUBLE;
        case "BOOLEAN":
          return documentschema.DataType.BOOLEAN;
      }
    }
    return null;
  }

  public static String dot(final String... strings) {
    return IterableExtensions.join(((Iterable<?>)Conversions.doWrapArray(strings)), ".");
  }
}
