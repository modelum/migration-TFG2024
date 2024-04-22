package juanfran.um;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
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

  private final Map<Table, EntityType> entityTypes;

  private final Map<Table, RelationshipType> relationshipTypes;

  private final Map<Column, Attribute> attributes;

  private final Map<Key, uschema.Key> keys;

  public MappingRelational2Uschema() {
    this.usFactory = UschemaFactory.eINSTANCE;
    HashMap<Table, EntityType> _hashMap = new HashMap<Table, EntityType>();
    this.entityTypes = _hashMap;
    HashMap<Table, RelationshipType> _hashMap_1 = new HashMap<Table, RelationshipType>();
    this.relationshipTypes = _hashMap_1;
    HashMap<Column, Attribute> _hashMap_2 = new HashMap<Column, Attribute>();
    this.attributes = _hashMap_2;
    HashMap<Key, uschema.Key> _hashMap_3 = new HashMap<Key, uschema.Key>();
    this.keys = _hashMap_3;
  }

  public USchema relationalSchema2USchema(final RelationalSchema relationalSchema) {
    final USchema uSchema = this.usFactory.createUSchema();
    uSchema.setName(relationalSchema.getName());
    final Consumer<Table> _function = (Table t) -> {
      final SchemaType st = this.table2SchemaType(t);
      boolean _matched = false;
      if ((st instanceof EntityType)) {
        _matched=true;
        uSchema.getEntities().add(((EntityType) st));
      }
      if (!_matched) {
        if ((st instanceof RelationshipType)) {
          _matched=true;
          uSchema.getRelationships().add(((RelationshipType) st));
        }
      }
    };
    relationalSchema.getTables().forEach(_function);
    final BiConsumer<Table, RelationshipType> _function_1 = (Table t, RelationshipType __) -> {
      this.mNTable2RelationshipType(t);
    };
    this.relationshipTypes.forEach(_function_1);
    final BiConsumer<Table, EntityType> _function_2 = (Table t, EntityType __) -> {
      boolean _weakCondition = this.weakCondition(t);
      if (_weakCondition) {
        this.weakTable2Aggregate(t);
      }
      this.r6(t);
    };
    this.entityTypes.forEach(_function_2);
    return uSchema;
  }

  public SchemaType table2SchemaType(final Table t) {
    SchemaType st = null;
    boolean _mNCondition = this.mNCondition(t);
    if (_mNCondition) {
      st = this.usFactory.createRelationshipType();
      this.relationshipTypes.put(t, ((RelationshipType) st));
    } else {
      final EntityType et = this.usFactory.createEntityType();
      et.setRoot(true);
      this.entityTypes.put(t, et);
      st = et;
    }
    st.setName(t.getName());
    final Function1<Column, Attribute> _function = (Column c) -> {
      return this.column2Attribute(c);
    };
    final List<Attribute> attributes = ListExtensions.<Column, Attribute>map(t.getColumns(), _function);
    st.getFeatures().addAll(attributes);
    final Function1<Key, uschema.Key> _function_1 = (Key k) -> {
      return this.pK2Key(k);
    };
    final List<uschema.Key> keys = ListExtensions.<Key, uschema.Key>map(t.getKeys(), _function_1);
    st.getFeatures().addAll(keys);
    return st;
  }

  public Attribute column2Attribute(final Column c) {
    final Attribute at = this.usFactory.createAttribute();
    final PrimitiveType primitiveType = this.usFactory.createPrimitiveType();
    primitiveType.setName(MappingRelational2Uschema.typeConversionRelToUsc(c.getDatatype()));
    at.setName(c.getName());
    at.setType(primitiveType);
    at.setOptional(c.isNullable());
    this.attributes.put(c, at);
    return at;
  }

  public uschema.Key pK2Key(final Key rKey) {
    final uschema.Key uKey = this.usFactory.createKey();
    uKey.setName(rKey.getConstraintname());
    uKey.setIsID(rKey.isIsPK());
    uKey.getAttributes().addAll(this.columns2Attributes(rKey.getColumns()));
    this.keys.put(rKey, uKey);
    return uKey;
  }

  public void weakTable2Aggregate(final Table w) {
    InputOutput.<String>println(w.toString());
    final Table s = IterableExtensions.<FKey>head(this.getFKsInPK(w)).getRefsTo().getOwner();
    final EntityType es = this.entityTypes.get(s);
    final EntityType ew = this.entityTypes.get(w);
    final Aggregate ag = this.usFactory.createAggregate();
    String _name = w.getName();
    String _plus = (_name + "s");
    ag.setName(_plus);
    ag.setLowerBound(0);
    ag.setUpperBound((-1));
    ag.setSpecifiedBy(ew);
    es.getFeatures().add(ag);
    ew.setRoot(false);
  }

  public RelationshipType mNTable2RelationshipType(final Table m) {
    final RelationshipType rm = this.relationshipTypes.get(m);
    final List<FKey> fKs = this.getFKsInPK(m);
    final FKey fK1 = fKs.get(0);
    final FKey fK2 = fKs.get(1);
    final Table t1 = fK1.getRefsTo().getOwner();
    final Table t2 = fK2.getRefsTo().getOwner();
    final EntityType et1 = this.entityTypes.get(t1);
    final EntityType et2 = this.entityTypes.get(t2);
    final Reference ref = this.usFactory.createReference();
    ref.setName(t2.getName());
    ref.setLowerBound(1);
    ref.setUpperBound(1);
    ref.setRefsTo(et2);
    ref.setOwner(et1);
    ref.setIsFeaturedBy(rm);
    ref.getAttributes().addAll(this.columns2Attributes(fK2.getColumns()));
    et1.setRoot(true);
    et2.setRoot(true);
    return rm;
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
    final EntityType et = this.entityTypes.get(t);
    final EntityType es = this.entityTypes.get(s);
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
  }

  public void r6Table1_N(final Table t, final FKey fk) {
    final Table s = fk.getRefsTo().getOwner();
    final EntityType et = this.entityTypes.get(t);
    final EntityType es = this.entityTypes.get(s);
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
      final PrimitiveType primitiveType = this.usFactory.createPrimitiveType();
      primitiveType.setName(MappingRelational2Uschema.typeConversionRelToUsc(col.getDatatype()));
      String _name_1 = col.getName();
      String _name_2 = et.getName();
      String _plus_1 = (_name_1 + _name_2);
      String _plus_2 = (_plus_1 + "s");
      at.setName(_plus_2);
      at.setType(primitiveType);
      rt.getAttributes().add(at);
      es.getFeatures().add(at);
    };
    pk.getColumns().forEach(_function);
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
      }
    }
    return null;
  }

  public List<Attribute> columns2Attributes(final List<Column> columns) {
    final Function1<Column, Attribute> _function = (Column c) -> {
      return this.attributes.get(c);
    };
    return ListExtensions.<Column, Attribute>map(columns, _function);
  }
}
