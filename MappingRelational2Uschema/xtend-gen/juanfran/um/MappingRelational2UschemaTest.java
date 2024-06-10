package juanfran.um;

import com.google.common.base.Objects;
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
    Assertions.assertNotNull(et);
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
    Assertions.assertNotNull(rt);
    Assertions.assertEquals((numRelationships1 + 1), numRelationships2);
    Assertions.assertEquals(t.getName(), rt.getName());
  }

  @Test
  public void column2Attribute_EntityType_OK() {
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
    Assertions.assertNotNull(at);
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    Assertions.assertEquals(c.getName(), at.getName());
    DataType _type = at.getType();
    Assertions.assertEquals("int", ((PrimitiveType) _type).getName());
    Assertions.assertEquals(Boolean.valueOf(c.isNullable()), Boolean.valueOf(at.isOptional()));
  }

  @Test
  public void key2Key_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_3);
    this.mapping.relationalSchema2USchema();
    final Table t = IterableExtensions.<Table>head(this.mapping.getRelationalSchema().getTables());
    this.mapping.table2SchemaType(t);
    final Consumer<Column> _function = (Column c) -> {
      this.mapping.column2Attribute(c);
    };
    t.getColumns().forEach(_function);
    final Key pK = this.mapping.findPK(t);
    final Key uK = IterableExtensions.<Key>head(this.mapping.findUKs(t));
    final EntityType et = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.key2Key(pK);
    this.mapping.key2Key(uK);
    final int etFeaturesSize2 = et.getFeatures().size();
    Assertions.assertEquals((etFeaturesSize1 + 2), etFeaturesSize2);
    final Function1<Feature, Boolean> _function_1 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Usuario_PK"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_1);
    final uschema.Key u_pk = ((uschema.Key) _findFirst);
    Assertions.assertNotNull(u_pk);
    Assertions.assertEquals(pK.getConstraintname(), u_pk.getName());
    Assertions.assertTrue(u_pk.isIsID());
    Assertions.assertEquals(pK.getColumns().size(), u_pk.getAttributes().size());
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "EmailTelefono_UK"));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_2);
    final uschema.Key u_uk = ((uschema.Key) _findFirst_1);
    Assertions.assertNotNull(u_uk);
    Assertions.assertEquals(uK.getConstraintname(), u_uk.getName());
    Assertions.assertFalse(u_uk.isIsID());
    Assertions.assertEquals(uK.getColumns().size(), u_uk.getAttributes().size());
  }

  @Test
  public void weakTable2Aggregate_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_4);
    this.mapping.relationalSchema2USchema();
    final Function1<Table, Boolean> _function = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "DetallesUsuario"));
    };
    final Table w = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function);
    final Function1<Table, Boolean> _function_1 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Usuario"));
    };
    final Table s = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_1);
    this.mapping.table2SchemaType(w);
    this.mapping.table2SchemaType(s);
    final Consumer<Table> _function_2 = (Table t) -> {
      final Consumer<Column> _function_3 = (Column c) -> {
        this.mapping.column2Attribute(c);
      };
      t.getColumns().forEach(_function_3);
      final Consumer<Key> _function_4 = (Key k) -> {
        this.mapping.key2Key(k);
      };
      t.getKeys().forEach(_function_4);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_2);
    final Function1<EntityType, Boolean> _function_3 = (EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "DetallesUsuario"));
    };
    final EntityType ew = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_3);
    final Function1<EntityType, Boolean> _function_4 = (EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Usuario"));
    };
    final EntityType es = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_4);
    final int esFeaturesSize1 = es.getFeatures().size();
    this.mapping.weakTable2Aggregate(w);
    final int esFeaturesSize2 = es.getFeatures().size();
    Object _head = IterableExtensions.<Object>head(this.mapping.getTrace().getTargetInstance("DetallesUsuario.Usuario_FK"));
    final Aggregate g = ((Aggregate) _head);
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

  @Test
  public void mNTable2RelationshipType_OK() {
    this.mapping.loadSchema(MappingRelational2UschemaTest.RELATIONAL_SCHEMA_5);
    this.mapping.relationalSchema2USchema();
    final Consumer<Table> _function = (Table t) -> {
      this.mapping.table2SchemaType(t);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function);
    final Function1<RelationshipType, Boolean> _function_1 = (RelationshipType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores"));
    };
    final RelationshipType rm = IterableExtensions.<RelationshipType>findFirst(this.mapping.getUSchema().getRelationships(), _function_1);
    final int rmNumFeatures1 = rm.getFeatures().size();
    final Consumer<Table> _function_2 = (Table t) -> {
      final Consumer<Column> _function_3 = (Column c) -> {
        this.mapping.column2Attribute(c);
      };
      t.getColumns().forEach(_function_3);
      final Consumer<Key> _function_4 = (Key k) -> {
        this.mapping.key2Key(k);
      };
      t.getKeys().forEach(_function_4);
    };
    this.mapping.getRelationalSchema().getTables().forEach(_function_2);
    final int rmNumFeatures2 = rm.getFeatures().size();
    Assertions.assertEquals((rmNumFeatures1 + 1), rmNumFeatures2);
    final Function1<Feature, Boolean> _function_3 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(rm.getFeatures(), _function_3);
    final Attribute libro = ((Attribute) _findFirst);
    final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Autor"));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(rm.getFeatures(), _function_4);
    final Attribute autor = ((Attribute) _findFirst_1);
    final Function1<Feature, Boolean> _function_5 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Editorial"));
    };
    Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(rm.getFeatures(), _function_5);
    final Attribute editorial = ((Attribute) _findFirst_2);
    Assertions.assertNull(libro);
    Assertions.assertNull(autor);
    Assertions.assertNotNull(editorial);
    final Function1<Feature, Boolean> _function_6 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores_PK"));
    };
    Feature _findFirst_3 = IterableExtensions.<Feature>findFirst(rm.getFeatures(), _function_6);
    final Key libros_Autores_PK = ((Key) _findFirst_3);
    Assertions.assertNull(libros_Autores_PK);
    final Function1<Table, Boolean> _function_7 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores"));
    };
    final Table m = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_7);
    final FKey fk1 = m.getFks().get(0);
    final FKey fk2 = m.getFks().get(1);
    final Function1<EntityType, Boolean> _function_8 = (EntityType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final EntityType et1 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_8);
    final Function1<EntityType, Boolean> _function_9 = (EntityType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Autor"));
    };
    final EntityType et2 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_9);
    final int rmNumReferences1 = rm.getReference().size();
    this.mapping.mNTable2RelationshipType(m);
    final int rmNumReferences2 = rm.getReference().size();
    Assertions.assertEquals((rmNumReferences1 + 2), rmNumReferences2);
    final Function1<Feature, Boolean> _function_10 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Autor_FK"));
    };
    Feature _findFirst_4 = IterableExtensions.<Feature>findFirst(et1.getFeatures(), _function_10);
    final Reference ref1 = ((Reference) _findFirst_4);
    Assertions.assertNotNull(ref1);
    Assertions.assertEquals(fk2.getConstraintname(), ref1.getName());
    Assertions.assertEquals(1, ref1.getLowerBound());
    Assertions.assertEquals((-1), ref1.getUpperBound());
    Assertions.assertTrue(rm.getReference().contains(ref1));
    final Function1<Feature, Boolean> _function_11 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "AutorID"));
    };
    Feature _findFirst_5 = IterableExtensions.<Feature>findFirst(et2.getFeatures(), _function_11);
    final Attribute at1 = ((Attribute) _findFirst_5);
    Assertions.assertNotNull(at1);
    Assertions.assertTrue(ref1.getAttributes().contains(at1));
    final Function1<Feature, Boolean> _function_12 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro_FK"));
    };
    Feature _findFirst_6 = IterableExtensions.<Feature>findFirst(et2.getFeatures(), _function_12);
    final Reference ref2 = ((Reference) _findFirst_6);
    Assertions.assertNotNull(ref2);
    Assertions.assertEquals(fk1.getConstraintname(), ref2.getName());
    Assertions.assertEquals(1, ref2.getLowerBound());
    Assertions.assertEquals((-1), ref2.getUpperBound());
    Assertions.assertTrue(rm.getReference().contains(ref2));
    final Function1<Feature, Boolean> _function_13 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "LibroID"));
    };
    Feature _findFirst_7 = IterableExtensions.<Feature>findFirst(et1.getFeatures(), _function_13);
    final Attribute at2 = ((Attribute) _findFirst_7);
    Assertions.assertNotNull(at2);
    Assertions.assertTrue(ref2.getAttributes().contains(at2));
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
    final Function1<Table, Boolean> _function_2 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Reserva"));
    };
    final Table t = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_2);
    final FKey t_fk = IterableExtensions.<FKey>head(t.getFks());
    final Function1<Table, Boolean> _function_3 = (Table table) -> {
      String _name = table.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final Table s = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_3);
    final Function1<EntityType, Boolean> _function_4 = (EntityType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Reserva"));
    };
    final EntityType et = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_4);
    final Function1<EntityType, Boolean> _function_5 = (EntityType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final EntityType es = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_5);
    final int etFeaturesSize1 = et.getFeatures().size();
    this.mapping.r6Table1_1(t, t_fk);
    final int etFeaturesSize2 = et.getFeatures().size();
    Assertions.assertEquals((etFeaturesSize1 + 1), etFeaturesSize2);
    final Function1<Feature, Boolean> _function_6 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro_Libro_FK"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_6);
    final Reference rs = ((Reference) _findFirst);
    Assertions.assertNotNull(rs);
    Assertions.assertTrue(et.getFeatures().contains(rs));
    String _name = s.getName();
    String _plus = (_name + "_");
    String _constraintname = t_fk.getConstraintname();
    String _plus_1 = (_plus + _constraintname);
    Assertions.assertEquals(_plus_1, rs.getName());
    Assertions.assertEquals(0, rs.getLowerBound());
    Assertions.assertEquals(1, rs.getUpperBound());
    Assertions.assertEquals(es, rs.getRefsTo());
    final Function1<Feature, Boolean> _function_7 = (Feature f) -> {
      String _name_1 = f.getName();
      return Boolean.valueOf(Objects.equal(_name_1, "Libro"));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(et.getFeatures(), _function_7);
    final Attribute at = ((Attribute) _findFirst_1);
    Assertions.assertNotNull(at);
    Assertions.assertTrue(rs.getAttributes().contains(at));
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
    final Function1<Table, Boolean> _function_2 = (Table t) -> {
      String _name = t.getName();
      return Boolean.valueOf(Objects.equal(_name, "Reserva"));
    };
    final Table t = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_2);
    final FKey t_fk = IterableExtensions.<FKey>head(t.getFks());
    final Function1<Table, Boolean> _function_3 = (Table table) -> {
      String _name = table.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final Table s = IterableExtensions.<Table>findFirst(this.mapping.getRelationalSchema().getTables(), _function_3);
    final Function1<EntityType, Boolean> _function_4 = (EntityType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Reserva"));
    };
    final EntityType et = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_4);
    final Function1<EntityType, Boolean> _function_5 = (EntityType r) -> {
      String _name = r.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final EntityType es = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_5);
    final int esFeaturesSize1 = es.getFeatures().size();
    this.mapping.r6Table1_N(t, t_fk);
    final int esFeaturesSize2 = es.getFeatures().size();
    Assertions.assertEquals((esFeaturesSize1 + 2), esFeaturesSize2);
    final Function1<Feature, Boolean> _function_6 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Reference));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(es.getFeatures(), _function_6);
    final Reference rt = ((Reference) _findFirst);
    Assertions.assertNotNull(rt);
    String _name = t.getName();
    String _plus = (_name + "s");
    Assertions.assertEquals(_plus, rt.getName());
    Assertions.assertEquals(0, rt.getLowerBound());
    Assertions.assertEquals((-1), rt.getUpperBound());
    Assertions.assertEquals(et, rt.getRefsTo());
    final Function1<Column, Boolean> _function_7 = (Column c) -> {
      String _name_1 = c.getName();
      return Boolean.valueOf(Objects.equal(_name_1, "ReservaID"));
    };
    final Column col = IterableExtensions.<Column>findFirst(t.getColumns(), _function_7);
    final Function1<Feature, Boolean> _function_8 = (Feature f) -> {
      String _name_1 = f.getName();
      return Boolean.valueOf(Objects.equal(_name_1, "ReservaIDReservas"));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(es.getFeatures(), _function_8);
    Attribute at = ((Attribute) _findFirst_1);
    Assertions.assertNotNull(at);
    String _name_1 = col.getName();
    String _name_2 = et.getName();
    String _plus_1 = (_name_1 + _name_2);
    String _plus_2 = (_plus_1 + "s");
    Assertions.assertEquals(_plus_2, at.getName());
    DataType _type = at.getType();
    Assertions.assertEquals("int", ((PrimitiveType) _type).getName());
    Assertions.assertTrue(rt.getAttributes().contains(at));
    Assertions.assertTrue(at.getReferences().contains(rt));
  }
}
