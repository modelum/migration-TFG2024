package es.um.dsdm.document2uschema.test;

import com.google.common.base.Objects;
import documentschema.Aggregate;
import documentschema.Attribute;
import documentschema.DocumentSchema;
import documentschema.EntityType;
import documentschema.Property;
import documentschema.Reference;
import es.um.dsdm.document2uschema.MappingDocument2Uschema;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uschema.Feature;
import uschema.Key;
import uschema.USchema;

@SuppressWarnings("all")
public class MappingDocument2UschemaTest {
  private static final String DOCUMENTSCHEMA_0 = "inputs/DocumentSchema-Regla0.xmi";

  private static final String DOCUMENTSCHEMA_1 = "inputs/DocumentSchema-Regla1.xmi";

  private static final String DOCUMENTSCHEMA_2 = "inputs/DocumentSchema-Regla2.xmi";

  private static final String DOCUMENTSCHEMA_3 = "inputs/DocumentSchema-Regla3.xmi";

  private static final String DOCUMENTSCHEMA_4 = "inputs/DocumentSchema-Regla4.xmi";

  private static final String DOCUMENTSCHEMA = "inputs/DocumentSchema.xmi";

  private MappingDocument2Uschema mapper;

  @Before
  public void setUp() {
    MappingDocument2Uschema _mappingDocument2Uschema = new MappingDocument2Uschema();
    this.mapper = _mappingDocument2Uschema;
  }

  @Test
  public void testDocumentSchema2USchema() {
    this.mapper.loadSchema(MappingDocument2UschemaTest.DOCUMENTSCHEMA_0);
    this.mapper.documentSchema2USchema();
    Assert.assertNotNull(this.mapper.getUSchema());
    Assert.assertEquals(this.mapper.getUSchema().getName(), this.mapper.getDocumentSchema().getName());
  }

  @Test
  public void testEntityType2entityType() {
    this.mapper.loadSchema(MappingDocument2UschemaTest.DOCUMENTSCHEMA_1);
    this.mapper.documentSchema2USchema();
    final EntityType entidad1 = this.mapper.getDocumentSchema().getEntities().get(0);
    final EntityType entidad2 = this.mapper.getDocumentSchema().getEntities().get(1);
    final uschema.EntityType entidad1Mapeada = this.mapper.entityType2entityType(entidad1);
    Assert.assertEquals(entidad1.getName(), entidad1Mapeada.getName());
    final uschema.EntityType entidad2Mapeada = this.mapper.entityType2entityType(entidad2);
    Assert.assertEquals(entidad2.getName(), entidad2Mapeada.getName());
    Assert.assertEquals(2, this.mapper.getUSchema().getEntities().size());
  }

  @Test
  public void testAttribute2Attribute() {
    this.mapper.loadSchema(MappingDocument2UschemaTest.DOCUMENTSCHEMA_2);
    this.mapper.documentSchema2USchema();
    final EntityType entidad1 = this.mapper.getDocumentSchema().getEntities().get(0);
    final EntityType entidad2 = this.mapper.getDocumentSchema().getEntities().get(1);
    final uschema.EntityType entidad1Mapeada = this.mapper.entityType2entityType(entidad1);
    final uschema.EntityType entidad2Mapeada = this.mapper.entityType2entityType(entidad2);
    final Consumer<Property> _function = (Property property) -> {
      if ((property instanceof Attribute)) {
        this.mapper.attribute2attribute(((Attribute) property));
      }
    };
    entidad1.getProperties().forEach(_function);
    final Consumer<Property> _function_1 = (Property property) -> {
      if ((property instanceof Attribute)) {
        this.mapper.attribute2attribute(((Attribute) property));
      }
    };
    entidad2.getProperties().forEach(_function_1);
    Feature _get = this.mapper.getUSchema().getEntities().get(0).getFeatures().get(0);
    final uschema.Attribute atributo1uschema = ((uschema.Attribute) _get);
    Property _get_1 = this.mapper.getDocumentSchema().getEntities().get(0).getProperties().get(0);
    final Attribute atributo1documentschema = ((Attribute) _get_1);
    Assert.assertEquals(atributo1uschema.getName(), atributo1documentschema.getName());
    Assert.assertEquals(this.mapper.getUSchema().getEntities().get(0).getFeatures().size(), 4);
    Assert.assertEquals(this.mapper.getUSchema().getEntities().get(1).getFeatures().size(), 3);
    Feature _get_2 = this.mapper.getUSchema().getEntities().get(0).getFeatures().get(2);
    final Key key = ((Key) _get_2);
    Assert.assertTrue(key.isIsID());
    Assert.assertEquals(key.getName(), this.mapper.getDocumentSchema().getEntities().get(0).getProperties().get(2).getName());
  }

