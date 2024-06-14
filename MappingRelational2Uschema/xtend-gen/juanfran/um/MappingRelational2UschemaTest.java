package juanfran.um;

import com.google.common.base.Objects;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import relationalschema.Column;
import relationalschema.FKey;
import relationalschema.Key;
import relationalschema.Table;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.DataType;
import uschema.EntityType;
import uschema.Feature;
import uschema.PrimitiveType;
import uschema.Reference;
import uschema.RelationshipType;
import uschema.SchemaType;

@SuppressWarnings("all")
public class MappingRelational2UschemaTest {
  private static final String RELATIONAL_SCHEMA_0 = "test-input-files/Relational_0.xmi";

  private static final String RELATIONAL_SCHEMA_1 = "test-input-files/Relational_1.xmi";

  private static final String RELATIONAL_SCHEMA_2 = "test-input-files/Relational_2.xmi";

  private static final String RELATIONAL_SCHEMA_3 = "test-input-files/Relational_3.xmi";

  private static final String RELATIONAL_SCHEMA_4 = "test-input-files/Relational_4.xmi";

  private static final String RELATIONAL_SCHEMA_5 = "test-input-files/Relational_5.xmi";

  private static final String RELATIONAL_SCHEMA_6_1_1 = "test-input-files/Relational_6-1_1.xmi";

  private static final String RELATIONAL_SCHEMA_6_1_N = "test-input-files/Relational_6-1_N.xmi";

  private static final String RELATIONAL_SCHEMA_INTEGRATION = "test-input-files/Relational_integration.xmi";

  private final MappingRelational2Uschema mapping = new MappingRelational2Uschema();

