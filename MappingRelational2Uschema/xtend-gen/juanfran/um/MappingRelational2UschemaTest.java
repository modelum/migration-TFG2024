package juanfran.um;

import java.util.List;
import java.util.function.Consumer;
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
    final int numEntities1 = this.mapping.getUSchema().getEntities().size();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.mapping.table2SchemaType(t);
    final int numEntities2 = this.mapping.getUSchema().getEntities().size();
    final EntityType et = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    Assertions.assertEquals((numEntities1 + 1), numEntities2);
    Assertions.assertEquals(t.getName(), et.getName());
    Assertions.assertTrue(et.isRoot());
  }

  @Test
  public void table2SchemaType_RelationshipType_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_5);
    this.mapping.relationalSchema2USchema();
    final int numRelationships1 = this.mapping.getUSchema().getRelationships().size();
    final Function1<Table, Boolean> _function = (Table t) -> {
      return Boolean.valueOf(this.mapping.isMNTable(t));
    };
    final Table t = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function);
    this.mapping.table2SchemaType(t);
    final int numRelationships2 = this.mapping.getUSchema().getRelationships().size();
    final RelationshipType rt = IterableExtensions.<RelationshipType>head(this.mapping.getUSchema().getRelationships());
    Assertions.assertEquals((numRelationships1 + 1), numRelationships2);
    Assertions.assertEquals(t.getName(), rt.getName());
  }

  @Test
  public void column2Attribute_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_2);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.mapping.table2SchemaType(t);
    final Column c = IterableExtensions.<Column>head(t.getColumns());
    final EntityType et = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.column2Attribute(c);
    Feature _head = IterableExtensions.<Feature>head(et.getFeatures());
    final Attribute at = ((Attribute) _head);
    final int etFeaturesSize2 = et.getFeatures().size();
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    Assertions.assertEquals(c.getName(), at.getName());
    DataType _type = at.getType();
    Assertions.assertEquals("int", ((PrimitiveType) _type).getName());
    Assertions.assertEquals(Boolean.valueOf(c.isNullable()), Boolean.valueOf(at.isOptional()));
  }

  @Test
  public void key2Key_PK_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_3);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.mapping.table2SchemaType(t);
    final Consumer<Column> _function = (Column c) -> {
      this.mapping.column2Attribute(c);
    };
    t.getColumns().forEach(_function);
    final Key pK = this.mapping.findPK(t);
    final EntityType et = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.key2Key(pK);
    final Function1<Feature, Boolean> _function_1 = (Feature f) -> {
      return Boolean.valueOf((f instanceof uschema.Key));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_1);
    final uschema.Key k = ((uschema.Key) _findFirst);
    final int etFeaturesSize2 = et.getFeatures().size();
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    Assertions.assertEquals(pK.getConstraintname(), k.getName());
    Assertions.assertTrue(k.isIsID());
    Assertions.assertEquals(pK.getColumns().size(), k.getAttributes().size());
  }

  @Test
  public void key2Key_UK_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_3);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.mapping.table2SchemaType(t);
    final Consumer<Column> _function = (Column c) -> {
      this.mapping.column2Attribute(c);
    };
    t.getColumns().forEach(_function);
    final Key uK = IterableExtensions.<Key>head(this.mapping.findUKs(t));
    final EntityType et = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.key2Key(uK);
    final Function1<Feature, Boolean> _function_1 = (Feature f) -> {
      return Boolean.valueOf((f instanceof uschema.Key));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_1);
    final uschema.Key k = ((uschema.Key) _findFirst);
    final int etFeaturesSize2 = et.getFeatures().size();
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    Assertions.assertEquals(uK.getConstraintname(), k.getName());
    Assertions.assertFalse(k.isIsID());
    Assertions.assertEquals(uK.getColumns().size(), k.getAttributes().size());
  }

  @Test
  public void weakTable2Aggregate_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_4);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.mapping.table2SchemaType(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.mapping.column2Attribute(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.mapping.key2Key(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    Object _head = IterableExtensions.<Object>head(this.mapping.getTrace().getSourceInstance("DetallesUsuario"));
    final Table w = ((Table) _head);
    Object _head_1 = IterableExtensions.<Object>head(this.mapping.getTrace().getSourceInstance("Usuario"));
    final Table s = ((Table) _head_1);
    Object _head_2 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance(w.getName()));
    final EntityType ew = ((EntityType) _head_2);
    Object _head_3 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance(s.getName()));
    final EntityType es = ((EntityType) _head_3);
    final int esFeaturesSize1 = es.getFeatures().size();
    this.mapping.weakTable2Aggregate(w);
    final int esFeaturesSize2 = es.getFeatures().size();
    Object _head_4 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance("DetallesUsuario.Usuario_FK"));
    final Aggregate g = ((Aggregate) _head_4);
    Assertions.assertEquals((esFeaturesSize1 + 1), esFeaturesSize2);
    String _name = w.getName();
    String _plus = (_name + "s");
    Assertions.assertEquals(_plus, g.getName());
    Assertions.assertEquals(0, g.getLowerBound());
    Assertions.assertEquals((-1), g.getUpperBound());
    Assertions.assertEquals(ew, g.getSpecifiedBy());
    Assertions.assertFalse(ew.isRoot());
  }

  @Test
  public void mNTable2RelationshipType_OK() {
    Assertions.<Object>fail("Not yet implemented");
  }

  @Test
  public void r6Table1_1_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_6_1_1);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.mapping.table2SchemaType(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.mapping.column2Attribute(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.mapping.key2Key(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    Object _head = IterableExtensions.<Object>head(this.mapping.getTrace().getSourceInstance("Reserva"));
    final Table t = ((Table) _head);
    final FKey t_fk = IterableExtensions.<FKey>head(t.getFks());
    Object _head_1 = IterableExtensions.<Object>head(this.mapping.getTrace().getSourceInstance("Libro"));
    final Table s = ((Table) _head_1);
    Object _head_2 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance(t.getName()));
    final EntityType et = ((EntityType) _head_2);
    Object _head_3 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance(s.getName()));
    final EntityType es = ((EntityType) _head_3);
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.r6Table1_1(t, t_fk);
    final int etFeaturesSize2 = et.getFeatures().size();
    Object _head_4 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance("Reserva.Libro_FK"));
    final Reference rs = ((Reference) _head_4);
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    Assertions.assertTrue(et.getFeatures().contains(rs));
    String _name = s.getName();
    String _plus = (_name + "_");
    String _constraintname = t_fk.getConstraintname();
    String _plus_1 = (_plus + _constraintname);
    Assertions.assertEquals(_plus_1, rs.getName());
    Assertions.assertEquals(0, rs.getLowerBound());
    Assertions.assertEquals(1, rs.getUpperBound());
    Assertions.assertEquals(es, rs.getRefsTo());
    Assertions.assertTrue(rs.getAttributes().containsAll(this.mapping.columns2Attributes(t_fk.getColumns())));
  }

  @Test
  public void r6Table1_N_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_6_1_N);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.mapping.table2SchemaType(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Consumer<Table> _function_1 = (Table t) -> {
      final Consumer<Column> _function_2 = (Column c) -> {
        this.mapping.column2Attribute(c);
      };
      t.getColumns().forEach(_function_2);
      final Consumer<Key> _function_3 = (Key k) -> {
        this.mapping.key2Key(k);
      };
      t.getKeys().forEach(_function_3);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_1);
    Object _head = IterableExtensions.<Object>head(this.mapping.getTrace().getSourceInstance("Reserva"));
    final Table t = ((Table) _head);
    final FKey t_fk = IterableExtensions.<FKey>head(t.getFks());
    Object _head_1 = IterableExtensions.<Object>head(this.mapping.getTrace().getSourceInstance("Libro"));
    final Table s = ((Table) _head_1);
    Object _head_2 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance(t.getName()));
    final EntityType et = ((EntityType) _head_2);
    Object _head_3 = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance(s.getName()));
    final EntityType es = ((EntityType) _head_3);
    final int esFeaturesSize1 = es.getFeatures().size();
    this.mapping.r6Table1_N(t, t_fk);
    final int esFeaturesSize2 = es.getFeatures().size();
    final List<Object> features = this.mapping.getTrace().getTargetInstance("Reserva.Libro_FK");
    Reference rt = null;
    Attribute at = null;
    for (final Object ft : features) {
      boolean _matched = false;
      if (ft instanceof Reference) {
        _matched=true;
        rt = ((Reference)ft);
      }
      if (!_matched) {
        if (ft instanceof Attribute) {
          _matched=true;
          at = ((Attribute)ft);
        }
      }
    }
    final Column col = IterableExtensions.<Column>head(this.mapping.findPK(t).getColumns());
    Assertions.assertEquals((esFeaturesSize1 + 2), esFeaturesSize2);
    String _name = t.getName();
    String _plus = (_name + "s");
    Assertions.assertEquals(_plus, rt.getName());
    Assertions.assertEquals(0, rt.getLowerBound());
    Assertions.assertEquals((-1), rt.getUpperBound());
    Assertions.assertEquals(et, rt.getRefsTo());
    String _name_1 = col.getName();
    String _name_2 = et.getName();
    String _plus_1 = (_name_1 + _name_2);
    String _plus_2 = (_plus_1 + "s");
    Assertions.assertEquals(_plus_2, at.getName());
    DataType _type = at.getType();
    Assertions.assertEquals("int", ((PrimitiveType) _type).getName());
    Assertions.assertTrue(rt.getAttributes().contains(at));
    Assertions.assertTrue(at.getReferences().contains(rt));
    Assertions.assertTrue(es.getFeatures().contains(at));
  }
}
