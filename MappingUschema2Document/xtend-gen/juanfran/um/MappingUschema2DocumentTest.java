package juanfran.um;

import com.google.common.base.Objects;
import documentschema.Array;
import documentschema.Property;
import documentschema.Type;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.DataType;
import uschema.EntityType;
import uschema.Feature;
import uschema.Key;
import uschema.PList;
import uschema.PMap;
import uschema.PSet;
import uschema.PTuple;
import uschema.PrimitiveType;
import uschema.Reference;
import uschema.RelationshipType;
import uschema.SchemaType;

@SuppressWarnings("all")
public class MappingUschema2DocumentTest {
  private static final String USCHEMA_0 = "input-files/USchema_0.xmi";

  private static final String USCHEMA_1 = "input-files/USchema_1.xmi";

  private static final String USCHEMA_2 = "input-files/USchema_2.xmi";

  private static final String USCHEMA_3 = "input-files/USchema_3.xmi";

  private static final String USCHEMA_4_1 = "input-files/USchema_4-1.xmi";

  private static final String USCHEMA_4_2 = "input-files/USchema_4-2.xmi";

  private static final String USCHEMA_5 = "input-files/USchema_5.xmi";

  private static final String USCHEMA_6 = "input-files/USchema_6.xmi";

  private static final String USCHEMA_7 = "input-files/USchema_7.xmi";

