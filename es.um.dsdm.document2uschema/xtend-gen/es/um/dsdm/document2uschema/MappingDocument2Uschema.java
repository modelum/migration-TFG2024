package es.um.dsdm.document2uschema;

import USchema.Aggregate;
import USchema.Attribute;
import USchema.DataType;
import USchema.Feature;
import USchema.Key;
import USchema.PList;
import USchema.Reference;
import USchema.USchemaClass;
import USchema.UschemaMMFactory;
import documentschema.Array;
import documentschema.DocumentSchema;
import documentschema.EntityType;
import documentschema.PrimitiveType;
import documentschema.Property;
import documentschema.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;

@SuppressWarnings("all")
public class MappingDocument2Uschema {
  private final UschemaMMFactory uschemaFactory;

  private final Map<EntityType, USchema.EntityType> mappedEntityTypes;

  private final Map<DocumentSchema, USchemaClass> mappedSchemas;

  public MappingDocument2Uschema() {
    this.uschemaFactory = UschemaMMFactory.eINSTANCE;
    this.mappedEntityTypes = CollectionLiterals.<EntityType, USchema.EntityType>newHashMap();
    this.mappedSchemas = CollectionLiterals.<DocumentSchema, USchemaClass>newHashMap();
  }

  public USchemaClass document2uschema(final DocumentSchema documentSchema) {
    boolean _containsKey = this.mappedSchemas.containsKey(documentSchema);
    if (_containsKey) {
      return this.mappedSchemas.get(documentSchema);
    }
    final USchemaClass USchema = this.uschemaFactory.createUSchemaClass();
    this.mappedSchemas.put(documentSchema, USchema);
    USchema.setName(documentSchema.getName());
    final Consumer<EntityType> _function = (EntityType entity) -> {
      USchema.getEntities().add(this.entityType2entityType(entity));
    };
    documentSchema.getEntities().forEach(_function);
    return USchema;
  }

  private USchema.EntityType entityType2entityType(final EntityType documentEntity) {
    boolean _containsKey = this.mappedEntityTypes.containsKey(documentEntity);
    if (_containsKey) {
      return this.mappedEntityTypes.get(documentEntity);
    }
    final USchema.EntityType uschemaEntity = this.uschemaFactory.createEntityType();
    this.mappedEntityTypes.put(documentEntity, uschemaEntity);
    uschemaEntity.setName(documentEntity.getName());
    uschemaEntity.setRoot(true);
    final Consumer<Property> _function = (Property property) -> {
      uschemaEntity.getFeatures().add(this.property2feature(property));
    };
    documentEntity.getProperties().forEach(_function);
    return uschemaEntity;
  }

  private Attribute _property2feature(final documentschema.Attribute a) {
    final Attribute ea = this.uschemaFactory.createAttribute();
    ea.setName(a.getName());
    ea.setType(this.type2datatype(a.getType()));
    boolean _isIsKey = a.isIsKey();
    boolean _equals = (_isIsKey == true);
    if (_equals) {
      final Key k = this.uschemaFactory.createKey();
      k.setName(a.getName());
      k.setIsID(true);
      this.mappedEntityTypes.get(a.eContainer()).getFeatures().add(k);
      ea.setKey(k);
      k.getAttributes().add(ea);
    }
    return ea;
  }

  private Reference _property2feature(final documentschema.Reference r) {
    final Reference er = this.uschemaFactory.createReference();
    er.setName(r.getName());
    final Attribute ea = this.uschemaFactory.createAttribute();
    ea.setName(r.getName());
    er.setLowerBound(1);
    int _xifexpression = (int) 0;
    Type _type = r.getType();
    if ((_type instanceof PrimitiveType)) {
      _xifexpression = 1;
    } else {
      _xifexpression = (-1);
    }
    er.setUpperBound(_xifexpression);
    er.getAttributes().add(ea);
    ea.getReferences().add(er);
    this.mappedEntityTypes.get(r.eContainer()).getFeatures().add(ea);
    er.setRefsTo(this.entityType2entityType(r.getTarget()));
    return er;
  }

  private Aggregate _property2feature(final documentschema.Aggregate g) {
    final USchema.EntityType eg = this.uschemaFactory.createEntityType();
    eg.setName(g.getName());
    Property _aggregates = g.getAggregates();
    boolean _tripleNotEquals = (_aggregates != null);
    if (_tripleNotEquals) {
      eg.getFeatures().add(this.property2feature(g.getAggregates()));
    }
    eg.setRoot(false);
    final USchemaClass uschemaClass = ((USchemaClass[])Conversions.unwrapArray(this.mappedSchemas.values(), USchemaClass.class))[0];
    uschemaClass.getEntities().add(eg);
    final Aggregate aggregate = this.uschemaFactory.createAggregate();
    aggregate.setName(g.getName());
    aggregate.setLowerBound(1);
    int _xifexpression = (int) 0;
    boolean _isIsMany = g.isIsMany();
    if (_isIsMany) {
      _xifexpression = (-1);
    } else {
      _xifexpression = 1;
    }
    aggregate.setUpperBound(_xifexpression);
    aggregate.setSpecifiedBy(eg);
    USchema.EntityType _get = this.mappedEntityTypes.get(g.eContainer());
    _get.setRoot(true);
    return aggregate;
  }

  private DataType type2datatype(final Type type) {
    if ((type instanceof PrimitiveType)) {
      final USchema.PrimitiveType et = this.uschemaFactory.createPrimitiveType();
      et.setName(this.mapPrimitiveTypeName(((PrimitiveType)type).getDatatype()));
      return et;
    } else {
      if ((type instanceof Array)) {
        final PList plist = this.uschemaFactory.createPList();
        plist.setElementType(this.type2datatype(((Array)type).getType()));
        return plist;
      }
    }
    return null;
  }

  private String mapPrimitiveTypeName(final documentschema.DataType datatype) {
    if (datatype != null) {
      switch (datatype) {
        case BOOLEAN:
          return "BOOLEAN";
        case INTEGER:
          return "INTEGER";
        case DOUBLE:
          return "DOUBLE";
        case STRING:
          return "STRING";
        default:
          break;
      }
    }
    return null;
  }

  private Feature property2feature(final Property g) {
    if (g instanceof documentschema.Aggregate) {
      return _property2feature((documentschema.Aggregate)g);
    } else if (g instanceof documentschema.Attribute) {
      return _property2feature((documentschema.Attribute)g);
    } else if (g instanceof documentschema.Reference) {
      return _property2feature((documentschema.Reference)g);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(g).toString());
    }
  }
}