  @Test
  public void testReference2Reference() {
    this.mapper.loadSchema(MappingDocument2UschemaTest.DOCUMENTSCHEMA_3);
    this.mapper.documentSchema2USchema();
    final EntityType entidad1 = this.mapper.getDocumentSchema().getEntities().get(0);
    final uschema.EntityType entidad1Mapeada = this.mapper.entityType2entityType(entidad1);
    final EntityType entidad2 = this.mapper.getDocumentSchema().getEntities().get(1);
    final uschema.EntityType entidad2Mapeada = this.mapper.entityType2entityType(entidad2);
    Property _get = entidad1.getProperties().get(0);
    final Reference referencia1 = ((Reference) _get);
    this.mapper.reference2reference(referencia1);
    Property _get_1 = entidad1.getProperties().get(1);
    final Reference referencia2 = ((Reference) _get_1);
    this.mapper.reference2reference(referencia2);
    Property _head = IterableExtensions.<Property>head(entidad2.getProperties());
    final Reference referencia3 = ((Reference) _head);
    this.mapper.reference2reference(referencia3);
    Feature _get_2 = entidad1Mapeada.getFeatures().get(0);
    final uschema.Reference referencia1Mapeada = ((uschema.Reference) _get_2);
    Feature _get_3 = entidad1Mapeada.getFeatures().get(1);
    final uschema.Attribute atributo1Mapeado = ((uschema.Attribute) _get_3);
    Feature _get_4 = entidad1Mapeada.getFeatures().get(2);
    final uschema.Reference referencia2Mapeada = ((uschema.Reference) _get_4);
    Feature _get_5 = entidad1Mapeada.getFeatures().get(3);
    final uschema.Attribute atributo2Mapeado = ((uschema.Attribute) _get_5);
    Feature _get_6 = entidad2Mapeada.getFeatures().get(0);
    final uschema.Reference referencia3Mapeada = ((uschema.Reference) _get_6);
    Feature _get_7 = entidad2Mapeada.getFeatures().get(1);
    final uschema.Attribute atributo3Mapeado = ((uschema.Attribute) _get_7);
    Assert.assertEquals(referencia1.getName(), referencia1Mapeada.getName());
    Assert.assertEquals(referencia1.getName(), atributo1Mapeado.getName());
    Assert.assertEquals(referencia2.getName(), referencia2Mapeada.getName());
    Assert.assertEquals(referencia2.getName(), atributo2Mapeado.getName());
    Assert.assertEquals(referencia3.getName(), referencia3Mapeada.getName());
    Assert.assertEquals(referencia3.getName(), atributo3Mapeado.getName());
    Assert.assertEquals(1, referencia1Mapeada.getLowerBound());
    Assert.assertEquals(1, referencia2Mapeada.getLowerBound());
    Assert.assertEquals(1, referencia3Mapeada.getLowerBound());
    Assert.assertEquals(1, referencia1Mapeada.getUpperBound());
    Assert.assertEquals((-1), referencia2Mapeada.getUpperBound());
    Assert.assertEquals(1, referencia3Mapeada.getUpperBound());
    Assert.assertEquals(referencia1Mapeada.getRefsTo(), this.mapper.entityType2entityType(referencia1.getTarget()));
    Assert.assertEquals(referencia2Mapeada.getRefsTo(), this.mapper.entityType2entityType(referencia2.getTarget()));
    Assert.assertEquals(referencia3Mapeada.getRefsTo(), this.mapper.entityType2entityType(referencia3.getTarget()));
  }

