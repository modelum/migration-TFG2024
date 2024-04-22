package juanfran.um

import uschema.UschemaFactory
import relationalschema.RelationalSchema
import uschema.USchema
import java.util.Map
import relationalschema.Table
import uschema.SchemaType
import java.util.HashMap
import uschema.EntityType
import uschema.RelationshipType
import uschema.Aggregate
import uschema.Attribute
import relationalschema.Column
import uschema.PrimitiveType
import relationalschema.Key
import java.util.List
import java.util.LinkedList
import relationalschema.FKey
import uschema.Reference

class MappingRelational2Uschema {
	
	val UschemaFactory usFactory;
	val Map<Table, EntityType> entityTypes;
	val Map<Table, RelationshipType> relationshipTypes;
	val Map<Column, Attribute> attributes;
	val Map<Key, uschema.Key> keys;
	
	new() {
		this.usFactory = UschemaFactory.eINSTANCE
		this.entityTypes = new HashMap()
		this.relationshipTypes = new HashMap()
		this.attributes = new HashMap()
		this.keys = new HashMap()
	}
	
	// R0: RelationalSchema to USchema
	def USchema relationalSchema2USchema(RelationalSchema relationalSchema) {
		val USchema uSchema = usFactory.createUSchema
		
		uSchema.name = relationalSchema.name
		
		relationalSchema.tables.forEach[ t |
			val SchemaType st = table2SchemaType(t)
			switch st {
				case st instanceof EntityType: 
					uSchema.entities.add(st as EntityType)
				case st instanceof RelationshipType:
					uSchema.relationships.add(st as RelationshipType)
			}
		]
		
		relationshipTypes.forEach[ t, _ |
			//r5
			mNTable2RelationshipType(t)
		]
		
		entityTypes.forEach[ t, _ | 
			if (weakCondition(t))
				//r4
				weakTable2Aggregate(t)
			//r6
			r6(t)
		]

		return uSchema
	}
	
	// R1: Table to SchemaType
	def SchemaType table2SchemaType(Table t) {
		var SchemaType st;
		
		if (mNCondition(t)) {
			st = usFactory.createRelationshipType
			relationshipTypes.put(t, st as RelationshipType)
		} else {
			val EntityType et = usFactory.createEntityType
			et.root = true //TODO: si se pone por defecto a true en el metamodelo, esto no hará falta
			entityTypes.put(t, et)
			
			st = et
		}
		
		st.name = t.name
		
		//r2
		val List<Attribute> attributes = t.columns.map[ c | column2Attribute(c) ]
		st.features.addAll(attributes)
		
		//r3
		val List<uschema.Key> keys = t.keys.map[ k | pK2Key(k) ]
		st.features.addAll(keys)
		
		return st
	}
	
	// R2: Column to Attribute
	def Attribute column2Attribute(Column c) {
		val Attribute at = usFactory.createAttribute
		val PrimitiveType primitiveType = usFactory.createPrimitiveType
		primitiveType.name = typeConversionRelToUsc(c.datatype)
		
		at.name = c.name
		at.type = primitiveType
		at.optional = c.nullable
		
		attributes.put(c, at)
		
		return at
	}
	
	// R3: PK & UK to Key
	def uschema.Key pK2Key(Key rKey) {
		val uschema.Key uKey = usFactory.createKey

		uKey.name = rKey.constraintname
		uKey.isID = rKey.isIsPK
		uKey.attributes.addAll(columns2Attributes(rKey.columns))
		
		keys.put(rKey, uKey)
		
		return uKey
	}
	
	// R4: Weak Table to Aggregate
	def void weakTable2Aggregate(Table w) {
		println(w.toString)
		val Table s = getFKsInPK(w).head.refsTo.owner
		val EntityType es = entityTypes.get(s)
		val EntityType ew = entityTypes.get(w)
		val Aggregate ag = usFactory.createAggregate
	
		ag.name = w.name + "s"
		ag.lowerBound = 0
		ag.upperBound = -1 // Many (n)
		ag.specifiedBy = ew
		es.features.add(ag)
		ew.root = false
	}
	
	// R5: M:N Table to RelationshipType
	def RelationshipType mNTable2RelationshipType(Table m) {
		val RelationshipType rm = relationshipTypes.get(m)
		val List<FKey> fKs = getFKsInPK(m)
		val FKey fK1 = fKs.get(0)
		val FKey fK2 = fKs.get(1)
		val Table t1 = fK1.refsTo.owner
		val Table t2 = fK2.refsTo.owner
		val EntityType et1 = entityTypes.get(t1)
		val EntityType et2 = entityTypes.get(t2)
		val Reference ref = usFactory.createReference
		
		ref.name = t2.name
		ref.lowerBound = 1
		ref.upperBound = 1
		ref.refsTo = et2
		ref.owner = et1
		ref.isFeaturedBy = rm
		
		ref.attributes.addAll(columns2Attributes(fK2.columns)) 
		
		et1.root = true //TODO: si se pone por defecto a true en el metamodelo, esto no hará falta
		et2.root = true
		
		return rm
	}
	
