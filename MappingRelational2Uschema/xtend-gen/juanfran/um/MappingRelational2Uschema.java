package juanfran.um;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import juanfran.um.trace.Trace;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import relationalschema.Column;
import relationalschema.FKey;
import relationalschema.Key;
import relationalschema.RelationalSchema;
import relationalschema.Table;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.EntityType;
import uschema.PrimitiveType;
import uschema.Reference;
import uschema.RelationshipType;
import uschema.SchemaType;
import uschema.USchema;
import uschema.UschemaFactory;

@SuppressWarnings("all")
public class MappingRelational2Uschema {
  private final UschemaFactory usFactory;

  private final Trace trace;

  public MappingRelational2Uschema() {
    this.usFactory = UschemaFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public USchema relationalSchema2USchema(final RelationalSchema relationalSchema) {
    final USchema uSchema = this.usFactory.createUSchema();
    uSchema.setName(relationalSchema.getName());
    final Consumer<Table> _function = (Table t) -> {
      this.table2SchemaType(t, uSchema);
    };
    relationalSchema.getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      boolean _weakCondition = this.weakCondition(t);
      if (_weakCondition) {
        this.weakTable2Aggregate(t);
      } else {
        boolean _mNCondition = this.mNCondition(t);
        if (_mNCondition) {
          this.mNTable2RelationshipType(t);
        } else {
          this.r6(t);
        }
      }
    };
    relationalSchema.getTables().forEach(_function_1);
    this.trace.addTrace(relationalSchema.getName(), relationalSchema, uSchema.getName(), uSchema);
    this.trace.printDirectTraceTypes();
    return uSchema;
  }

  public void table2SchemaType(final Table t, final USchema uSchema) {
    SchemaType st = null;
    boolean _mNCondition = this.mNCondition(t);
    if (_mNCondition) {
      st = this.usFactory.createRelationshipType();
      uSchema.getRelationships().add(((RelationshipType) st));
    } else {
      final EntityType et = this.usFactory.createEntityType();
      et.setRoot(true);
      uSchema.getEntities().add(et);
      st = et;
    }
    st.setName(t.getName());
    EList<Column> _columns = t.getColumns();
    for (final Column c : _columns) {
      this.column2Attribute(c, st);
    }
    EList<Key> _keys = t.getKeys();
    for (final Key k : _keys) {
      this.pK2Key(k, st);
    }
    this.trace.addTrace(t.getName(), t, st.getName(), st);
  }

  public void column2Attribute(final Column c, final SchemaType st) {
    final Attribute at = this.usFactory.createAttribute();
    st.getFeatures().add(at);
    at.setName(c.getName());
    this.datatype2PrimitiveType(c, at);
    at.setOptional(c.isNullable());
    this.trace.addTrace(MappingRelational2Uschema.dot(c.getOwner().getName(), c.getName()), c, MappingRelational2Uschema.dot(at.getOwner().getName(), at.getName()), at);
  }

  public void pK2Key(final Key rKey, final SchemaType st) {
    final uschema.Key uKey = this.usFactory.createKey();
    st.getFeatures().add(uKey);
    uKey.setName(rKey.getConstraintname());
    uKey.setIsID(rKey.isIsPK());
    uKey.getAttributes().addAll(this.columns2Attributes(rKey.getColumns()));
    this.trace.addTrace(
      MappingRelational2Uschema.dot(rKey.getOwner().getName(), rKey.getConstraintname()), rKey, 
      MappingRelational2Uschema.dot(uKey.getOwner().getName(), uKey.getName()), uKey);
  }

  public void weakTable2Aggregate(final Table w) {
    final FKey fk = IterableExtensions.<FKey>head(this.getFKsInPK(w));
    final Table s = fk.getRefsTo().getOwner();
    Object _targetInstance = this.trace.getTargetInstance(s.getName(), EntityType.class.getName());
    final EntityType es = ((EntityType) _targetInstance);
    Object _targetInstance_1 = this.trace.getTargetInstance(w.getName(), EntityType.class.getName());
    final EntityType ew = ((EntityType) _targetInstance_1);
    final Aggregate ag = this.usFactory.createAggregate();
    es.getFeatures().add(ag);
    String _name = w.getName();
    String _plus = (_name + "s");
    ag.setName(_plus);
    ag.setLowerBound(0);
    ag.setUpperBound((-1));
    ag.setSpecifiedBy(ew);
    ew.setRoot(false);
    this.trace.addTrace(
      MappingRelational2Uschema.dot(fk.getOwner().getName(), fk.getConstraintname()), fk, 
      MappingRelational2Uschema.dot(ag.getOwner().getName(), ag.getName()), ag);
  }

