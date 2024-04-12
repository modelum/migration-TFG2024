package es.um.dsdm.uschema2relational;

import USchema.Aggregate;
import USchema.Attribute;
import USchema.DataType;
import USchema.EntityType;
import USchema.Feature;
import USchema.Key;
import USchema.PrimitiveType;
import USchema.Reference;
import USchema.RelationshipType;
import USchema.SchemaType;
import USchema.USchemaClass;
import com.google.common.base.Objects;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import relationalschema.Column;
import relationalschema.FKey;
import relationalschema.ReferentialAction;
import relationalschema.RelationalMMFactory;
import relationalschema.RelationalSchema;
import relationalschema.Table;

@SuppressWarnings("all")
public class MappingUSchema2Relational {
  private final RelationalMMFactory relationalFactory;

  private final Map<EntityType, Table> mappedTables;

  public MappingUSchema2Relational() {
    this.relationalFactory = RelationalMMFactory.eINSTANCE;
    this.mappedTables = CollectionLiterals.<EntityType, Table>newHashMap();
  }

  public RelationalSchema uschema2relational(final USchemaClass us) {
    final RelationalSchema rs = this.relationalFactory.createRelationalSchema();
    rs.setName(us.getName());
    final Consumer<EntityType> _function = (EntityType entity) -> {
      rs.getTables().add(this.entityType2table(entity));
    };
    us.getEntities().forEach(_function);
    final Consumer<RelationshipType> _function_1 = (RelationshipType relationship) -> {
      rs.getTables().add(this.relationshipType2table(relationship));
    };
    us.getRelationships().forEach(_function_1);
    return rs;
  }

