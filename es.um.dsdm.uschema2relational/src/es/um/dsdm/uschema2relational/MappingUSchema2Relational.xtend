package es.um.dsdm.uschema2relational

import relationalschema.RelationalMMFactory
import USchema.USchemaClass
import relationalschema.RelationalSchema
import relationalschema.Table
import USchema.EntityType
import relationalschema.Column
import USchema.Attribute
import USchema.DataType
import USchema.Key
import USchema.Reference
import relationalschema.FKey
import relationalschema.ReferentialAction
import USchema.Aggregate
import USchema.RelationshipType
import USchema.PrimitiveType
import java.util.Map

class MappingUSchema2Relational {
	
	val RelationalMMFactory relationalFactory;
	val Map<EntityType, Table> mappedTables
	
	new() {
		relationalFactory = RelationalMMFactory.eINSTANCE
		mappedTables = newHashMap()
	}
	
	// R0
	def RelationalSchema uschema2relational(USchemaClass us) {
		val rs = relationalFactory.createRelationalSchema
		rs.name = us.name
		// transformamos las entidades
		us.entities.forEach[entity | 
			rs.tables.add(entityType2table(entity))
		]
		// transformamos las relaciones
		us.relationships.forEach[relationship |
			rs.tables.add(relationshipType2table(relationship))
		]
		return rs
	}
	
	// R1
	private def Table entityType2table(EntityType e) {
		if (mappedTables.containsKey(e)) 
	    	return mappedTables.get(e)
		val t = relationalFactory.createTable
		mappedTables.put(e, t)
		t.name = e.name
		e.features.forEach[feature |
			if (feature instanceof Attribute) {
				t.columns.add(attribute2column(feature, t))
			}
			if (feature instanceof Key) {
				t.keys.add(key2key(feature, t))
			}
			if (feature instanceof Reference) {
				t.fks.add(reference2fkey(feature, t))
			}
			if (feature instanceof Aggregate) {
				aggregate2weaktable(feature, t)	
			}]
		return t
	}
	
	// R2
	private def Column attribute2column(Attribute at, Table t) {
		// esta comprobacion se debe a que cuando usamos otra regla
		// que requiere el uso de esta regla se crean las columnas duplicadas	
		val columnaExistente = t.columns.findFirst[col |
			col.name == at.name
		]
		if (columnaExistente !== null) {
			return columnaExistente
		}
		else {
			val col = relationalFactory.createColumn
		col.name = at.name
		col.datatype = mapType2String(at.type)
		switch (col.datatype) {
			case "VARCHAR": {
				col.size = 255
			}
			case "NUMERIC": {
				col.size = 38
			}
			case "BOOLEAN": {
				col.size = 1
			}
			case "DATE": {
				col.size = 10
			}
			default: {
				
			}
		}
		col.nullable = true
		col.defaultvalue = null
		t.columns.add(col)
		return col
		}
	}
	
	// R3
	private def relationalschema.Key key2key(Key k, Table t) {
		val pk = relationalFactory.createKey
		if (k.isID == true) {
			pk.constraintname = t.name + "_pk"
		}
		else {
			pk.constraintname = k.name + "_ak"
		}
		pk.owner = t
		pk.isPK = k.isID
		k.attributes.forEach[attribute |
			pk.columns.add(attribute2column(attribute, t))
		]
		return pk
	}
	
	// R4
	private def FKey reference2fkey(Reference ref, Table t) {
		val fk = relationalFactory.createFKey
		val tr = entityType2table(ref.refsTo)
		fk.constraintname = tr.name + "_fk"
		fk.owner = t
		tr.keys.forEach[key |
			if (key.isPK == true) {
				fk.refsTo = key	
			}
		]
		if (ref.attributes !== null) {
			ref.attributes.forEach[attribute |
				fk.columns.add(attribute2column(attribute, t))
			]
		}
		else {
			fk.refsTo.columns.forEach[column |
				fk.columns.add(column)
			]
		}
		fk.onDelete = ReferentialAction.NO_ACTION
		fk.onUpdate = ReferentialAction.CASCADE
		return fk
	}
	
	// R5
	private def Table aggregate2weaktable(Aggregate ag, Table ts) {
		val schema = ag.specifiedBy
		// hacemos un casteo para poder usar la regla 1
		val entidad = schema as EntityType
		// obtenemos la tabla asociada al aggregate
		val tg = entityType2table(entidad)
		// creamos la clave
		val fk = relationalFactory.createFKey
		fk.constraintname = ts.name + "_fk"
		ts.keys.forEach[key |
			if (key.isPK) {
				fk.refsTo = key
				key.columns.forEach[column |
					tg.columns.add(column)
					fk.columns.add(column)
				]
			}
		]
		fk.onDelete = ReferentialAction.NO_ACTION
		fk.onUpdate = ReferentialAction.CASCADE
		// añadimos a la tabla la fk
		tg.fks.add(fk)
		// creamos la key y la column
		val k = relationalFactory.createKey
		val col = relationalFactory.createColumn
		k.constraintname = tg.name + "_pk"
		k.isPK = true
		col.name = "id"
		col.datatype = "Numeric"
		col.size = 38
		col.nullable = false
		col.defaultvalue = "1"
		fk.columns.forEach[column |
			k.columns.add(column)
		]
		k.columns.add(col)
		tg.keys.add(k)
		return tg
	}
	
	// R6
	private def Table relationshipType2table(RelationshipType rt) {
		val t = relationalFactory.createTable
		t.name = rt.name
		rt.features.forEach[feature |
			if (feature instanceof Attribute) {
				t.columns.add(attribute2column(feature, t))
			}
			if (feature instanceof Reference) {
				if (feature.isFeaturedBy == rt) {
					val fk1 = relationalFactory.createFKey
					// mapeamos la tabla desde la entidad a la que se referencia
					val td = entityType2table(feature.refsTo)
					fk1.constraintname = td.name + "_fk"
					// buscamos la pk dentro de la tabla y la asignamos
					td.keys.forEach[key |
						if (key.isPK){
							fk1.refsTo = key
							// le añadimos las columnas de la pk a fk1
							key.columns.forEach[column |
								fk1.columns.add(column)
							]				
						}
					]
					// lo añadimos a t
					t.fks.add(fk1)
					// Creamos ahora fk2
					val fk2 = relationalFactory.createFKey
					//val to = entityType2table(feature.owner)
					val to = relationalFactory.createTable
					fk2.constraintname = to.name + "_fk"
					to.keys.forEach[key |
						if (key.isPK) {
							fk2.refsTo = key
							key.columns.forEach[column |
								fk2.columns.add(column)
							]
						}
					] 
					t.fks.add(fk2)
					val pk = relationalFactory.createKey
					pk.constraintname = t.name + "_pk"
					pk.isPK = true
					fk1.columns.forEach[column |
						pk.columns.add(column)
					]
					fk2.columns.forEach[column |
						pk.columns.add(column)
					]
					t.keys.add(pk)
				}
			}
		]
		return t
	}
	
	// R7
	private def String mapType2String(DataType datatype) {
		val primitiveType = datatype as PrimitiveType
		if (primitiveType !== null) {
			switch (primitiveType.name) {
				case "String": {
					return "VARCHAR"
				}
				case "int": {
					return "NUMERIC"
				}
				case "boolean": {
					return "BOOLEAN"
				}
				case "double": {
					return "NUMERIC"
				}
				case "Date": {
					return "DATE"
				}
				default: {
					
				}
			}
		}
	}
	
}