package juanfran.um;

import com.google.common.base.Objects;
import documentschema.Array;
import documentschema.DataType;
import documentschema.PrimitiveType;
import documentschema.Property;
import documentschema.Type;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

@SuppressWarnings("all")
public class MappingUschema2DocumentTest {
  private static final String USCHEMA_0 = "test-input-files/USchema_0.xmi";

  private static final String USCHEMA_1 = "test-input-files/USchema_1.xmi";

  private static final String USCHEMA_2 = "test-input-files/USchema_2.xmi";

  private static final String USCHEMA_3 = "test-input-files/USchema_3.xmi";

  private static final String USCHEMA_4_1 = "test-input-files/USchema_4-1.xmi";

  private static final String USCHEMA_4_2 = "test-input-files/USchema_4-2.xmi";

  private static final String USCHEMA_5 = "test-input-files/USchema_5.xmi";

  private static final String USCHEMA_6 = "test-input-files/USchema_6.xmi";

  private static final String USCHEMA_7 = "test-input-files/USchema_7.xmi";

  private final MappingUschema2Document mapping = new MappingUschema2Document();

  @Test
  public void uSchema2DocumentSchema_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_0);
    this.mapping.uSchema2DocumentSchema();
    Assertions.assertNotNull(this.mapping.getDocumentSchema());
    Assertions.assertEquals(this.mapping.getUSchema().getName(), this.mapping.getDocumentSchema().getName());
  }

  @Test
  public void entityType2EntityType_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_1);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    final int numEntities1 = this.mapping.getDocumentSchema().getEntities().size();
    this.mapping.entityType2EntityType(e);
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>head(this.mapping.getDocumentSchema().getEntities());
    final int numEntities2 = this.mapping.getDocumentSchema().getEntities().size();
    Assertions.assertNotNull(d);
    Assertions.assertEquals((numEntities1 + 1), numEntities2);
    Assertions.assertEquals(e.getName(), d.getName());
  }

  @Test
  public void attribute2Attribute_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_2);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.mapping.entityType2EntityType(e);
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>head(this.mapping.getDocumentSchema().getEntities());
    Feature _head = IterableExtensions.<Feature>head(e.getFeatures());
    final Attribute f_at = ((Attribute) _head);
    final int numProperties1 = d.getProperties().size();
    this.mapping.attribute2Attribute(f_at, d);
    Property _head_1 = IterableExtensions.<Property>head(d.getProperties());
    final documentschema.Attribute p_at = ((documentschema.Attribute) _head_1);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_at);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Assertions.assertEquals(f_at.getName(), p_at.getName());
    Type _type = p_at.getType();
    Assertions.assertEquals(DataType.INTEGER, ((PrimitiveType) _type).getDatatype());
  }

  @Test
  public void aggregate2Aggregate_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_3);
    this.mapping.uSchema2DocumentSchema();
    final Function1<EntityType, Boolean> _function = (EntityType et) -> {
      return Boolean.valueOf(et.isRoot());
    };
    final EntityType e = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    this.mapping.entityType2EntityType(e);
    final Function1<documentschema.EntityType, Boolean> _function_1 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Usuario"));
    };
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_1);
    Feature _head = IterableExtensions.<Feature>head(e.getFeatures());
    final Aggregate f_ag = ((Aggregate) _head);
    SchemaType _specifiedBy = f_ag.getSpecifiedBy();
    final EntityType w = ((EntityType) _specifiedBy);
    Feature _head_1 = IterableExtensions.<Feature>head(w.getFeatures());
    final Attribute f_at = ((Attribute) _head_1);
    final int numProperties1 = d.getProperties().size();
    this.mapping.aggregate2Aggregate(f_ag, d);
    Property _head_2 = IterableExtensions.<Property>head(d.getProperties());
    final documentschema.Aggregate p_ag = ((documentschema.Aggregate) _head_2);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_ag);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Assertions.assertEquals(f_ag.getName(), p_ag.getName());
    Assertions.assertTrue(p_ag.isIsMany());
    Property _head_3 = IterableExtensions.<Property>head(p_ag.getAggregates());
    final documentschema.Attribute p_at = ((documentschema.Attribute) _head_3);
    Assertions.assertNotNull(p_at);
    Assertions.assertEquals(1, p_ag.getAggregates().size());
    Assertions.assertEquals(f_at.getName(), p_at.getName());
    Type _type = p_at.getType();
    Assertions.assertEquals(DataType.INTEGER, ((PrimitiveType) _type).getDatatype());
  }

  @Test
  public void key2Attribute_Simple_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_4_1);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.mapping.entityType2EntityType(e);
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>head(this.mapping.getDocumentSchema().getEntities());
    final Function1<Feature, Boolean> _function = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function);
    final Attribute f_at = ((Attribute) _findFirst);
    final int numProperties1 = d.getProperties().size();
    this.mapping.attribute2Attribute(f_at, d);
    Property _head = IterableExtensions.<Property>head(d.getProperties());
    final documentschema.Attribute key_at = ((documentschema.Attribute) _head);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(key_at);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    String _name = f_at.getName();
    String _plus = (_name + "_id");
    Assertions.assertEquals(_plus, key_at.getName());
    Assertions.assertTrue(key_at.isIsKey());
  }

  @Test
  public void key2Attribute_Composite_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_4_2);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.mapping.entityType2EntityType(e);
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>head(this.mapping.getDocumentSchema().getEntities());
    EList<Feature> _features = e.getFeatures();
    for (final Feature f : _features) {
      boolean _matched = false;
      if (f instanceof Attribute) {
        _matched=true;
        this.mapping.attribute2Attribute(((Attribute)f), d);
      }
    }
    final Function1<Feature, Boolean> _function = (Feature f_1) -> {
      String _name = f_1.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro_PK"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function);
    final Key f_key = ((Key) _findFirst);
    final int numProperties1 = d.getProperties().size();
    this.mapping.key2Attribute(f_key, d);
    final Function1<Property, Boolean> _function_1 = (Property p) -> {
      String _name = p.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro_id"));
    };
    Property _findFirst_1 = IterableExtensions.<Property>findFirst(d.getProperties(), _function_1);
    final documentschema.Attribute p_at = ((documentschema.Attribute) _findFirst_1);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_at);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    String _name = d.getName();
    String _plus = (_name + "_id");
    Assertions.assertEquals(_plus, p_at.getName());
    Type _type = p_at.getType();
    Assertions.assertEquals(DataType.STRING, ((PrimitiveType) _type).getDatatype());
    Assertions.assertTrue(p_at.isIsKey());
  }

  @Test
  public void reference2Reference_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_5);
    this.mapping.uSchema2DocumentSchema();
    final Function1<EntityType, Boolean> _function = (EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final EntityType e1 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final Function1<EntityType, Boolean> _function_1 = (EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Editor"));
    };
    final EntityType e2 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_1);
    this.mapping.entityType2EntityType(e1);
    this.mapping.entityType2EntityType(e2);
    final Function1<documentschema.EntityType, Boolean> _function_2 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final documentschema.EntityType d1 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_2);
    final Function1<documentschema.EntityType, Boolean> _function_3 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Editor"));
    };
    final documentschema.EntityType d2 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_3);
    final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_4);
    final Attribute f_at1 = ((Attribute) _findFirst);
    final Function1<Feature, Boolean> _function_5 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_5);
    final Attribute f_at2 = ((Attribute) _findFirst_1);
    this.mapping.attribute2Attribute(f_at1, d1);
    this.mapping.attribute2Attribute(f_at2, d2);
    final Function1<Feature, Boolean> _function_6 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Key));
    };
    Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_6);
    final Key f_key1 = ((Key) _findFirst_2);
    final Function1<Feature, Boolean> _function_7 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Key));
    };
    Feature _findFirst_3 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_7);
    final Key f_key2 = ((Key) _findFirst_3);
    this.mapping.key2Attribute(f_key1, d1);
    this.mapping.key2Attribute(f_key2, d2);
    final Function1<Feature, Boolean> _function_8 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Reference));
    };
    Feature _findFirst_4 = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_8);
    final Reference f_ref = ((Reference) _findFirst_4);
    final int numProperties1 = d1.getProperties().size();
    this.mapping.reference2Reference(f_ref, d1);
    final Function1<Property, Boolean> _function_9 = (Property p) -> {
      return Boolean.valueOf((p instanceof documentschema.Reference));
    };
    Property _findFirst_5 = IterableExtensions.<Property>findFirst(d1.getProperties(), _function_9);
    final documentschema.Reference p_ref = ((documentschema.Reference) _findFirst_5);
    final int numProperties2 = d1.getProperties().size();
    Assertions.assertNotNull(p_ref);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Assertions.assertEquals(f_ref.getName(), p_ref.getName());
    Assertions.assertEquals(d2, p_ref.getTarget());
    Type _type = p_ref.getType();
    Assertions.assertEquals(DataType.STRING, ((PrimitiveType) _type).getDatatype());
  }

  @Test
  public void relationshipType2EntityType_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_6);
    this.mapping.uSchema2DocumentSchema();
    final Function1<EntityType, Boolean> _function = (EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Autor"));
    };
    final EntityType e1 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
    final Function1<EntityType, Boolean> _function_1 = (EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final EntityType e2 = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function_1);
    this.mapping.entityType2EntityType(e1);
    this.mapping.entityType2EntityType(e2);
    final Function1<documentschema.EntityType, Boolean> _function_2 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Autor"));
    };
    final documentschema.EntityType d1 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_2);
    final Function1<documentschema.EntityType, Boolean> _function_3 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final documentschema.EntityType d2 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_3);
    final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_4);
    final Attribute f_at1 = ((Attribute) _findFirst);
    final Function1<Feature, Boolean> _function_5 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_5);
    final Attribute f_at2 = ((Attribute) _findFirst_1);
    this.mapping.attribute2Attribute(f_at1, d1);
    this.mapping.attribute2Attribute(f_at2, d2);
    final Function1<Feature, Boolean> _function_6 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Key));
    };
    Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_6);
    final Key f_key1 = ((Key) _findFirst_2);
    final Function1<Feature, Boolean> _function_7 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Key));
    };
    Feature _findFirst_3 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_7);
    final Key f_key2 = ((Key) _findFirst_3);
    this.mapping.key2Attribute(f_key1, d1);
    this.mapping.key2Attribute(f_key2, d2);
    final Function1<Feature, Boolean> _function_8 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Reference));
    };
    Feature _findFirst_4 = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_8);
    final Reference f_ref1 = ((Reference) _findFirst_4);
    final Function1<Feature, Boolean> _function_9 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Reference));
    };
    Feature _findFirst_5 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_9);
    final Reference f_ref2 = ((Reference) _findFirst_5);
    this.mapping.reference2Reference(f_ref1, d1);
    this.mapping.reference2Reference(f_ref2, d2);
    final Function1<Property, Boolean> _function_10 = (Property p) -> {
      return Boolean.valueOf((p instanceof documentschema.Reference));
    };
    Property _findFirst_6 = IterableExtensions.<Property>findFirst(d1.getProperties(), _function_10);
    Assertions.assertNull(((documentschema.Reference) _findFirst_6));
    final Function1<Property, Boolean> _function_11 = (Property p) -> {
      return Boolean.valueOf((p instanceof documentschema.Reference));
    };
    Property _findFirst_7 = IterableExtensions.<Property>findFirst(d2.getProperties(), _function_11);
    Assertions.assertNull(((documentschema.Reference) _findFirst_7));
    final RelationshipType rt = IterableExtensions.<RelationshipType>head(this.mapping.getUSchema().getRelationships());
    this.mapping.relationshipType2EntityType(rt);
    final Function1<documentschema.EntityType, Boolean> _function_12 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores"));
    };
    final documentschema.EntityType c = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_12);
    Assertions.assertNotNull(c);
    Assertions.assertEquals(rt.getName(), c.getName());
    Feature _head = IterableExtensions.<Feature>head(rt.getFeatures());
    final Attribute rt_at = ((Attribute) _head);
    final Function1<Property, Boolean> _function_13 = (Property p) -> {
      String _name = p.getName();
      return Boolean.valueOf(Objects.equal(_name, "Editorial"));
    };
    Property _findFirst_8 = IterableExtensions.<Property>findFirst(c.getProperties(), _function_13);
    final documentschema.Attribute c_at = ((documentschema.Attribute) _findFirst_8);
    Assertions.assertNotNull(c_at);
    Assertions.assertEquals(rt_at.getName(), c_at.getName());
    Type _type = c_at.getType();
    Assertions.assertEquals(DataType.STRING, ((PrimitiveType) _type).getDatatype());
    final Function1<Property, Boolean> _function_14 = (Property p) -> {
      String _name = p.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores_id"));
    };
    Property _findFirst_9 = IterableExtensions.<Property>findFirst(c.getProperties(), _function_14);
    final documentschema.Attribute at = ((documentschema.Attribute) _findFirst_9);
    Assertions.assertNotNull(at);
    String _name = c.getName();
    String _plus = (_name + "_id");
    Assertions.assertEquals(_plus, at.getName());
    Assertions.assertTrue(at.isIsKey());
    Type _type_1 = at.getType();
    Assertions.assertEquals(DataType.STRING, ((PrimitiveType) _type_1).getDatatype());
    final Function1<Property, Boolean> _function_15 = (Property p) -> {
      String _name_1 = p.getName();
      return Boolean.valueOf(Objects.equal(_name_1, "Libro_FK"));
    };
    Property _findFirst_10 = IterableExtensions.<Property>findFirst(c.getProperties(), _function_15);
    final documentschema.Reference p_ref1 = ((documentschema.Reference) _findFirst_10);
    final Function1<Property, Boolean> _function_16 = (Property p) -> {
      String _name_1 = p.getName();
      return Boolean.valueOf(Objects.equal(_name_1, "Autor_FK"));
    };
    Property _findFirst_11 = IterableExtensions.<Property>findFirst(c.getProperties(), _function_16);
    final documentschema.Reference p_ref2 = ((documentschema.Reference) _findFirst_11);
    Assertions.assertNotNull(p_ref1);
    Assertions.assertNotNull(p_ref2);
    Assertions.assertEquals(d1, p_ref2.getTarget());
    Assertions.assertEquals(d2, p_ref1.getTarget());
    Type _type_2 = p_ref1.getType();
    Assertions.assertEquals(DataType.STRING, ((PrimitiveType) _type_2).getDatatype());
    Type _type_3 = p_ref2.getType();
    Assertions.assertEquals(DataType.INTEGER, ((PrimitiveType) _type_3).getDatatype());
    Assertions.assertEquals(4, c.getProperties().size());
  }

  @Test
  public void datatype2Type_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_7);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.mapping.entityType2EntityType(e);
    final Function1<Feature, Boolean> _function = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "AutorID"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function);
    uschema.DataType _type = ((Attribute) _findFirst).getType();
    final uschema.PrimitiveType f_primitive = ((uschema.PrimitiveType) _type);
    final Function1<Feature, Boolean> _function_1 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "NombreCompleto"));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_1);
    uschema.DataType _type_1 = ((Attribute) _findFirst_1).getType();
    final PTuple f_tuple = ((PTuple) _type_1);
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Géneros"));
    };
    Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_2);
    uschema.DataType _type_2 = ((Attribute) _findFirst_2).getType();
    final PSet f_set = ((PSet) _type_2);
    final Function1<Feature, Boolean> _function_3 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros"));
    };
    Feature _findFirst_3 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_3);
    uschema.DataType _type_3 = ((Attribute) _findFirst_3).getType();
    final PList f_list = ((PList) _type_3);
    final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Premios"));
    };
    Feature _findFirst_4 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_4);
    uschema.DataType _type_4 = ((Attribute) _findFirst_4).getType();
    final PMap f_map = ((PMap) _type_4);
    final Type p_primitive = this.mapping.datatype2Type(f_primitive);
    final Type p_tuple = this.mapping.datatype2Type(f_tuple);
    final Type p_set = this.mapping.datatype2Type(f_set);
    final Type p_list = this.mapping.datatype2Type(f_list);
    final Type p_map = this.mapping.datatype2Type(f_map);
    Assertions.assertTrue((p_primitive instanceof PrimitiveType));
    Assertions.assertTrue((p_tuple instanceof Array));
    Assertions.assertTrue((p_set instanceof Array));
    Assertions.assertTrue((p_list instanceof Array));
    Assertions.assertTrue((p_map instanceof Array));
    Assertions.assertEquals(DataType.INTEGER, ((PrimitiveType) p_primitive).getDatatype());
    Assertions.assertEquals(DataType.STRING, ((Array) p_tuple).getType().getDatatype());
    Assertions.assertEquals(DataType.STRING, ((Array) p_set).getType().getDatatype());
    Assertions.assertEquals(DataType.STRING, ((Array) p_list).getType().getDatatype());
    Assertions.assertEquals(DataType.STRING, ((Array) p_map).getType().getDatatype());
  }
}
