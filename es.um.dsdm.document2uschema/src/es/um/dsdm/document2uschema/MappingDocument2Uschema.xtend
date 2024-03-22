package es.um.dsdm.document2uschema

import USchema.UschemaMMFactory
import java.util.Map
import documentschema.DocumentSchema
import USchema.USchemaClass
import documentschema.Array
import USchema.SchemaType

class MappingDocument2Uschema {
	
	val UschemaMMFactory uschemaFactory;
	val Map<documentschema.EntityType, USchema.EntityType> mappedEntityTypes
	
	new() {
		uschemaFactory = UschemaMMFactory.eINSTANCE
		mappedEntityTypes = newHashMap()
	}
	
	
	
	// R0
	def USchemaClass document2uschema(DocumentSchema documentSchema) {
		val USchema = uschemaFactory.createUSchemaClass
		USchema.name = documentSchema.name
		documentSchema.entities.forEach[entity |
			USchema.entities.add(entityType2entityType(entity))
		]
		
		return USchema
	}
	// R1
	def private USchema.EntityType entityType2entityType(documentschema.EntityType documentEntity) {
		if (mappedEntityTypes.containsKey(documentEntity)) 
	    	return mappedEntityTypes.get(documentEntity)
		val uschemaEntity = uschemaFactory.createEntityType
		mappedEntityTypes.put(documentEntity, uschemaEntity)
		uschemaEntity.name = documentEntity.name
		documentEntity.properties.forEach[property |
			if (property instanceof documentschema.Attribute) {
				uschemaEntity.features.add(property2feature(property))
			}
			else if (property instanceof documentschema.Reference) {
				uschemaEntity.features.add(property2feature(property))
			}
			else if (property instanceof documentschema.Aggregate) {
				uschemaEntity.features.add(property2feature(property))
			}
		]
		return uschemaEntity
	}
	
	// R2
	def private USchema.Attribute property2feature(documentschema.Attribute a) {
		val ea = uschemaFactory.createAttribute
		ea.name = a.name
		ea.type = type2datatype(a.type)
		if (a.isKey == true) {
			val k = uschemaFactory.createKey
			k.name = a.name
			k.isID = true
			mappedEntityTypes.get(a.eContainer).features.add(k)
			ea.key = k
			k.attributes.add(ea)
		}
		return ea
	}	
	
	// R3
	def private USchema.Reference property2feature(documentschema.Reference r) {
		val er = uschemaFactory.createReference
		er.name = r.name
		val ea = uschemaFactory.createAttribute
		ea.name = r.name
		er.lowerBound = 1
		er.upperBound = r.type instanceof documentschema.PrimitiveType ? 1 : -1
		er.attributes.add(ea)
		ea.references.add(er)
		mappedEntityTypes.get(r.eContainer).features.add(ea)		
		er.refsTo = entityType2entityType(r.target)
		return er
	}
	
	// R4
	def private USchema.Aggregate property2feature(documentschema.Aggregate g) {
		val eg = uschemaFactory.createEntityType
		eg.name = g.name
		// falta eg.features = g.aggregates 
		eg.root = false
		val aggregate = uschemaFactory.createAggregate
		aggregate.name = g.name
		aggregate.lowerBound = 1
		aggregate.upperBound = g.isMany ? -1 : 1
		aggregate.aggregates = eg
		mappedEntityTypes.get(g.eContainer).root = true
		return aggregate
		
	}
	// R5
	def private USchema.DataType type2datatype(documentschema.Type type) {
		if (type instanceof documentschema.PrimitiveType) {
			val et = uschemaFactory.createPrimitiveType
			et.name = mapPrimitiveTypeName(type.datatype) 
			return et
			
		}
		else if (type instanceof documentschema.Array) {
			val plist = uschemaFactory.createPList
			plist.elementType = type2datatype(type.type)
			return plist
		}
	}
	
	def private String mapPrimitiveTypeName(documentschema.DataType datatype) {
		switch(datatype) {
			case documentschema.DataType::BOOLEAN: return "BOOLEAN"
			case documentschema.DataType::INTEGER: return "INTEGER"
			case documentschema.DataType::DOUBLE: return "DOUBLE"
			case documentschema.DataType::STRING: return "STRING"	
		}
	}
	
}