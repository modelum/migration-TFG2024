package es.um.dsdm.uschema2relational.test;

import es.um.dsdm.uschema2relational.MappingUSchema2Relational;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import relationalschema.ReferentialAction;
import relationalschema.Table;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.EntityType;
import uschema.Feature;
import uschema.Key;
import uschema.Reference;

@SuppressWarnings("all")
public class MappingUSchema2RelationalTest {
  private static final String USCHEMA_0 = "inputs/USchema-Regla0.xmi";

  private static final String USCHEMA_1 = "inputs/USchema-Regla1.xmi";

  private static final String USCHEMA_2 = "inputs/USchema-Regla2.xmi";

  private static final String USCHEMA_3 = "inputs/USchema-Regla3.xmi";

  private static final String USCHEMA_4 = "inputs/USchema-Regla4.xmi";

  private static final String USCHEMA_5 = "inputs/USchema-Regla5.xmi";

  private static final String USCHEMA_6 = "inputs/USchema-Regla6.xmi";

  private MappingUSchema2Relational mapper;

  @Before
  public void setUp() {
    MappingUSchema2Relational _mappingUSchema2Relational = new MappingUSchema2Relational();
    this.mapper = _mappingUSchema2Relational;
  }

  @Test
  public void testUSchema2RelationalSchema() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_0);
    this.mapper.uschema2relational();
    Assert.assertNotNull(this.mapper.getRelationalSchema());
    Assert.assertEquals(this.mapper.getRelationalSchema().getName(), this.mapper.getUschema().getName());
  }

  @Test
  public void testEntityType2Table() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_1);
    this.mapper.uschema2relational();
    final EntityType entidad1 = this.mapper.getUschema().getEntities().get(0);
    final EntityType entidad2 = this.mapper.getUschema().getEntities().get(1);
    this.mapper.entityType2Table(entidad1);
    this.mapper.entityType2Table(entidad2);
    Assert.assertEquals(entidad1.getName(), this.mapper.getRelationalSchema().getTables().get(0).getName());
    Assert.assertEquals(entidad2.getName(), this.mapper.getRelationalSchema().getTables().get(1).getName());
  }

  @Test
  public void testAttribute2Column() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_2);
    this.mapper.uschema2relational();
    final EntityType entidad1 = this.mapper.getUschema().getEntities().get(0);
    final EntityType entidad2 = this.mapper.getUschema().getEntities().get(1);
    this.mapper.entityType2Table(entidad1);
    Feature _get = entidad1.getFeatures().get(0);
    final Attribute atributo1 = ((Attribute) _get);
    this.mapper.attribute2Column(atributo1);
    Assert.assertEquals(atributo1.getName(), this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(0).getName());
    Assert.assertEquals(255, this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(0).getSize());
    Assert.assertTrue(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(0).isNullable());
    Assert.assertNull(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(0).getDefaultvalue());
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(0).getDatatype(), "VARCHAR");
    Feature _get_1 = entidad1.getFeatures().get(1);
    final Attribute atributo2 = ((Attribute) _get_1);
    this.mapper.attribute2Column(atributo2);
    Assert.assertEquals(atributo2.getName(), this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(1).getName());
    Assert.assertEquals(38, this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(1).getSize());
    Assert.assertTrue(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(1).isNullable());
    Assert.assertNull(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(1).getDefaultvalue());
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(1).getDatatype(), "NUMERIC");
    Feature _get_2 = entidad1.getFeatures().get(2);
    final Attribute atributo3 = ((Attribute) _get_2);
    this.mapper.attribute2Column(atributo3);
    Assert.assertEquals(atributo3.getName(), this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(2).getName());
    Assert.assertEquals(38, this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(2).getSize());
    Assert.assertTrue(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(2).isNullable());
    Assert.assertNull(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(2).getDefaultvalue());
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(0).getColumns().get(2).getDatatype(), "NUMERIC");
    this.mapper.entityType2Table(entidad2);
    Feature _get_3 = entidad2.getFeatures().get(0);
    final Attribute atributo4 = ((Attribute) _get_3);
    this.mapper.attribute2Column(atributo4);
    Assert.assertEquals(atributo4.getName(), this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(0).getName());
    Assert.assertEquals(10, this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(0).getSize());
    Assert.assertTrue(this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(0).isNullable());
    Assert.assertNull(this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(0).getDefaultvalue());
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(0).getDatatype(), "DATE");
    Feature _get_4 = entidad2.getFeatures().get(1);
    final Attribute atributo5 = ((Attribute) _get_4);
    this.mapper.attribute2Column(atributo5);
    Assert.assertEquals(atributo5.getName(), this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(1).getName());
    Assert.assertEquals(1, this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(1).getSize());
    Assert.assertTrue(this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(1).isNullable());
    Assert.assertNull(this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(1).getDefaultvalue());
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(1).getColumns().get(1).getDatatype(), "BOOLEAN");
  }

  @Test
  public void testKey2Key() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_3);
    this.mapper.uschema2relational();
    final EntityType entidad1 = this.mapper.getUschema().getEntities().get(0);
    this.mapper.entityType2Table(entidad1);
    final List<Key> keys = CollectionLiterals.<Key>newArrayList();
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Key)) {
        keys.add(((Key)feature));
        this.mapper.key2key(((Key) feature));
      } else {
        if ((feature instanceof Attribute)) {
          this.mapper.attribute2Column(((Attribute) feature));
        }
      }
    };
    entidad1.getFeatures().forEach(_function);
    final Table tabla = this.mapper.getRelationalSchema().getTables().get(0);
    final relationalschema.Key key1 = tabla.getKeys().get(0);
    Assert.assertTrue(key1.isIsPK());
    Assert.assertEquals(tabla, key1.getOwner());
    String _constraintname = key1.getConstraintname();
    String _name = tabla.getName();
    String _plus = (_name + "_pk");
    Assert.assertEquals(_constraintname, _plus);
    Assert.assertEquals(key1.getColumns().size(), 1);
    final relationalschema.Key key2 = tabla.getKeys().get(1);
    Assert.assertFalse(key2.isIsPK());
    Assert.assertEquals(tabla, key2.getOwner());
    String _constraintname_1 = key2.getConstraintname();
    String _name_1 = keys.get(1).getName();
    String _plus_1 = (_name_1 + "_ak");
    Assert.assertEquals(_constraintname_1, _plus_1);
    Assert.assertEquals(key2.getColumns().size(), 2);
  }

  @Test
  public void testReference2Fkey() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_4);
    this.mapper.uschema2relational();
    final EntityType entidad1 = this.mapper.getUschema().getEntities().get(0);
    this.mapper.entityType2Table(entidad1);
    final EntityType entidad2 = this.mapper.getUschema().getEntities().get(1);
    this.mapper.entityType2Table(entidad2);
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        this.mapper.attribute2Column(((Attribute) feature));
      }
    };
    entidad1.getFeatures().forEach(_function);
    final Consumer<Feature> _function_1 = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        this.mapper.attribute2Column(((Attribute) feature));
      }
    };
    entidad2.getFeatures().forEach(_function_1);
    final Consumer<Feature> _function_2 = (Feature feature) -> {
      if ((feature instanceof Key)) {
        this.mapper.key2key(((Key) feature));
      }
    };
    entidad1.getFeatures().forEach(_function_2);
    final Consumer<Feature> _function_3 = (Feature feature) -> {
      if ((feature instanceof Key)) {
        this.mapper.key2key(((Key) feature));
      }
    };
    entidad2.getFeatures().forEach(_function_3);
    final Consumer<Feature> _function_4 = (Feature feature) -> {
      if ((feature instanceof Reference)) {
        this.mapper.reference2fkey(((Reference) feature));
      }
    };
    entidad1.getFeatures().forEach(_function_4);
    final Consumer<Feature> _function_5 = (Feature feature) -> {
      if ((feature instanceof Reference)) {
        this.mapper.reference2fkey(((Reference) feature));
      }
    };
    entidad2.getFeatures().forEach(_function_5);
    final Table tabla1 = this.mapper.getRelationalSchema().getTables().get(0);
    final Table tabla2 = this.mapper.getRelationalSchema().getTables().get(1);
    Feature _get = entidad1.getFeatures().get(0);
    final Reference referencia1 = ((Reference) _get);
    String _constraintname = tabla1.getFks().get(0).getConstraintname();
    String _name = this.mapper.entityType2Table(referencia1.getRefsTo()).getName();
    String _plus = (_name + "_fk");
    Assert.assertEquals(_constraintname, _plus);
    Assert.assertEquals(tabla1.getFks().get(0).getOwner(), tabla1);
    Assert.assertEquals(tabla1.getFks().get(0).getRefsTo(), tabla2.getKeys().get(0));
    Assert.assertEquals(tabla1.getFks().get(0).getOnDelete(), ReferentialAction.NO_ACTION);
    Assert.assertEquals(tabla1.getFks().get(0).getOnUpdate(), ReferentialAction.CASCADE);
    Assert.assertEquals(2, tabla1.getFks().get(0).getColumns().size());
    Assert.assertEquals(tabla1.getFks().get(0).getColumns().get(0).getName(), "atributo2");
    Assert.assertEquals(tabla1.getFks().get(0).getColumns().get(1).getName(), "atributo1");
    Feature _get_1 = entidad2.getFeatures().get(0);
    final Reference referencia2 = ((Reference) _get_1);
    String _constraintname_1 = tabla2.getFks().get(0).getConstraintname();
    String _name_1 = this.mapper.entityType2Table(referencia2.getRefsTo()).getName();
    String _plus_1 = (_name_1 + "_fk");
    Assert.assertEquals(_constraintname_1, _plus_1);
    Assert.assertEquals(tabla2.getFks().get(0).getOwner(), tabla2);
    Assert.assertEquals(tabla2.getFks().get(0).getRefsTo(), tabla1.getKeys().get(0));
    Assert.assertEquals(tabla2.getFks().get(0).getOnDelete(), ReferentialAction.NO_ACTION);
    Assert.assertEquals(tabla2.getFks().get(0).getOnUpdate(), ReferentialAction.CASCADE);
    Assert.assertEquals(1, tabla2.getFks().get(0).getColumns().size());
    Assert.assertEquals(tabla2.getFks().get(0).getColumns().get(0).getName(), "atributo1");
  }

  @Test
  public void testAggregate2WeakTable() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_5);
    this.mapper.uschema2relational();
    final EntityType entidad1 = this.mapper.getUschema().getEntities().get(0);
    final Table tabla1 = this.mapper.entityType2Table(entidad1);
    final EntityType entidad2 = this.mapper.getUschema().getEntities().get(1);
    final Table tabla2 = this.mapper.entityType2Table(entidad2);
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        this.mapper.attribute2Column(((Attribute) feature));
      }
    };
    entidad1.getFeatures().forEach(_function);
    final Consumer<Feature> _function_1 = (Feature feature) -> {
      if ((feature instanceof Key)) {
        this.mapper.key2key(((Key) feature));
      }
    };
    entidad1.getFeatures().forEach(_function_1);
    final Consumer<Feature> _function_2 = (Feature feature) -> {
      if ((feature instanceof Aggregate)) {
        this.mapper.aggregate2weakTable(((Aggregate) feature));
      }
    };
    entidad1.getFeatures().forEach(_function_2);
    Assert.assertEquals(1, tabla2.getFks().size());
    String _constraintname = tabla2.getFks().get(0).getConstraintname();
    String _name = tabla1.getName();
    String _plus = (_name + "_fk");
    Assert.assertEquals(_constraintname, _plus);
    Assert.assertEquals(tabla2.getFks().get(0).getRefsTo(), tabla1.getKeys().get(0));
    Assert.assertEquals(2, tabla2.getFks().get(0).getColumns().size());
    Assert.assertEquals(tabla2.getFks().get(0).getColumns().get(0), tabla1.getKeys().get(0).getColumns().get(0));
    Assert.assertEquals(tabla2.getFks().get(0).getColumns().get(1), tabla1.getKeys().get(0).getColumns().get(1));
    Assert.assertEquals(tabla2.getFks().get(0).getOnDelete(), ReferentialAction.NO_ACTION);
    Assert.assertEquals(tabla2.getFks().get(0).getOnUpdate(), ReferentialAction.CASCADE);
    Assert.assertEquals(1, tabla2.getKeys().size());
    Assert.assertEquals(1, tabla2.getColumns().size());
    String _constraintname_1 = tabla2.getKeys().get(0).getConstraintname();
    String _name_1 = tabla2.getName();
    String _plus_1 = (_name_1 + "_pk");
    Assert.assertEquals(_constraintname_1, _plus_1);
    Assert.assertTrue(tabla2.getKeys().get(0).isIsPK());
    Assert.assertEquals(tabla2.getColumns().get(0).getName(), "id");
    Assert.assertEquals(tabla2.getColumns().get(0).getDatatype(), "NUMERIC");
    Assert.assertEquals(tabla2.getColumns().get(0).getSize(), 38);
    Assert.assertFalse(tabla2.getColumns().get(0).isNullable());
    Assert.assertEquals(tabla2.getColumns().get(0).getDefaultvalue(), "1");
    Assert.assertEquals(3, tabla2.getKeys().get(0).getColumns().size());
  }

  @Test
  public void testRelationshipType2Table() {
    this.mapper.loadSchema(MappingUSchema2RelationalTest.USCHEMA_6);
    this.mapper.uschema2relational();
    this.mapper.entityType2Table(this.mapper.getUschema().getEntities().get(0));
    this.mapper.entityType2Table(this.mapper.getUschema().getEntities().get(1));
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        this.mapper.attribute2Column(((Attribute) feature));
      }
    };
    this.mapper.getUschema().getEntities().get(0).getFeatures().forEach(_function);
    final Consumer<Feature> _function_1 = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        this.mapper.attribute2Column(((Attribute) feature));
      }
    };
    this.mapper.getUschema().getEntities().get(1).getFeatures().forEach(_function_1);
    final Consumer<Feature> _function_2 = (Feature feature) -> {
      if ((feature instanceof Key)) {
        this.mapper.key2key(((Key) feature));
      }
    };
    this.mapper.getUschema().getEntities().get(0).getFeatures().forEach(_function_2);
    final Consumer<Feature> _function_3 = (Feature feature) -> {
      if ((feature instanceof Key)) {
        this.mapper.key2key(((Key) feature));
      }
    };
    this.mapper.getUschema().getEntities().get(1).getFeatures().forEach(_function_3);
    final Consumer<Feature> _function_4 = (Feature feature) -> {
      if ((feature instanceof Reference)) {
        this.mapper.reference2fkey(((Reference) feature));
      }
    };
    this.mapper.getUschema().getEntities().get(0).getFeatures().forEach(_function_4);
    final Consumer<Feature> _function_5 = (Feature feature) -> {
      if ((feature instanceof Reference)) {
        this.mapper.reference2fkey(((Reference) feature));
      }
    };
    this.mapper.getUschema().getEntities().get(1).getFeatures().forEach(_function_5);
    this.mapper.relationshipType2Table(this.mapper.getUschema().getRelationships().get(0));
    Assert.assertEquals(3, this.mapper.getRelationalSchema().getTables().size());
    Assert.assertEquals("Comprar", this.mapper.getRelationalSchema().getTables().get(2).getName());
    Assert.assertEquals(2, this.mapper.getRelationalSchema().getTables().get(2).getColumns().size());
    Assert.assertEquals(2, this.mapper.getRelationalSchema().getTables().get(2).getFks().size());
    String _name = this.mapper.getRelationalSchema().getTables().get(0).getName();
    String _plus = (_name + "_fk");
    Assert.assertEquals(_plus, this.mapper.getRelationalSchema().getTables().get(2).getFks().get(0).getConstraintname());
    String _name_1 = this.mapper.getRelationalSchema().getTables().get(1).getName();
    String _plus_1 = (_name_1 + "_fk");
    Assert.assertEquals(_plus_1, this.mapper.getRelationalSchema().getTables().get(2).getFks().get(1).getConstraintname());
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(0).getRefsTo(), this.mapper.getRelationalSchema().getTables().get(0).getKeys().get(0));
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(1).getRefsTo(), this.mapper.getRelationalSchema().getTables().get(1).getKeys().get(0));
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(0).getColumns().size(), 2);
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(0).getColumns().get(0), this.mapper.getRelationalSchema().getTables().get(0).getKeys().get(0).getColumns().get(0));
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(0).getColumns().get(1), this.mapper.getRelationalSchema().getTables().get(0).getKeys().get(0).getColumns().get(1));
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(1).getColumns().size(), 2);
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(1).getColumns().get(0), this.mapper.getRelationalSchema().getTables().get(1).getKeys().get(0).getColumns().get(0));
    Assert.assertEquals(this.mapper.getRelationalSchema().getTables().get(2).getFks().get(1).getColumns().get(1), this.mapper.getRelationalSchema().getTables().get(1).getKeys().get(0).getColumns().get(1));
    String _constraintname = this.mapper.getRelationalSchema().getTables().get(2).getKeys().get(0).getConstraintname();
    String _name_2 = this.mapper.getRelationalSchema().getTables().get(2).getName();
    String _plus_2 = (_name_2 + "_pk");
    Assert.assertEquals(_constraintname, _plus_2);
    Assert.assertTrue(this.mapper.getRelationalSchema().getTables().get(2).getKeys().get(0).isIsPK());
    Assert.assertEquals(4, this.mapper.getRelationalSchema().getTables().get(2).getKeys().get(0).getColumns().size());
  }
}