  private Table entityType2table(final EntityType e) {
    boolean _containsKey = this.mappedTables.containsKey(e);
    if (_containsKey) {
      return this.mappedTables.get(e);
    }
    final Table t = this.relationalFactory.createTable();
    this.mappedTables.put(e, t);
    t.setName(e.getName());
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        t.getColumns().add(this.attribute2column(((Attribute)feature), t));
      }
      if ((feature instanceof Key)) {
        t.getKeys().add(this.key2key(((Key)feature), t));
      }
      if ((feature instanceof Reference)) {
        t.getFks().add(this.reference2fkey(((Reference)feature), t));
      }
      if ((feature instanceof Aggregate)) {
        this.aggregate2weaktable(((Aggregate)feature), t);
      }
    };
    e.getFeatures().forEach(_function);
    return t;
  }

  private Column attribute2column(final Attribute at, final Table t) {
    final Function1<Column, Boolean> _function = (Column col) -> {
      String _name = col.getName();
      String _name_1 = at.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final Column columnaExistente = IterableExtensions.<Column>findFirst(t.getColumns(), _function);
    if ((columnaExistente != null)) {
      return columnaExistente;
    } else {
      final Column col = this.relationalFactory.createColumn();
      col.setName(at.getName());
      col.setDatatype(this.mapType2String(at.getType()));
      String _datatype = col.getDatatype();
      if (_datatype != null) {
        switch (_datatype) {
          case "VARCHAR":
            col.setSize(255);
            break;
          case "NUMERIC":
            col.setSize(38);
            break;
          case "BOOLEAN":
            col.setSize(1);
            break;
          case "DATE":
            col.setSize(10);
            break;
          default:
            break;
        }
      } else {
      }
      col.setNullable(true);
      col.setDefaultvalue(null);
      t.getColumns().add(col);
      return col;
    }
  }

  private relationalschema.Key key2key(final Key k, final Table t) {
    final relationalschema.Key pk = this.relationalFactory.createKey();
    boolean _isIsID = k.isIsID();
    boolean _equals = (_isIsID == true);
    if (_equals) {
      String _name = t.getName();
      String _plus = (_name + "_pk");
      pk.setConstraintname(_plus);
    } else {
      String _name_1 = k.getName();
      String _plus_1 = (_name_1 + "_ak");
      pk.setConstraintname(_plus_1);
    }
    pk.setOwner(t);
    pk.setIsPK(k.isIsID());
    final Consumer<Attribute> _function = (Attribute attribute) -> {
      pk.getColumns().add(this.attribute2column(attribute, t));
    };
    k.getAttributes().forEach(_function);
    return pk;
  }

  private FKey reference2fkey(final Reference ref, final Table t) {
    final FKey fk = this.relationalFactory.createFKey();
    final Table tr = this.entityType2table(ref.getRefsTo());
    String _name = tr.getName();
    String _plus = (_name + "_fk");
    fk.setConstraintname(_plus);
    fk.setOwner(t);
    final Consumer<relationalschema.Key> _function = (relationalschema.Key key) -> {
      boolean _isIsPK = key.isIsPK();
      boolean _equals = (_isIsPK == true);
      if (_equals) {
        fk.setRefsTo(key);
      }
    };
    tr.getKeys().forEach(_function);
    EList<Attribute> _attributes = ref.getAttributes();
    boolean _tripleNotEquals = (_attributes != null);
    if (_tripleNotEquals) {
      final Consumer<Attribute> _function_1 = (Attribute attribute) -> {
        fk.getColumns().add(this.attribute2column(attribute, t));
      };
      ref.getAttributes().forEach(_function_1);
    } else {
      final Consumer<Column> _function_2 = (Column column) -> {
        fk.getColumns().add(column);
      };
      fk.getRefsTo().getColumns().forEach(_function_2);
    }
    fk.setOnDelete(ReferentialAction.NO_ACTION);
    fk.setOnUpdate(ReferentialAction.CASCADE);
    return fk;
  }

  private Table aggregate2weaktable(final Aggregate ag, final Table ts) {
    final SchemaType schema = ag.getSpecifiedBy();
    final EntityType entidad = ((EntityType) schema);
    final Table tg = this.entityType2table(entidad);
    final FKey fk = this.relationalFactory.createFKey();
    String _name = ts.getName();
    String _plus = (_name + "_fk");
    fk.setConstraintname(_plus);
    final Consumer<relationalschema.Key> _function = (relationalschema.Key key) -> {
      boolean _isIsPK = key.isIsPK();
      if (_isIsPK) {
        fk.setRefsTo(key);
        final Consumer<Column> _function_1 = (Column column) -> {
          tg.getColumns().add(column);
          fk.getColumns().add(column);
        };
        key.getColumns().forEach(_function_1);
      }
    };
    ts.getKeys().forEach(_function);
    fk.setOnDelete(ReferentialAction.NO_ACTION);
    fk.setOnUpdate(ReferentialAction.CASCADE);
    tg.getFks().add(fk);
    final relationalschema.Key k = this.relationalFactory.createKey();
    final Column col = this.relationalFactory.createColumn();
    String _name_1 = tg.getName();
    String _plus_1 = (_name_1 + "_pk");
    k.setConstraintname(_plus_1);
    k.setIsPK(true);
    col.setName("id");
    col.setDatatype("Numeric");
    col.setSize(38);
    col.setNullable(false);
    col.setDefaultvalue("1");
    final Consumer<Column> _function_1 = (Column column) -> {
      k.getColumns().add(column);
    };
    fk.getColumns().forEach(_function_1);
    k.getColumns().add(col);
    tg.getKeys().add(k);
    return tg;
  }

  private Table relationshipType2table(final RelationshipType rt) {
    final Table t = this.relationalFactory.createTable();
    t.setName(rt.getName());
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        t.getColumns().add(this.attribute2column(((Attribute)feature), t));
      }
      if ((feature instanceof Reference)) {
        EList<SchemaType> _isFeaturedBy = ((Reference)feature).getIsFeaturedBy();
        boolean _equals = Objects.equal(_isFeaturedBy, rt);
        if (_equals) {
          final FKey fk1 = this.relationalFactory.createFKey();
          final Table td = this.entityType2table(((Reference)feature).getRefsTo());
          String _name = td.getName();
          String _plus = (_name + "_fk");
          fk1.setConstraintname(_plus);
          final Consumer<relationalschema.Key> _function_1 = (relationalschema.Key key) -> {
            boolean _isIsPK = key.isIsPK();
            if (_isIsPK) {
              fk1.setRefsTo(key);
              final Consumer<Column> _function_2 = (Column column) -> {
                fk1.getColumns().add(column);
              };
              key.getColumns().forEach(_function_2);
            }
          };
          td.getKeys().forEach(_function_1);
          t.getFks().add(fk1);
          final FKey fk2 = this.relationalFactory.createFKey();
          final Table to = this.relationalFactory.createTable();
          String _name_1 = to.getName();
          String _plus_1 = (_name_1 + "_fk");
          fk2.setConstraintname(_plus_1);
          final Consumer<relationalschema.Key> _function_2 = (relationalschema.Key key) -> {
            boolean _isIsPK = key.isIsPK();
            if (_isIsPK) {
              fk2.setRefsTo(key);
              final Consumer<Column> _function_3 = (Column column) -> {
                fk2.getColumns().add(column);
              };
              key.getColumns().forEach(_function_3);
            }
          };
          to.getKeys().forEach(_function_2);
          t.getFks().add(fk2);
          final relationalschema.Key pk = this.relationalFactory.createKey();
          String _name_2 = t.getName();
          String _plus_2 = (_name_2 + "_pk");
          pk.setConstraintname(_plus_2);
          pk.setIsPK(true);
          final Consumer<Column> _function_3 = (Column column) -> {
            pk.getColumns().add(column);
          };
          fk1.getColumns().forEach(_function_3);
          final Consumer<Column> _function_4 = (Column column) -> {
            pk.getColumns().add(column);
          };
          fk2.getColumns().forEach(_function_4);
          t.getKeys().add(pk);
        }
      }
    };
    rt.getFeatures().forEach(_function);
    return t;
  }

  private String mapType2String(final DataType datatype) {
    Object _xblockexpression = null;
    {
      final PrimitiveType primitiveType = ((PrimitiveType) datatype);
      Object _xifexpression = null;
      if ((primitiveType != null)) {
        Object _switchResult = null;
        String _name = primitiveType.getName();
        if (_name != null) {
          switch (_name) {
            case "String":
              return "VARCHAR";
            case "int":
              return "NUMERIC";
            case "boolean":
              return "BOOLEAN";
            case "double":
              return "NUMERIC";
            case "Date":
              return "DATE";
            default:
              _switchResult = null;
              break;
          }
        } else {
          _switchResult = null;
        }
        _xifexpression = _switchResult;
      }
      _xblockexpression = _xifexpression;
    }
    return ((String)_xblockexpression);
  }
}