  @Test
  public void testAggregate2Aggregate() {
    this.mapper.loadSchema(MappingDocument2UschemaTest.DOCUMENTSCHEMA_4);
    this.mapper.documentSchema2USchema();
    final Consumer<EntityType> _function = (EntityType entidad) -> {
      this.mapper.entityType2entityType(entidad);
    };
    this.mapper.getDocumentSchema().getEntities().forEach(_function);
    final Consumer<EntityType> _function_1 = (EntityType entidad) -> {
      final Consumer<Property> _function_2 = (Property atributo) -> {
        if ((atributo instanceof Attribute)) {
          this.mapper.attribute2attribute(((Attribute) atributo));
        }
      };
      entidad.getProperties().forEach(_function_2);
    };
    this.mapper.getDocumentSchema().getEntities().forEach(_function_1);
    final Consumer<EntityType> _function_2 = (EntityType entidad) -> {
      final Consumer<Property> _function_3 = (Property reference) -> {
        if ((reference instanceof Reference)) {
          this.mapper.reference2reference(((Reference) reference));
        }
      };
      entidad.getProperties().forEach(_function_3);
    };
    this.mapper.getDocumentSchema().getEntities().forEach(_function_2);
    Assert.assertEquals(this.mapper.getUSchema().getEntities().size(), 2);
    final Consumer<EntityType> _function_3 = (EntityType entidad) -> {
      final Consumer<Property> _function_4 = (Property agregado) -> {
        if ((agregado instanceof Aggregate)) {
          this.mapper.aggregate2entityType(((Aggregate) agregado));
        }
      };
      entidad.getProperties().forEach(_function_4);
    };
    this.mapper.getDocumentSchema().getEntities().forEach(_function_3);
    Assert.assertEquals(this.mapper.getUSchema().getEntities().size(), 3);
    Property _get = this.mapper.getDocumentSchema().getEntities().get(0).getProperties().get(0);
    final Aggregate agregadoDocument = ((Aggregate) _get);
    Assert.assertEquals(this.mapper.getUSchema().getEntities().get(2).getName(), agregadoDocument.getName());
    Assert.assertEquals(this.mapper.getUSchema().getEntities().get(2).getFeatures().size(), 1);
    Assert.assertFalse(this.mapper.getUSchema().getEntities().get(2).isRoot());
  }

  @Test
  public void testTransformation() {
    this.mapper.loadSchema(MappingDocument2UschemaTest.DOCUMENTSCHEMA);
    this.mapper.transformacion();
    final DocumentSchema documentschema = this.mapper.getDocumentSchema();
    final USchema uschema = this.mapper.getUSchema();
    Assert.assertNotNull(uschema);
    int _size = uschema.getEntities().size();
    int _size_1 = documentschema.getEntities().size();
    int _plus = (_size_1 + 1);
    Assert.assertEquals(_size, _plus);
    final Function1<uschema.EntityType, Boolean> _function = (uschema.EntityType e) -> {
      String _name = e.getName();
      return Boolean.valueOf(Objects.equal(_name, "User"));
    };
    final uschema.EntityType userEntity = IterableExtensions.<uschema.EntityType>findFirst(uschema.getEntities(), _function);
    Assert.assertNotNull(userEntity);
    final Function1<uschema.EntityType, Boolean> _function_1 = (uschema.EntityType e) -> {
      String _name = e.getName();
      return Boolean.valueOf(Objects.equal(_name, "Order"));
    };
    final uschema.EntityType orderEntity = IterableExtensions.<uschema.EntityType>findFirst(uschema.getEntities(), _function_1);
    Assert.assertNotNull(orderEntity);
    final Function1<uschema.EntityType, Boolean> _function_2 = (uschema.EntityType e) -> {
      String _name = e.getName();
      return Boolean.valueOf(Objects.equal(_name, "Product"));
    };
    final uschema.EntityType productEntity = IterableExtensions.<uschema.EntityType>findFirst(uschema.getEntities(), _function_2);
    Assert.assertNotNull(productEntity);
    final Function1<uschema.EntityType, Boolean> _function_3 = (uschema.EntityType e) -> {
      String _name = e.getName();
      return Boolean.valueOf(Objects.equal(_name, "OrderDetails"));
    };
    final uschema.EntityType orderDetailsEntity = IterableExtensions.<uschema.EntityType>findFirst(uschema.getEntities(), _function_3);
    Assert.assertNotNull(orderDetailsEntity);
    final Function1<uschema.EntityType, Boolean> _function_4 = (uschema.EntityType e) -> {
      String _name = e.getName();
      return Boolean.valueOf(Objects.equal(_name, "Shop"));
    };
    final uschema.EntityType shopEntity = IterableExtensions.<uschema.EntityType>findFirst(uschema.getEntities(), _function_4);
    Assert.assertNotNull(shopEntity);
    final Function1<uschema.EntityType, Boolean> _function_5 = (uschema.EntityType e) -> {
      String _name = e.getName();
      return Boolean.valueOf(Objects.equal(_name, "producto"));
    };
    final uschema.EntityType aggregateEntity = IterableExtensions.<uschema.EntityType>findFirst(uschema.getEntities(), _function_5);
    Assert.assertNotNull(aggregateEntity);
  }
}