  public void mNTable2RelationshipType(final Table m) {
    Object _targetInstance = this.trace.getTargetInstance(m.getName(), RelationshipType.class.getName());
    final RelationshipType rm = ((RelationshipType) _targetInstance);
    final List<FKey> fKs = this.getFKsInPK(m);
    final FKey fK1 = fKs.get(0);
    final FKey fK2 = fKs.get(1);
    final Table t1 = fK1.getRefsTo().getOwner();
    final Table t2 = fK2.getRefsTo().getOwner();
    Object _targetInstance_1 = this.trace.getTargetInstance(t1.getName(), EntityType.class.getName());
    final EntityType et1 = ((EntityType) _targetInstance_1);
    Object _targetInstance_2 = this.trace.getTargetInstance(t2.getName(), EntityType.class.getName());
    final EntityType et2 = ((EntityType) _targetInstance_2);
    final Reference ref1 = this.usFactory.createReference();
    final Reference ref2 = this.usFactory.createReference();
    ref1.setName(t1.getName());
    ref1.setLowerBound(1);
    ref1.setUpperBound(1);
    ref1.setRefsTo(et1);
    ref1.setIsFeaturedBy(rm);
    ref1.setOwner(et1);
    ref1.getAttributes().addAll(this.columns2Attributes(fK1.getColumns()));
    ref2.setName(t2.getName());
    ref2.setLowerBound(1);
    ref2.setUpperBound(1);
    ref2.setRefsTo(et2);
    ref2.setIsFeaturedBy(rm);
    ref2.setOwner(et2);
    ref2.getAttributes().addAll(this.columns2Attributes(fK2.getColumns()));
    this.trace.addTrace(
      MappingRelational2Uschema.dot(fK1.getOwner().getName(), fK1.getConstraintname()), fK1, 
      MappingRelational2Uschema.dot(ref1.getOwner().getName(), ref1.getName()), ref1);
    this.trace.addTrace(
      MappingRelational2Uschema.dot(fK2.getOwner().getName(), fK2.getConstraintname()), fK2, 
      MappingRelational2Uschema.dot(ref2.getOwner().getName(), ref2.getName()), ref2);
  }

  public void r6(final Table t) {
    final List<FKey> fKs = this.getFKsNotInPK(t);
    final List<Key> uKs = this.findUKs(t);
    final Consumer<FKey> _function = (FKey fk) -> {
      boolean _isFKInUKs = this.isFKInUKs(fk, uKs);
      if (_isFKInUKs) {
        this.r6Table1_1(t, fk);
      } else {
        this.r6Table1_N(t, fk);
      }
    };
    fKs.forEach(_function);
  }

  public void r6Table1_1(final Table t, final FKey fk) {
    final Table s = fk.getRefsTo().getOwner();
    Object _targetInstance = this.trace.getTargetInstance(t.getName(), EntityType.class.getName());
    final EntityType et = ((EntityType) _targetInstance);
    Object _targetInstance_1 = this.trace.getTargetInstance(s.getName(), EntityType.class.getName());
    final EntityType es = ((EntityType) _targetInstance_1);
    final Reference rs = this.usFactory.createReference();
    String _name = s.getName();
    String _plus = (_name + "_");
    String _constraintname = fk.getConstraintname();
    String _plus_1 = (_plus + _constraintname);
    rs.setName(_plus_1);
    rs.setLowerBound(0);
    rs.setUpperBound(1);
    rs.setRefsTo(es);
    rs.getAttributes().addAll(this.columns2Attributes(fk.getColumns()));
    et.getFeatures().add(rs);
    this.trace.addTrace(
      MappingRelational2Uschema.dot(fk.getOwner().getName(), fk.getConstraintname()), fk, 
      MappingRelational2Uschema.dot(rs.getOwner().getName(), rs.getName()), rs);
  }

  public void r6Table1_N(final Table t, final FKey fk) {
    final Table s = fk.getRefsTo().getOwner();
    Object _targetInstance = this.trace.getTargetInstance(t.getName(), EntityType.class.getName());
    final EntityType et = ((EntityType) _targetInstance);
    Object _targetInstance_1 = this.trace.getTargetInstance(s.getName(), EntityType.class.getName());
    final EntityType es = ((EntityType) _targetInstance_1);
    final Reference rt = this.usFactory.createReference();
    final Key pk = this.findPK(t);
    String _name = t.getName();
    String _plus = (_name + "s");
    rt.setName(_plus);
    rt.setLowerBound(0);
    rt.setUpperBound((-1));
    rt.setRefsTo(et);
    es.getFeatures().add(rt);
    final Consumer<Column> _function = (Column col) -> {
      final Attribute at = this.usFactory.createAttribute();
      String _name_1 = col.getName();
      String _name_2 = et.getName();
      String _plus_1 = (_name_1 + _name_2);
      String _plus_2 = (_plus_1 + "s");
      at.setName(_plus_2);
      rt.getAttributes().add(at);
      es.getFeatures().add(at);
      this.datatype2PrimitiveType(col, at);
      this.trace.addTrace(
        MappingRelational2Uschema.dot(col.getOwner().getName(), col.getName()), col, 
        MappingRelational2Uschema.dot(at.getOwner().getName(), at.getName()), at);
    };
    pk.getColumns().forEach(_function);
    this.trace.addTrace(
      MappingRelational2Uschema.dot(fk.getOwner().getName(), fk.getConstraintname()), fk, 
      MappingRelational2Uschema.dot(rt.getOwner().getName(), rt.getName()), rt);
  }

