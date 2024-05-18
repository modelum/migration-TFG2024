package juanfran.um;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import juanfran.um.trace.Trace;
import org.eclipse.emf.common.util.EList;
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
  private RelationalSchema relationalSchema;

  private USchema uSchema;

  private final UschemaFactory usFactory;

  private final Trace trace;

  public MappingRelational2Uschema() {
    this.usFactory = UschemaFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public USchema relationalSchema2USchema(final RelationalSchema rs) {
    this.relationalSchema = rs;
    this.uSchema = this.usFactory.createUSchema();
    this.uSchema.setName(this.relationalSchema.getName());
    final Consumer<Table> _function = (Table t) -> {
      this.table2SchemaType(t);
    };
    this.relationalSchema.getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      boolean _isWeakTable = this.isWeakTable(t);
      if (_isWeakTable) {
        this.weakTable2Aggregate(t);
      } else {
        boolean _isMNTable = this.isMNTable(t);
        if (_isMNTable) {
          this.mNTable2RelationshipType(t);
        } else {
          this.r6(t);
        }
      }
    };
    this.relationalSchema.getTables().forEach(_function_1);
    this.trace.addTrace(this.relationalSchema.getName(), this.relationalSchema, this.uSchema.getName(), this.uSchema);
    this.trace.printDirectTraceTypes();
    return this.uSchema;
  }

  public void table2SchemaType(final Table t) {
    SchemaType st = null;
    boolean _isMNTable = this.isMNTable(t);
    if (_isMNTable) {
      st = this.usFactory.createRelationshipType();
      this.uSchema.getRelationships().add(((RelationshipType) st));
    } else {
      final EntityType et = this.usFactory.createEntityType();
      et.setRoot(true);
      this.uSchema.getEntities().add(et);
      st = et;
    }
    st.setName(t.getName());
    this.trace.addTrace(t.getName(), t, st.getName(), st);
    EList<Column> _columns = t.getColumns();
    for (final Column c : _columns) {
      this.column2Attribute(c, st);
    }
    EList<Key> _keys = t.getKeys();
    for (final Key k : _keys) {
      this.pK2Key(k, st);
    }
  }

  public void column2Attribute(final Column c, final SchemaType st) {
    final Attribute at = this.usFactory.createAttribute();
    final PrimitiveType primitiveType = this.usFactory.createPrimitiveType();
    primitiveType.setName(MappingRelational2Uschema.typeConversionRelToUsc(c.getDatatype()));
    at.setName(c.getName());
    at.setOptional(c.isNullable());
    st.getFeatures().add(at);
    String _name = c.getOwner().getName();
    String _plus = (_name + ".");
    String _name_1 = c.getName();
    String _plus_1 = (_plus + _name_1);
    String _name_2 = at.getOwner().getName();
    String _plus_2 = (_name_2 + ".");
    String _name_3 = at.getName();
    String _plus_3 = (_plus_2 + _name_3);
    this.trace.addTrace(_plus_1, c, _plus_3, at);
    at.setType(primitiveType);
    String _name_4 = c.getOwner().getName();
    String _plus_4 = (_name_4 + ".");
    String _name_5 = c.getName();
    String _plus_5 = (_plus_4 + _name_5);
    String _name_6 = at.getOwner().getName();
    String _plus_6 = (_name_6 + ".");
    String _name_7 = at.getName();
    String _plus_7 = (_plus_6 + _name_7);
    String _plus_8 = (_plus_7 + ".");
    String _name_8 = primitiveType.getName();
    String _plus_9 = (_plus_8 + _name_8);
    this.trace.addTrace(_plus_5, c, _plus_9, at.getType());
  }

  public void pK2Key(final Key rKey, final SchemaType st) {
    final uschema.Key uKey = this.usFactory.createKey();
    uKey.setName(rKey.getConstraintname());
    uKey.setIsID(rKey.isIsPK());
    uKey.getAttributes().addAll(this.columns2Attributes(rKey.getColumns()));
    st.getFeatures().add(uKey);
    String _name = rKey.getOwner().getName();
    String _plus = (_name + ".");
    String _constraintname = rKey.getConstraintname();
    String _plus_1 = (_plus + _constraintname);
    String _name_1 = uKey.getOwner().getName();
    String _plus_2 = (_name_1 + ".");
    String _name_2 = uKey.getName();
    String _plus_3 = (_plus_2 + _name_2);
    this.trace.addTrace(_plus_1, rKey, _plus_3, uKey);
  }

  public void weakTable2Aggregate(final Table w) {
    final FKey fk = IterableExtensions.<FKey>head(this.getFKsInPK(w));
    final Table s = fk.getRefsTo().getOwner();
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(s.getName()));
    final EntityType es = ((EntityType) _head);
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(w.getName()));
    final EntityType ew = ((EntityType) _head_1);
    final Aggregate ag = this.usFactory.createAggregate();
    String _name = w.getName();
    String _plus = (_name + "s");
    ag.setName(_plus);
    ag.setLowerBound(0);
    ag.setUpperBound((-1));
    ag.setSpecifiedBy(ew);
    ew.setRoot(false);
    es.getFeatures().add(ag);
    String _name_1 = fk.getOwner().getName();
    String _plus_1 = (_name_1 + ".");
    String _constraintname = fk.getConstraintname();
    String _plus_2 = (_plus_1 + _constraintname);
    String _name_2 = ag.getOwner().getName();
    String _plus_3 = (_name_2 + ".");
    String _name_3 = ag.getName();
    String _plus_4 = (_plus_3 + _name_3);
    this.trace.addTrace(_plus_2, fk, _plus_4, ag);
  }

  public void mNTable2RelationshipType(final Table m) {
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(m.getName()));
    final RelationshipType rm = ((RelationshipType) _head);
    final List<FKey> fKs = this.getFKsInPK(m);
    final FKey fK1 = fKs.get(0);
    final FKey fK2 = fKs.get(1);
    final Table t1 = fK1.getRefsTo().getOwner();
    final Table t2 = fK2.getRefsTo().getOwner();
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(t1.getName()));
    final EntityType et1 = ((EntityType) _head_1);
    Object _head_2 = IterableExtensions.<Object>head(this.trace.getTargetInstance(t2.getName()));
    final EntityType et2 = ((EntityType) _head_2);
    final Reference ref1 = this.usFactory.createReference();
    final Reference ref2 = this.usFactory.createReference();
    ref1.setName(t1.getName());
    ref1.setLowerBound(1);
    ref1.setUpperBound(1);
    ref1.setRefsTo(et1);
    ref1.setIsFeaturedBy(rm);
    ref1.getAttributes().addAll(this.columns2Attributes(fK1.getColumns()));
    et1.getFeatures().add(ref1);
    String _name = fK1.getOwner().getName();
    String _plus = (_name + ".");
    String _constraintname = fK1.getConstraintname();
    String _plus_1 = (_plus + _constraintname);
    String _name_1 = ref1.getOwner().getName();
    String _plus_2 = (_name_1 + ".");
    String _name_2 = ref1.getName();
    String _plus_3 = (_plus_2 + _name_2);
    this.trace.addTrace(_plus_1, fK1, _plus_3, ref1);
    ref2.setName(t2.getName());
    ref2.setLowerBound(1);
    ref2.setUpperBound(1);
    ref2.setRefsTo(et2);
    ref2.setIsFeaturedBy(rm);
    ref2.getAttributes().addAll(this.columns2Attributes(fK2.getColumns()));
    et2.getFeatures().add(ref2);
    String _name_3 = fK2.getOwner().getName();
    String _plus_4 = (_name_3 + ".");
    String _constraintname_1 = fK2.getConstraintname();
    String _plus_5 = (_plus_4 + _constraintname_1);
    String _name_4 = ref2.getOwner().getName();
    String _plus_6 = (_name_4 + ".");
    String _name_5 = ref2.getName();
    String _plus_7 = (_plus_6 + _name_5);
    this.trace.addTrace(_plus_5, fK2, _plus_7, ref2);
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
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(t.getName()));
    final EntityType et = ((EntityType) _head);
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(s.getName()));
    final EntityType es = ((EntityType) _head_1);
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
    String _name_1 = fk.getOwner().getName();
    String _plus_2 = (_name_1 + ".");
    String _constraintname_1 = fk.getConstraintname();
    String _plus_3 = (_plus_2 + _constraintname_1);
    String _name_2 = rs.getOwner().getName();
    String _plus_4 = (_name_2 + ".");
    String _name_3 = rs.getName();
    String _plus_5 = (_plus_4 + _name_3);
    this.trace.addTrace(_plus_3, fk, _plus_5, rs);
  }

  public void r6Table1_N(final Table t, final FKey fk) {
    final Table s = fk.getRefsTo().getOwner();
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(t.getName()));
    final EntityType et = ((EntityType) _head);
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(s.getName()));
    final EntityType es = ((EntityType) _head_1);
    final Reference rt = this.usFactory.createReference();
    final Key pk = this.findPK(t);
    String _name = t.getName();
    String _plus = (_name + "s");
    rt.setName(_plus);
    rt.setLowerBound(0);
    rt.setUpperBound((-1));
    rt.setRefsTo(et);
    es.getFeatures().add(rt);
    String _name_1 = fk.getOwner().getName();
    String _plus_1 = (_name_1 + ".");
    String _constraintname = fk.getConstraintname();
    String _plus_2 = (_plus_1 + _constraintname);
    String _name_2 = rt.getOwner().getName();
    String _plus_3 = (_name_2 + ".");
    String _name_3 = rt.getName();
    String _plus_4 = (_plus_3 + _name_3);
    this.trace.addTrace(_plus_2, fk, _plus_4, rt);
    final Consumer<Column> _function = (Column col) -> {
      final Attribute at = this.usFactory.createAttribute();
      final PrimitiveType primitiveType = this.usFactory.createPrimitiveType();
      primitiveType.setName(MappingRelational2Uschema.typeConversionRelToUsc(col.getDatatype()));
      String _name_4 = col.getName();
      String _name_5 = et.getName();
      String _plus_5 = (_name_4 + _name_5);
      String _plus_6 = (_plus_5 + "s");
      at.setName(_plus_6);
      rt.getAttributes().add(at);
      es.getFeatures().add(at);
      String _name_6 = fk.getOwner().getName();
      String _plus_7 = (_name_6 + ".");
      String _constraintname_1 = fk.getConstraintname();
      String _plus_8 = (_plus_7 + _constraintname_1);
      String _name_7 = at.getOwner().getName();
      String _plus_9 = (_name_7 + ".");
      String _name_8 = at.getName();
      String _plus_10 = (_plus_9 + _name_8);
      this.trace.addTrace(_plus_8, fk, _plus_10, at);
      at.setType(primitiveType);
      String _name_9 = fk.getOwner().getName();
      String _plus_11 = (_name_9 + ".");
      String _constraintname_2 = fk.getConstraintname();
      String _plus_12 = (_plus_11 + _constraintname_2);
      String _name_10 = at.getOwner().getName();
      String _plus_13 = (_name_10 + ".");
      String _name_11 = at.getName();
      String _plus_14 = (_plus_13 + _name_11);
      String _plus_15 = (_plus_14 + ".");
      String _name_12 = primitiveType.getName();
      String _plus_16 = (_plus_15 + _name_12);
      this.trace.addTrace(_plus_12, fk, _plus_16, at.getType());
    };
    pk.getColumns().forEach(_function);
  }

  public boolean isWeakTable(final Table t) {
    final List<FKey> fKs = this.getFKsInPK(t);
    int _size = fKs.size();
    boolean _equals = (_size == 1);
    if (_equals) {
      return true;
    }
    return false;
  }

  public boolean isMNTable(final Table t) {
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

  public List<Attribute> columns2Attributes(final List<Column> columns) {
    final Function1<Column, Attribute> _function = (Column c) -> {
      String _name = c.getOwner().getName();
      String _plus = (_name + ".");
      String _name_1 = c.getName();
      final String columnName = (_plus + _name_1);
      Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(columnName));
      final Attribute at = ((Attribute) _head);
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
}