  @Test
  public void relationalSchema2USchemaOK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_0);
    this.mapping.relationalSchema2USchema();
    Assertions.assertNotNull(this.mapping.getUSchema());
    Assertions.assertEquals(this.mapping.getUSchema().getName(), this.mapping.getRelationalSchema().getName());
  }

  @Test
  public void table2SchemaType_EntityType_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_1);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.table2EntityType_asserts(t);
  }

  @Test
  public void table2SchemaType_RelationshipType_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_5);
    this.mapping.relationalSchema2USchema();
    final Function1<Table, Boolean> _function = (Table t) -> {
      return Boolean.valueOf(this.mapping.isMNTable(t));
    };
    final Table t = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function);
    this.table2RelationshipType_asserts(t);
  }

  @Test
  public void column2Attribute_EntityType_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_2);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.table2EntityType_asserts(t);
    final Column c = IterableExtensions.<Column>head(t.getColumns());
    this.column2Attribute_asserts(c);
  }

  @Test
  public void key2Key_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_3);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.table2EntityType_asserts(t);
    final Consumer<Column> _function = (Column c) -> {
      this.column2Attribute_asserts(c);
    };
    t.getColumns().forEach(_function);
    final Key pK = this.mapping.findPK(t);
    this.key2Key_asserts(pK);
    final Key uK = IterableExtensions.<Key>head(this.mapping.findUKs(t));
    this.key2Key_asserts(uK);
  }

  @Test
  public void weakTable2Aggregate_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_4);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.table2EntityType_asserts(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.column2Attribute_asserts(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.key2Key_asserts(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    final Function1<Table, Boolean> _function_2 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "DetallesUsuario"));
    };
    final Table w = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_2);
    this.weakTable2Aggregate_asserts(w);
  }

  @Test
  public void mNTable2RelationshipType_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_5);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      boolean _isMNTable = this.mapping.isMNTable(t);
      if (_isMNTable) {
        this.table2RelationshipType_asserts(t);
      } else {
        this.table2EntityType_asserts(t);
      }
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.column2Attribute_asserts(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.key2Key_asserts(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    final Function1<Table, Boolean> _function_2 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores"));
    };
    final Table m = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_2);
    this.mNTable2RelationshipType_asserts(m);
  }

  @Test
  public void r6Table1_1_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_6_1_1);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.table2EntityType_asserts(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.column2Attribute_asserts(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.key2Key_asserts(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    final Function1<Table, Boolean> _function_2 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Reserva"));
    };
    final Table t = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_2);
    final FKey fk = IterableExtensions.<FKey>head(t.getFks());
    this.r6Table1_1_asserts(t, fk);
  }

  @Test
  public void r6Table1_N_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_6_1_N);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.table2EntityType_asserts(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.column2Attribute_asserts(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.key2Key_asserts(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    final Function1<Table, Boolean> _function_2 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Reserva"));
    };
    final Table t = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_2);
    final FKey fk = IterableExtensions.<FKey>head(t.getFks());
    this.r6Table1_N_asserts(t, fk);
  }

  @Test
  public void relationalSchema2USchema_integration_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_INTEGRATION);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      boolean _isMNTable = this.mapping.isMNTable(t);
      if (_isMNTable) {
        this.table2RelationshipType_asserts(t);
      } else {
        this.table2EntityType_asserts(t);
      }
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.column2Attribute_asserts(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.key2Key_asserts(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    final Consumer<Table> _function_2 = (Table t) -> {
      boolean _isWeakTable = this.mapping.isWeakTable(t);
      if (_isWeakTable) {
        this.weakTable2Aggregate_asserts(t);
      } else {
        boolean _isMNTable = this.mapping.isMNTable(t);
        if (_isMNTable) {
          this.mNTable2RelationshipType_asserts(t);
        } else {
          final List<FKey> fKs = this.mapping.getFKsNotInPK(t);
          final List<Key> uKs = this.mapping.findUKs(t);
          final Consumer<FKey> _function_3 = (FKey fk) -> {
            boolean _isFKInUKs = this.mapping.isFKInUKs(fk, uKs);
            if (_isFKInUKs) {
              this.r6Table1_1_asserts(t, fk);
            } else {
              this.r6Table1_N_asserts(t, fk);
            }
          };
          fKs.forEach(_function_3);
        }
      }
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_2);
  }

  private EntityType table2EntityType_asserts(final Table t) {
    final int numEntities1 = this.mapping.getUSchema().getEntities().size();
    this.mapping.table2SchemaType(t);
    final Function1<EntityType, Boolean> _function = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = t.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType et = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final int numEntities2 = this.mapping.getUSchema().getEntities().size();
    Assertions.assertNotNull(et);
    Assertions.assertEquals((numEntities1 + 1), numEntities2);
    Assertions.assertTrue(et.isRoot());
    return et;
  }

  private void table2RelationshipType_asserts(final Table t) {
    final int numRelationships1 = this.mapping.getUSchema().getRelationships().size();
    this.mapping.table2SchemaType(t);
    final Function1<RelationshipType, Boolean> _function = (RelationshipType r) -> {
      String _name = r.getName();
      String _name_1 = t.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final RelationshipType rt = IterableExtensions.<RelationshipType>findFirst(this.mapping.getUSchema().getRelationships(), _function);
    final int numRelationship2 = this.mapping.getUSchema().getRelationships().size();
    Assertions.assertNotNull(rt);
    Assertions.assertEquals((numRelationships1 + 1), numRelationship2);
  }

  private void column2Attribute_asserts(final Column c) {
    final Function1<EntityType, Boolean> _function = (EntityType e) -> {
      String _name = e.getName();
      String _name_1 = c.getOwner().getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    SchemaType st = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    if ((st == null)) {
      final Function1<RelationshipType, Boolean> _function_1 = (RelationshipType r) -> {
        String _name = r.getName();
        String _name_1 = c.getOwner().getName();
        return Boolean.valueOf(Objects.equal(_name, _name_1));
      };
      st = IterableExtensions.<RelationshipType>findFirst(this.mapping.getUSchema().getRelationships(), _function_1);
    }
    final int stFeaturesSize1 = st.getFeatures().size();
    this.mapping.column2Attribute(c);
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      String _name_1 = c.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(st.getFeatures(), _function_2);
    final Attribute at = ((Attribute) _findFirst);
    final int stFeaturesSize2 = st.getFeatures().size();
    if (((st instanceof RelationshipType) && this.mapping.isColumnInFKsOrPKs(c))) {
      Assertions.assertNull(at);
    } else {
      Assertions.assertNotNull(at);
      Assertions.assertEquals((stFeaturesSize1 + 1), stFeaturesSize2);
      DataType _type = at.getType();
      Assertions.assertEquals(MappingRelational2Uschema.typeConversionRelToUsc(c.getDatatype()), ((PrimitiveType) _type).getName());
      Assertions.assertEquals(Boolean.valueOf(c.isNullable()), Boolean.valueOf(at.isOptional()));
    }
  }

  private void key2Key_asserts(final Key k) {
    final Function1<EntityType, Boolean> _function = (EntityType e) -> {
      String _name = e.getName();
      String _name_1 = k.getOwner().getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    EntityType et = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    if ((et == null)) {
      return;
    }
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.key2Key(k);
    final int etFeaturesSize2 = et.getFeatures().size();
    final Function1<Feature, Boolean> _function_1 = (Feature f) -> {
      String _name = f.getName();
      String _constraintname = k.getConstraintname();
      return Boolean.valueOf(Objects.equal(_name, _constraintname));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_1);
    final uschema.Key u_k = ((uschema.Key) _findFirst);
    Assertions.assertNotNull(u_k);
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    Assertions.assertEquals(Boolean.valueOf(k.isIsPK()), Boolean.valueOf(u_k.isIsID()));
    Assertions.assertEquals(k.getColumns().size(), u_k.getAttributes().size());
  }

  private void weakTable2Aggregate_asserts(final Table w) {
    final FKey fk = IterableExtensions.<FKey>head(this.mapping.getFKsInPK(w));
    final Table s = fk.getRefsTo().getOwner();
    final Function1<EntityType, Boolean> _function = (EntityType et) -> {
      String _name = et.getName();
      String _name_1 = w.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType ew = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final Function1<EntityType, Boolean> _function_1 = (EntityType et) -> {
      String _name = et.getName();
      String _name_1 = s.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType es = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_1);
    final int esFeaturesSize1 = es.getFeatures().size();
    this.mapping.weakTable2Aggregate(w);
    final int esFeaturesSize2 = es.getFeatures().size();
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      String _name_1 = w.getName();
      String _plus = (_name_1 + "s");
      return Boolean.valueOf(Objects.equal(_name, _plus));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(es.getFeatures(), _function_2);
    final Aggregate g = ((Aggregate) _findFirst);
    Assertions.assertNotNull(g);
    Assertions.assertEquals((esFeaturesSize1 + 1), esFeaturesSize2);
    String _name = w.getName();
    String _plus = (_name + "s");
    Assertions.assertEquals(_plus, g.getName());
    Assertions.assertEquals(0, g.getLowerBound());
    Assertions.assertEquals((-1), g.getUpperBound());
    Assertions.assertEquals(ew, g.getSpecifiedBy());
    Assertions.assertFalse(ew.isRoot());
  }

  private void mNTable2RelationshipType_asserts(final Table m) {
    final FKey fk1 = m.getFks().get(0);
    final FKey fk2 = m.getFks().get(1);
    final Table t1 = fk1.getRefsTo().getOwner();
    final Table t2 = fk2.getRefsTo().getOwner();
    final Function1<EntityType, Boolean> _function = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = t1.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType et1 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final Function1<EntityType, Boolean> _function_1 = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = t2.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType et2 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_1);
    final Function1<RelationshipType, Boolean> _function_2 = (RelationshipType r) -> {
      String _name = r.getName();
      String _name_1 = m.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final RelationshipType rm = IterableExtensions.<RelationshipType>findFirst(this.mapping.getUSchema().getRelationships(), _function_2);
    final int rmNumReferences1 = rm.getReference().size();
    this.mapping.mNTable2RelationshipType(m);
    final int rmNumReferences2 = rm.getReference().size();
    Assertions.assertEquals((rmNumReferences1 + 2), rmNumReferences2);
    final Function1<Feature, Boolean> _function_3 = (Feature f) -> {
      String _name = f.getName();
      String _constraintname = fk2.getConstraintname();
      return Boolean.valueOf(Objects.equal(_name, _constraintname));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et1.getFeatures(), _function_3);
    final Reference ref1 = ((Reference) _findFirst);
    Assertions.assertNotNull(ref1);
    Assertions.assertEquals(1, ref1.getLowerBound());
    Assertions.assertEquals((-1), ref1.getUpperBound());
    Assertions.assertTrue(rm.getReference().contains(ref1));
    EList<Column> _columns = fk2.getRefsTo().getColumns();
    for (final Column c : _columns) {
      {
        final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
          String _name = f.getName();
          String _name_1 = c.getName();
          return Boolean.valueOf(Objects.equal(_name, _name_1));
        };
        Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(et2.getFeatures(), _function_4);
        final Attribute at = ((Attribute) _findFirst_1);
        Assertions.assertNotNull(at);
        Assertions.assertTrue(ref1.getAttributes().contains(at));
      }
    }
    final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
      String _name = f.getName();
      String _constraintname = fk1.getConstraintname();
      return Boolean.valueOf(Objects.equal(_name, _constraintname));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(et2.getFeatures(), _function_4);
    final Reference ref2 = ((Reference) _findFirst_1);
    Assertions.assertNotNull(ref2);
    Assertions.assertEquals(1, ref2.getLowerBound());
    Assertions.assertEquals((-1), ref2.getUpperBound());
    Assertions.assertTrue(rm.getReference().contains(ref2));
    EList<Column> _columns_1 = fk1.getRefsTo().getColumns();
    for (final Column c_1 : _columns_1) {
      {
        final Function1<Feature, Boolean> _function_5 = (Feature f) -> {
          String _name = f.getName();
          String _name_1 = c_1.getName();
          return Boolean.valueOf(Objects.equal(_name, _name_1));
        };
        Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(et1.getFeatures(), _function_5);
        final Attribute at = ((Attribute) _findFirst_2);
        Assertions.assertNotNull(at);
        Assertions.assertTrue(ref2.getAttributes().contains(at));
      }
    }
  }

  private void r6Table1_1_asserts(final Table t, final FKey fk) {
    final Table s = fk.getRefsTo().getOwner();
    final Function1<EntityType, Boolean> _function = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = t.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType et = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final Function1<EntityType, Boolean> _function_1 = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = s.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType es = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_1);
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.r6Table1_1(t, fk);
    final int etFeaturesSize2 = et.getFeatures().size();
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      String _name_1 = s.getName();
      String _plus = (_name_1 + "_");
      String _constraintname = fk.getConstraintname();
      String _plus_1 = (_plus + _constraintname);
      return Boolean.valueOf(Objects.equal(_name, _plus_1));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_2);
    final Reference rs = ((Reference) _findFirst);
    Assertions.assertNotNull(rs);
    Assertions.assertEquals(0, rs.getLowerBound());
    Assertions.assertEquals(1, rs.getUpperBound());
    Assertions.assertEquals(es, rs.getRefsTo());
    EList<Column> _columns = fk.getColumns();
    for (final Column c : _columns) {
      {
        final Function1<Feature, Boolean> _function_3 = (Feature f) -> {
          String _name = f.getName();
          String _name_1 = c.getName();
          return Boolean.valueOf(Objects.equal(_name, _name_1));
        };
        Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_3);
        final Attribute at = ((Attribute) _findFirst_1);
        Assertions.assertNotNull(at);
        Assertions.assertTrue(rs.getAttributes().contains(at));
      }
    }
  }

  private void r6Table1_N_asserts(final Table t, final FKey fk) {
    final Table s = fk.getRefsTo().getOwner();
    final Function1<EntityType, Boolean> _function = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = t.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType et = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final Function1<EntityType, Boolean> _function_1 = (EntityType r) -> {
      String _name = r.getName();
      String _name_1 = s.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final EntityType es = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_1);
    final Key pk = this.mapping.findPK(t);
    final int esFeaturesSize1 = es.getFeatures().size();
    this.mapping.r6Table1_N(t, fk);
    final int esFeaturesSize2 = es.getFeatures().size();
    Assertions.assertEquals((esFeaturesSize1 + 2), esFeaturesSize2);
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      String _name_1 = t.getName();
      String _plus = (_name_1 + "s");
      return Boolean.valueOf(Objects.equal(_name, _plus));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(es.getFeatures(), _function_2);
    final Reference rt = ((Reference) _findFirst);
    Assertions.assertNotNull(rt);
    Assertions.assertEquals(0, rt.getLowerBound());
    Assertions.assertEquals((-1), rt.getUpperBound());
    Assertions.assertEquals(et, rt.getRefsTo());
    EList<Column> _columns = pk.getColumns();
    for (final Column c : _columns) {
      {
        final Function1<Feature, Boolean> _function_3 = (Feature f) -> {
          String _name = f.getName();
          String _name_1 = c.getName();
          String _name_2 = et.getName();
          String _plus = (_name_1 + _name_2);
          String _plus_1 = (_plus + "s");
          return Boolean.valueOf(Objects.equal(_name, _plus_1));
        };
        Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(es.getFeatures(), _function_3);
        Attribute at = ((Attribute) _findFirst_1);
        Assertions.assertNotNull(at);
        DataType _type = at.getType();
        Assertions.assertEquals(MappingRelational2Uschema.typeConversionRelToUsc(c.getDatatype()), ((PrimitiveType) _type).getName());
        Assertions.assertTrue(rt.getAttributes().contains(at));
        Assertions.assertTrue(at.getReferences().contains(rt));
      }
    }
  }
}