  public boolean weakCondition(final Table t) {
    final List<FKey> fKs = this.getFKsInPK(t);
    int _size = fKs.size();
    boolean _equals = (_size == 1);
    if (_equals) {
      return true;
    }
    return false;
  }

  public boolean mNCondition(final Table t) {
    final List<FKey> fKs = this.getFKsInPK(t);
    int _size = fKs.size();
    boolean _equals = (_size == 2);
    if (_equals) {
      return true;
    }
    return false;
  }

  public List<FKey> getFKsInPK(final Table t) {
    final List<FKey> fKs = new LinkedList<FKey>();
    final Key pk = this.findPK(t);
    if ((pk == null)) {
      return fKs;
    }
    final Consumer<FKey> _function = (FKey fk) -> {
      boolean _containsAll = pk.getColumns().containsAll(fk.getColumns());
      if (_containsAll) {
        fKs.add(fk);
      }
    };
    t.getFks().forEach(_function);
    return fKs;
  }

  public List<FKey> getFKsNotInPK(final Table t) {
    final List<FKey> fKs = new LinkedList<FKey>();
    final Key pk = this.findPK(t);
    if ((pk == null)) {
      return fKs;
    }
    final Consumer<FKey> _function = (FKey fk) -> {
      boolean _containsAll = pk.getColumns().containsAll(fk.getColumns());
      boolean _not = (!_containsAll);
      if (_not) {
        fKs.add(fk);
      }
    };
    t.getFks().forEach(_function);
    return fKs;
  }

  public Key findPK(final Table t) {
    final Function1<Key, Boolean> _function = (Key key) -> {
      boolean _isIsPK = key.isIsPK();
      return Boolean.valueOf((_isIsPK == true));
    };
    return IterableExtensions.<Key>findFirst(t.getKeys(), _function);
  }

  public List<Key> findUKs(final Table t) {
    final Function1<Key, Boolean> _function = (Key key) -> {
      boolean _isIsPK = key.isIsPK();
      return Boolean.valueOf((_isIsPK == false));
    };
    return IterableExtensions.<Key>toList(IterableExtensions.<Key>filter(t.getKeys(), _function));
  }

  public boolean isFKInUKs(final FKey fk, final List<Key> uKs) {
    for (final Key uk : uKs) {
      boolean _containsAll = uk.getColumns().containsAll(fk.getColumns());
      if (_containsAll) {
        return true;
      }
    }
    return false;
  }

  public void datatype2PrimitiveType(final Column c, final Attribute at) {
    final PrimitiveType primitiveType = this.usFactory.createPrimitiveType();
    primitiveType.setName(MappingRelational2Uschema.typeConversionRelToUsc(c.getDatatype()));
    at.setType(primitiveType);
    this.trace.addTrace(
      MappingRelational2Uschema.dot(c.getOwner().getName(), c.getName()), c, 
      MappingRelational2Uschema.dot(at.getOwner().getName(), at.getName(), primitiveType.getName()), 
      at.getType());
  }

  public List<Attribute> columns2Attributes(final List<Column> columns) {
    final Function1<Column, Attribute> _function = (Column c) -> {
      final String columnName = MappingRelational2Uschema.dot(c.getOwner().getName(), c.getName());
      Object _targetInstance = this.trace.getTargetInstance(columnName, Attribute.class.getName());
      final Attribute at = ((Attribute) _targetInstance);
      return at;
    };
    return ListExtensions.<Column, Attribute>map(columns, _function);
  }

  public static String typeConversionRelToUsc(final String dataType) {
    final String dtUp = dataType.toUpperCase();
    if (dtUp != null) {
      switch (dtUp) {
        case "VARCHAR":
          return "String";
        case "INT":
          return "int";
        case "DOUBLE":
          return "double";
        case "BOOLEAN":
          return "boolean";
        case "DATE":
          return "Date";
      }
    }
    return null;
  }

  public static String dot(final String... strings) {
    return IterableExtensions.join(((Iterable<?>)Conversions.doWrapArray(strings)), ".");
  }
}