	// R6
	def void r6(Table t) {
		// KeyPropagation condition
		val List<FKey> fKs = getFKsNotInPK(t)
		val List<Key> uKs = findUKs(t)
		
		fKs.forEach[ fk |
			if (isFKInUKs(fk, uKs))
				r6Table1_1(t, fk)
			else
				r6Table1_N(t, fk)
		]
	}
	
	// R6: 1:1 table
	def void r6Table1_1(Table t, FKey fk) {
		val Table s = fk.refsTo.owner
		val EntityType et = entityTypes.get(t)
		val EntityType es = entityTypes.get(s)
		val Reference rs = usFactory.createReference
		
		rs.name = s.name + "_" + fk.constraintname
		rs.lowerBound = 0
		rs.upperBound = 1
		rs.refsTo = es
		rs.attributes.addAll(columns2Attributes(fk.columns))
		et.features.add(rs)
	}
	
	// R6: 1:N table
	def r6Table1_N(Table t, FKey fk) {
		val Table s = fk.refsTo.owner
		val EntityType et = entityTypes.get(t)
		val EntityType es = entityTypes.get(s)
		val Reference rt = usFactory.createReference
		val Key pk = findPK(t);
		
		rt.name = t.name + 's'
		rt.lowerBound = 0
		rt.upperBound = -1 // Many (n)
		rt.refsTo = et
		es.features.add(rt)
		
		pk.columns.forEach[ col |
			val Attribute at = usFactory.createAttribute
			val PrimitiveType primitiveType = usFactory.createPrimitiveType
			primitiveType.name = typeConversionRelToUsc(col.datatype)
		
			at.name = col.name + et.name + 's'
			at.type = primitiveType
			rt.attributes.add(at)
			es.features.add(at)
		]
	}
	
	// Checks whether a table satisfies the weak condition
	def boolean weakCondition(Table t) {
		val List<FKey> fKs = getFKsInPK(t)

		if (fKs.size == 1)
			return true
		
		return false
	}
	
	// Checks whether a table satisfies the M:N condition
	def boolean mNCondition(Table t) {
		val List<FKey> fKs = getFKsInPK(t)

		if (fKs.size == 2)
			return true
		
		return false
	}
	
	// Returns a list of foreign keys that are included in the primary key of "t"
	def List<FKey> getFKsInPK(Table t) {
		val List<FKey> fKs = new LinkedList();
		
		val relationalschema.Key pk = findPK(t)
		if (pk === null)
			return fKs
	
		t.fks.forEach[ fk |
			if (pk.columns.containsAll(fk.columns))
				fKs.add(fk)
		]
		
		return fKs
	}
	
	// (KeyPropagation condition) Returns a list of foreign keys that are not included in the primary key of "t"
	def List<FKey> getFKsNotInPK(Table t) {
		val List<FKey> fKs = new LinkedList();
		
		val relationalschema.Key pk = findPK(t)
		if (pk === null)
			return fKs
	
		t.fks.forEach[ fk |
			if (!pk.columns.containsAll(fk.columns))
				fKs.add(fk)
		]
		
		return fKs
	}
	
	// Returns the primary key of "t"
	def Key findPK(Table t) {
		return t.keys.findFirst[ key | key.isIsPK == true ]
	}
	
	// Returns a list of unique keys of "t"
	def List<Key> findUKs(Table t) {
		return t.keys.filter[ key | key.isIsPK == false ].toList
	}
	
	def boolean isFKInUKs(FKey fk, List<Key> uKs) {
		for (uk : uKs) {
			if (uk.columns.containsAll(fk.columns))
				return true;
		}
		return false;
	}
	
	// Types conversion from Relational to USchema
	def static String typeConversionRelToUsc(String dataType) {
		val String dtUp = dataType.toUpperCase
		switch dtUp {
		 	case "VARCHAR": return "String"
		 	case "INT": return "int"
		 	case "DOUBLE": return "double"
		 	case "BOOLEAN": return "boolean"
 		}
	}
	
	// Columns to attributes
	def List<Attribute> columns2Attributes(List<Column> columns) {
		return columns.map[ c | attributes.get(c)]
	}
}