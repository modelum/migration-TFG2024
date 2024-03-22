package es.um.dsdm.document2uschema;

import USchema.DataType;
import USchema.Key;
import USchema.PList;
import USchema.USchemaClass;
import USchema.UschemaMMFactory;
import documentschema.Aggregate;
import documentschema.Array;
import documentschema.Attribute;
import documentschema.DocumentSchema;
import documentschema.EntityType;
import documentschema.PrimitiveType;
import documentschema.Property;
import documentschema.Reference;
import documentschema.Type;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class MappingDocument2Uschema {
  private final UschemaMMFactory uschemaFactory;

  private final Map<EntityType, USchema.EntityType> mappedEntityTypes;

  public MappingDocument2Uschema() {
    this.uschemaFactory = UschemaMMFactory.eINSTANCE;
    this.mappedEntityTypes = CollectionLiterals.<EntityType, USchema.EntityType>newHashMap();
  }

  public USchemaClass document2uschema(final DocumentSchema documentSchema) {
    final USchemaClass USchema = this.uschemaFactory.createUSchemaClass();
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
    final Consumer<Property> _function = (Property property) -> {
      if ((property instanceof Attribute)) {
        uschemaEntity.getFeatures().add(this.property2feature(((Attribute)property)));
      } else {
        if ((property instanceof Reference)) {
          uschemaEntity.getFeatures().add(this.property2feature(((Reference)property)));
        } else {
          if ((property instanceof Aggregate)) {
            uschemaEntity.getFeatures().add(this.property2feature(((Aggregate)property)));
          }
        }
      }
    };
    documentEntity.getProperties().forEach(_function);
    return uschemaEntity;
  }

  private USchema.Attribute property2feature(final Attribute a) {
    final USchema.Attribute ea = this.uschemaFactory.createAttribute();
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

  private USchema.Reference property2feature(final Reference r) {
    final USchema.Reference er = this.uschemaFactory.createReference();
    er.setName(r.getName());
    final USchema.Attribute ea = this.uschemaFactory.createAttribute();
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

  private USchema.Aggregate property2feature(final Aggregate g) {
    final USchema.EntityType eg = this.uschemaFactory.createEntityType();
    eg.setName(g.getName());
    eg.setRoot(false);
    final USchema.Aggregate aggregate = this.uschemaFactory.createAggregate();
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
    aggregate.setAggregates(eg);
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
}