  private static final String USCHEMA_INTEGRATION = "input-files/USchema_integration.xmi";

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
    this.entityType2EntityType_asserts(e);
  }

  @Test
  public void attribute2Attribute_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_2);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.entityType2EntityType_asserts(e);
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>head(this.mapping.getDocumentSchema().getEntities());
    Feature _head = IterableExtensions.<Feature>head(e.getFeatures());
    final Attribute f_at = ((Attribute) _head);
    this.attribute2Attribute_asserts(f_at, d);
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
    this.aggregate2Aggregate_asserts(f_ag, d);
  }

  @Test
  public void key2Attribute_Simple_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_4_1);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.entityType2EntityType_asserts(e);
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
    this.entityType2EntityType_asserts(e);
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>head(this.mapping.getDocumentSchema().getEntities());
    EList<Feature> _features = e.getFeatures();
    for (final Feature f : _features) {
      boolean _matched = false;
      if (f instanceof Attribute) {
        _matched=true;
        this.attribute2Attribute_asserts(((Attribute)f), d);
      }
    }
    final Function1<Feature, Boolean> _function = (Feature f_1) -> {
      String _name = f_1.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro_PK"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function);
    final Key f_key = ((Key) _findFirst);
    this.key2Attribute_asserts(f_key, d);
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
    final Consumer<EntityType> _function_2 = (EntityType e) -> {
      this.entityType2EntityType_asserts(e);
    };
    this.mapping.getUSchema().getEntities().forEach(_function_2);
    final Function1<documentschema.EntityType, Boolean> _function_3 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libro"));
    };
    final documentschema.EntityType d1 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_3);
    final Function1<documentschema.EntityType, Boolean> _function_4 = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Editor"));
    };
    final documentschema.EntityType d2 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_4);
    final Function1<Feature, Boolean> _function_5 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_5);
    final Attribute f_at1 = ((Attribute) _findFirst);
    final Function1<Feature, Boolean> _function_6 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Attribute));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_6);
    final Attribute f_at2 = ((Attribute) _findFirst_1);
    this.attribute2Attribute_asserts(f_at1, d1);
    this.attribute2Attribute_asserts(f_at2, d2);
    final Function1<Feature, Boolean> _function_7 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Key));
    };
    Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_7);
    final Key f_key1 = ((Key) _findFirst_2);
    final Function1<Feature, Boolean> _function_8 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Key));
    };
    Feature _findFirst_3 = IterableExtensions.<Feature>findFirst(e2.getFeatures(), _function_8);
    final Key f_key2 = ((Key) _findFirst_3);
    this.key2Attribute_asserts(f_key1, d1);
    this.key2Attribute_asserts(f_key2, d2);
    final Function1<Feature, Boolean> _function_9 = (Feature f) -> {
      return Boolean.valueOf((f instanceof Reference));
    };
    Feature _findFirst_4 = IterableExtensions.<Feature>findFirst(e1.getFeatures(), _function_9);
    final Reference f_ref = ((Reference) _findFirst_4);
    this.reference2Reference_asserts(f_ref, d1);
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
    this.entityType2EntityType_asserts(e1);
    this.entityType2EntityType_asserts(e2);
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
    this.attribute2Attribute_asserts(f_at1, d1);
    this.attribute2Attribute_asserts(f_at2, d2);
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
    this.key2Attribute_asserts(f_key1, d1);
    this.key2Attribute_asserts(f_key2, d2);
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
    this.reference2Reference_asserts(f_ref1, d1);
    this.reference2Reference_asserts(f_ref2, d2);
    final RelationshipType rt = IterableExtensions.<RelationshipType>head(this.mapping.getUSchema().getRelationships());
    this.relationshipType2EntityType_asserts(rt);
  }

  @Test
  public void datatype2Type_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_7);
    this.mapping.uSchema2DocumentSchema();
    final EntityType e = IterableExtensions.<EntityType>head(this.mapping.getUSchema().getEntities());
    this.entityType2EntityType_asserts(e);
    final Function1<Feature, Boolean> _function = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "AutorID"));
    };
    Feature _findFirst = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function);
    DataType _type = ((Attribute) _findFirst).getType();
    final PrimitiveType f_primitive = ((PrimitiveType) _type);
    final Function1<Feature, Boolean> _function_1 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "NombreCompleto"));
    };
    Feature _findFirst_1 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_1);
    DataType _type_1 = ((Attribute) _findFirst_1).getType();
    final PTuple f_tuple = ((PTuple) _type_1);
    final Function1<Feature, Boolean> _function_2 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Géneros"));
    };
    Feature _findFirst_2 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_2);
    DataType _type_2 = ((Attribute) _findFirst_2).getType();
    final PSet f_set = ((PSet) _type_2);
    final Function1<Feature, Boolean> _function_3 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros"));
    };
    Feature _findFirst_3 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_3);
    DataType _type_3 = ((Attribute) _findFirst_3).getType();
    final PList f_list = ((PList) _type_3);
    final Function1<Feature, Boolean> _function_4 = (Feature f) -> {
      String _name = f.getName();
      return Boolean.valueOf(Objects.equal(_name, "Premios"));
    };
    Feature _findFirst_4 = IterableExtensions.<Feature>findFirst(e.getFeatures(), _function_4);
    DataType _type_4 = ((Attribute) _findFirst_4).getType();
    final PMap f_map = ((PMap) _type_4);
    final Type p_primitive = this.mapping.datatype2Type(f_primitive);
    final Type p_tuple = this.mapping.datatype2Type(f_tuple);
    final Type p_set = this.mapping.datatype2Type(f_set);
    final Type p_list = this.mapping.datatype2Type(f_list);
    final Type p_map = this.mapping.datatype2Type(f_map);
    Assertions.assertTrue((p_primitive instanceof documentschema.PrimitiveType));
    Assertions.assertTrue((p_tuple instanceof Array));
    Assertions.assertTrue((p_set instanceof Array));
    Assertions.assertTrue((p_list instanceof Array));
    Assertions.assertTrue((p_map instanceof Array));
    Assertions.assertEquals(documentschema.DataType.INTEGER, ((documentschema.PrimitiveType) p_primitive).getDatatype());
    Assertions.assertEquals(documentschema.DataType.STRING, ((Array) p_tuple).getType().getDatatype());
    Assertions.assertEquals(documentschema.DataType.STRING, ((Array) p_set).getType().getDatatype());
    Assertions.assertEquals(documentschema.DataType.STRING, ((Array) p_list).getType().getDatatype());
    Assertions.assertEquals(documentschema.DataType.STRING, ((Array) p_map).getType().getDatatype());
  }

  @Test
  public void uSchema2DocumentSchema_integration_OK() {
    this.mapping.loadSchema(MappingUschema2DocumentTest.USCHEMA_INTEGRATION);
    this.mapping.uSchema2DocumentSchema();
    EList<EntityType> _entities = this.mapping.getUSchema().getEntities();
    for (final EntityType e : _entities) {
      boolean _isRoot = e.isRoot();
      if (_isRoot) {
        this.entityType2EntityType_asserts(e);
      }
    }
    EList<documentschema.EntityType> _entities_1 = this.mapping.getDocumentSchema().getEntities();
    for (final documentschema.EntityType d : _entities_1) {
      {
        final Function1<EntityType, Boolean> _function = (EntityType e_1) -> {
          String _name = e_1.getName();
          String _name_1 = d.getName();
          return Boolean.valueOf(Objects.equal(_name, _name_1));
        };
        EntityType _findFirst = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
        final EntityType uet = ((EntityType) _findFirst);
        EList<Feature> _features = uet.getFeatures();
        for (final Feature f : _features) {
          boolean _matched = false;
          if (f instanceof Attribute) {
            _matched=true;
            this.attribute2Attribute_asserts(((Attribute)f), d);
          }
        }
        EList<Feature> _features_1 = uet.getFeatures();
        for (final Feature f_1 : _features_1) {
          boolean _matched_1 = false;
          if (f_1 instanceof Key) {
            _matched_1=true;
            boolean _isIsID = ((Key)f_1).isIsID();
            if (_isIsID) {
              this.key2Attribute_asserts(((Key)f_1), d);
            }
          }
        }
      }
    }
    EList<documentschema.EntityType> _entities_2 = this.mapping.getDocumentSchema().getEntities();
    for (final documentschema.EntityType d_1 : _entities_2) {
      {
        final Function1<EntityType, Boolean> _function = (EntityType e_1) -> {
          String _name = e_1.getName();
          String _name_1 = d_1.getName();
          return Boolean.valueOf(Objects.equal(_name, _name_1));
        };
        EntityType _findFirst = IterableExtensions.<EntityType>findFirst(this.mapping.getUSchema().getEntities(), _function);
        final EntityType uet = ((EntityType) _findFirst);
        EList<Feature> _features = uet.getFeatures();
        for (final Feature f : _features) {
          boolean _matched = false;
          if (f instanceof Aggregate) {
            _matched=true;
            this.aggregate2Aggregate_asserts(((Aggregate)f), d_1);
          }
          if (!_matched) {
            if (f instanceof Reference) {
              _matched=true;
              this.reference2Reference_asserts(((Reference)f), d_1);
            }
          }
        }
      }
    }
    EList<RelationshipType> _relationships = this.mapping.getUSchema().getRelationships();
    for (final RelationshipType rt : _relationships) {
      this.relationshipType2EntityType_asserts(rt);
    }
  }

  private void entityType2EntityType_asserts(final EntityType e) {
    final int numEntities1 = this.mapping.getDocumentSchema().getEntities().size();
    this.mapping.entityType2EntityType(e);
    final Function1<documentschema.EntityType, Boolean> _function = (documentschema.EntityType et) -> {
      String _name = et.getName();
      String _name_1 = e.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final documentschema.EntityType d = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function);
    final int numEntities2 = this.mapping.getDocumentSchema().getEntities().size();
    Assertions.assertNotNull(d);
    Assertions.assertEquals((numEntities1 + 1), numEntities2);
  }

  private void attribute2Attribute_asserts(final Attribute f_at, final documentschema.EntityType d) {
    final int numProperties1 = d.getProperties().size();
    this.mapping.attribute2Attribute(f_at, d);
    documentschema.Attribute p_at = null;
    if ((((f_at.getKey() != null) && (f_at.getKey().getAttributes().size() == 1)) && f_at.getKey().isIsID())) {
      final Function1<Property, Boolean> _function = (Property f) -> {
        String _name = f.getName();
        String _name_1 = f_at.getName();
        String _plus = (_name_1 + "_id");
        return Boolean.valueOf(Objects.equal(_name, _plus));
      };
      Property _findFirst = IterableExtensions.<Property>findFirst(d.getProperties(), _function);
      p_at = ((documentschema.Attribute) _findFirst);
    } else {
      p_at = (p_at = ((documentschema.Attribute) IterableExtensions.<Property>findFirst(d.getProperties(), ((Function1<Property, Boolean>) (Property f) -> {
        String _name = f.getName();
        String _name_1 = f_at.getName();
        return Boolean.valueOf(Objects.equal(_name, _name_1));
      }))));
    }
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_at);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Assertions.assertEquals(this.mapping.datatype2Type(f_at.getType()), p_at.getType());
  }

  private void aggregate2Aggregate_asserts(final Aggregate f_ag, final documentschema.EntityType d) {
    SchemaType _specifiedBy = f_ag.getSpecifiedBy();
    final EntityType w = ((EntityType) _specifiedBy);
    final int numProperties1 = d.getProperties().size();
    this.mapping.aggregate2Aggregate(f_ag, d);
    final Function1<Property, Boolean> _function = (Property p) -> {
      String _name = p.getName();
      String _name_1 = f_ag.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    Property _findFirst = IterableExtensions.<Property>findFirst(d.getProperties(), _function);
    final documentschema.Aggregate p_ag = ((documentschema.Aggregate) _findFirst);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_ag);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Assertions.assertTrue(p_ag.isIsMany());
    EList<Feature> _features = w.getFeatures();
    for (final Feature f : _features) {
      boolean _matched = false;
      if (f instanceof Attribute) {
        _matched=true;
        documentschema.Attribute p_at = null;
        if ((((((Attribute)f).getKey() != null) && (((Attribute)f).getKey().getAttributes().size() == 1)) && ((Attribute)f).getKey().isIsID())) {
          final Function1<Property, Boolean> _function_1 = (Property at) -> {
            String _name = at.getName();
            String _name_1 = ((Attribute)f).getName();
            String _plus = (_name_1 + "_id");
            return Boolean.valueOf(Objects.equal(_name, _plus));
          };
          Property _findFirst_1 = IterableExtensions.<Property>findFirst(p_ag.getAggregates(), _function_1);
          p_at = ((documentschema.Attribute) _findFirst_1);
        } else {
          final Function1<Property, Boolean> _function_2 = (Property at) -> {
            String _name = at.getName();
            String _name_1 = ((Attribute)f).getName();
            return Boolean.valueOf(Objects.equal(_name, _name_1));
          };
          Property _findFirst_2 = IterableExtensions.<Property>findFirst(p_ag.getAggregates(), _function_2);
          p_at = ((documentschema.Attribute) _findFirst_2);
        }
        Assertions.assertNotNull(p_at);
        Assertions.assertEquals(1, p_ag.getAggregates().size());
        Assertions.assertEquals(this.mapping.datatype2Type(((Attribute)f).getType()), p_at.getType());
      }
    }
  }

  private void key2Attribute_asserts(final Key f_key, final documentschema.EntityType d) {
    int _size = f_key.getAttributes().size();
    boolean _lessEqualsThan = (_size <= 1);
    if (_lessEqualsThan) {
      return;
    }
    final int numProperties1 = d.getProperties().size();
    this.mapping.key2Attribute(f_key, d);
    final Function1<Property, Boolean> _function = (Property p) -> {
      String _name = p.getName();
      String _name_1 = d.getName();
      String _plus = (_name_1 + "_id");
      return Boolean.valueOf(Objects.equal(_name, _plus));
    };
    Property _findFirst = IterableExtensions.<Property>findFirst(d.getProperties(), _function);
    final documentschema.Attribute p_at = ((documentschema.Attribute) _findFirst);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_at);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Type _type = p_at.getType();
    Assertions.assertEquals(documentschema.DataType.STRING, ((documentschema.PrimitiveType) _type).getDatatype());
    Assertions.assertTrue(p_at.isIsKey());
  }

  private void reference2Reference_asserts(final Reference f_ref, final documentschema.EntityType d) {
    RelationshipType _isFeaturedBy = f_ref.getIsFeaturedBy();
    boolean _tripleNotEquals = (_isFeaturedBy != null);
    if (_tripleNotEquals) {
      return;
    }
    final int numProperties1 = d.getProperties().size();
    this.mapping.reference2Reference(f_ref, d);
    final Function1<Property, Boolean> _function = (Property p) -> {
      String _name = p.getName();
      String _name_1 = f_ref.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    Property _findFirst = IterableExtensions.<Property>findFirst(d.getProperties(), _function);
    final documentschema.Reference p_ref = ((documentschema.Reference) _findFirst);
    final Function1<documentschema.EntityType, Boolean> _function_1 = (documentschema.EntityType e) -> {
      String _name = e.getName();
      String _name_1 = f_ref.getRefsTo().getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final documentschema.EntityType d_target = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_1);
    final int numProperties2 = d.getProperties().size();
    Assertions.assertNotNull(p_ref);
    Assertions.assertEquals((numProperties1 + 1), numProperties2);
    Assertions.assertEquals(d_target, p_ref.getTarget());
    Type t = null;
    int _upperBound = f_ref.getUpperBound();
    boolean _equals = (_upperBound == 1);
    if (_equals) {
      t = this.mapping.findAttributeKey(d_target).getType();
    } else {
      if (((f_ref.getUpperBound() == (-1)) || (f_ref.getUpperBound() > 1))) {
        Type _type = this.mapping.findAttributeKey(d_target).getType();
        final documentschema.PrimitiveType pt = ((documentschema.PrimitiveType) _type);
        t = this.mapping.getDocArrayTypes().get(pt.getDatatype());
      }
    }
    Assertions.assertEquals(t, p_ref.getType());
  }

  private void relationshipType2EntityType_asserts(final RelationshipType rt) {
    this.mapping.relationshipType2EntityType(rt);
    final Function1<documentschema.EntityType, Boolean> _function = (documentschema.EntityType et) -> {
      String _name = et.getName();
      return Boolean.valueOf(Objects.equal(_name, "Libros_Autores"));
    };
    final documentschema.EntityType c = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function);
    Assertions.assertNotNull(c);
    Assertions.assertEquals(rt.getName(), c.getName());
    EList<Feature> _features = rt.getFeatures();
    for (final Feature f : _features) {
      {
        final Attribute rt_at = ((Attribute) f);
        final Function1<Property, Boolean> _function_1 = (Property p) -> {
          String _name = p.getName();
          String _name_1 = rt_at.getName();
          return Boolean.valueOf(Objects.equal(_name, _name_1));
        };
        Property _findFirst = IterableExtensions.<Property>findFirst(c.getProperties(), _function_1);
        final documentschema.Attribute c_at = ((documentschema.Attribute) _findFirst);
        Assertions.assertNotNull(c_at);
        Assertions.assertEquals(this.mapping.datatype2Type(rt_at.getType()), c_at.getType());
      }
    }
    final Function1<Property, Boolean> _function_1 = (Property p) -> {
      String _name = p.getName();
      String _name_1 = c.getName();
      String _plus = (_name_1 + "_id");
      return Boolean.valueOf(Objects.equal(_name, _plus));
    };
    Property _findFirst = IterableExtensions.<Property>findFirst(c.getProperties(), _function_1);
    final documentschema.Attribute at = ((documentschema.Attribute) _findFirst);
    Assertions.assertNotNull(at);
    Assertions.assertTrue(at.isIsKey());
    Type _type = at.getType();
    Assertions.assertEquals(documentschema.DataType.STRING, ((documentschema.PrimitiveType) _type).getDatatype());
    final Reference u_r1 = rt.getReference().get(0);
    final Reference u_r2 = rt.getReference().get(1);
    final Function1<documentschema.EntityType, Boolean> _function_2 = (documentschema.EntityType e) -> {
      String _name = e.getName();
      String _name_1 = u_r1.getOwner().getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final documentschema.EntityType p_d1 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_2);
    final Function1<documentschema.EntityType, Boolean> _function_3 = (documentschema.EntityType e) -> {
      String _name = e.getName();
      String _name_1 = u_r2.getOwner().getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    final documentschema.EntityType p_d2 = IterableExtensions.<documentschema.EntityType>findFirst(this.mapping.getDocumentSchema().getEntities(), _function_3);
    final Function1<Property, Boolean> _function_4 = (Property p) -> {
      String _name = p.getName();
      String _name_1 = u_r1.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    Property _findFirst_1 = IterableExtensions.<Property>findFirst(c.getProperties(), _function_4);
    final documentschema.Reference p_ref1 = ((documentschema.Reference) _findFirst_1);
    final Function1<Property, Boolean> _function_5 = (Property p) -> {
      String _name = p.getName();
      String _name_1 = u_r2.getName();
      return Boolean.valueOf(Objects.equal(_name, _name_1));
    };
    Property _findFirst_2 = IterableExtensions.<Property>findFirst(c.getProperties(), _function_5);
    final documentschema.Reference p_ref2 = ((documentschema.Reference) _findFirst_2);
    Assertions.assertNotNull(p_ref1);
    Assertions.assertNotNull(p_ref2);
    Assertions.assertEquals(p_d1, p_ref2.getTarget());
    Assertions.assertEquals(p_d2, p_ref1.getTarget());
    Assertions.assertEquals(this.mapping.findAttributeKey(p_ref1.getTarget()).getType(), p_ref1.getType());
    Assertions.assertEquals(this.mapping.findAttributeKey(p_ref2.getTarget()).getType(), p_ref2.getType());
    int _size = rt.getFeatures().size();
    int _plus = (_size + 3);
    Assertions.assertEquals(_plus, c.getProperties().size());
  }
}
