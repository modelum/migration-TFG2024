package juanfran.um

import uschema.UschemaFactory
import relationalschema.RelationalSchema
import uschema.USchema
import relationalschema.Table
import uschema.SchemaType
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
import juanfran.um.trace.Trace

class MappingRelational2Uschema {
	
	var RelationalSchema relationalSchema;
	var USchema uSchema;
	val UschemaFactory usFactory;
	val Trace trace;
	
	new() {
		this.usFactory = UschemaFactory.eINSTANCE
		this.trace = new Trace()
	}
	
	// R0: RelationalSchema to USchema
	def USchema relationalSchema2USchema(RelationalSchema rs) {
		relationalSchema = rs
		uSchema = usFactory.createUSchema
		
		uSchema.name = relationalSchema.name
		
		relationalSchema.tables.forEach[ t | table2SchemaType(t) ] //r1
		
		relationalSchema.tables.forEach[ t | 
			if (isWeakTable(t))
				weakTable2Aggregate(t) //r4
			else if (isMNTable(t))
				mNTable2RelationshipType(t) //r5
			else
				r6(t) //r6
		]
		
		trace.addTrace(relationalSchema.name, relationalSchema, uSchema.name, uSchema);
		trace.printDirectTraceTypes

		return uSchema
	}
	
	// R1: Table to SchemaType
	def void table2SchemaType(Table t) {
		var SchemaType st;
		
		if (isMNTable(t)) {
			st = usFactory.createRelationshipType
			uSchema.relationships.add(st as RelationshipType)
		} else {
			val EntityType et = usFactory.createEntityType
			et.root = true
			uSchema.entities.add(et)
			st = et
		}
		st.name = t.name
		
		trace.addTrace(t.name, t, st.name, st)
		
		for(c : t.columns) { column2Attribute(c, st) } //r2
		for(k : t.keys) { pK2Key(k, st) } //r3
	}
	
	// R2: Column to Attribute
	def void column2Attribute(Column c, SchemaType st) {
		val Attribute at = usFactory.createAttribute
		val PrimitiveType primitiveType = usFactory.createPrimitiveType
		primitiveType.name = typeConversionRelToUsc(c.datatype)
		
		// New Attribute
		at.name = c.name
		at.optional = c.nullable
		st.features.add(at)
		
		trace.addTrace(c.owner.name+"."+c.name, c, at.owner.name+"."+at.name, at)
		
		// New DataType (PrimitiveType)
		at.type = primitiveType
		
		trace.addTrace(c.owner.name+"."+c.name, c, at.owner.name+"."+at.name+"."+primitiveType.name, at.type)
	}
	
	// R3: PK & UK to Key
	def void pK2Key(Key rKey, SchemaType st) {
		val uschema.Key uKey = usFactory.createKey
		
		// New uschema.Key
		uKey.name = rKey.constraintname
		uKey.isID = rKey.isIsPK
		uKey.attributes.addAll(columns2Attributes(rKey.columns))
		st.features.add(uKey)
		
		trace.addTrace(rKey.owner.name+"."+rKey.constraintname, rKey, uKey.owner.name+"."+uKey.name, uKey)
	}
	
	// R4: Weak Table to Aggregate
	def void weakTable2Aggregate(Table w) {
		val FKey fk = getFKsInPK(w).head
		val Table s = fk.refsTo.owner
		val EntityType es = trace.getTargetInstance(s.name).head as EntityType
		val EntityType ew = trace.getTargetInstance(w.name).head as EntityType
		val Aggregate ag = usFactory.createAggregate
	
		// New Aggregate
		ag.name = w.name + "s"
		ag.lowerBound = 0
		ag.upperBound = -1 // Many (n)
		ag.specifiedBy = ew
		ew.root = false
		es.features.add(ag)
		
		trace.addTrace(fk.owner.name+"."+fk.constraintname, fk, ag.owner.name+"."+ag.name, ag)
	}
	
	// R5: M:N Table to RelationshipType
	def void mNTable2RelationshipType(Table m) {
		val RelationshipType rm = trace.getTargetInstance(m.name).head as RelationshipType
		val List<FKey> fKs = getFKsInPK(m)
		val FKey fK1 = fKs.get(0)
		val FKey fK2 = fKs.get(1)
		val Table t1 = fK1.refsTo.owner
		val Table t2 = fK2.refsTo.owner
		val EntityType et1 = trace.getTargetInstance(t1.name).head as EntityType
		val EntityType et2 = trace.getTargetInstance(t2.name).head as EntityType
		val Reference ref1 = usFactory.createReference
		val Reference ref2 = usFactory.createReference
		
		// New Reference 1
		ref1.name = t1.name
		ref1.lowerBound = 1
		ref1.upperBound = 1
		ref1.refsTo = et1
		ref1.isFeaturedBy = rm
		ref1.attributes.addAll(columns2Attributes(fK1.columns))
		et1.features.add(ref1)
		
		trace.addTrace(fK1.owner.name+"."+fK1.constraintname, fK1, ref1.owner.name+"."+ref1.name, ref1)
		
		// New Reference 2
		ref2.name = t2.name
		ref2.lowerBound = 1
		ref2.upperBound = 1
		ref2.refsTo = et2
		ref2.isFeaturedBy = rm
		ref2.attributes.addAll(columns2Attributes(fK2.columns))
		et2.features.add(ref2)
		
		trace.addTrace(fK2.owner.name+"."+fK2.constraintname, fK2, ref2.owner.name+"."+ref2.name, ref2)
	}
	
	// R6
	def void r6(Table t) {
		val List<FKey> fKs = getFKsNotInPK(t) // KeyPropagation condition
		val List<Key> uKs = findUKs(t)
		
		fKs.forEach[ fk |
			if (isFKInUKs(fk, uKs))
				r6Table1_1(t, fk)
			else
				r6Table1_N(t, fk)
		]
	}
	
	// R6: 1:1 Table
	def void r6Table1_1(Table t, FKey fk) {
		val Table s = fk.refsTo.owner
		val EntityType et = trace.getTargetInstance(t.name).head as EntityType
		val EntityType es = trace.getTargetInstance(s.name).head as EntityType
		val Reference rs = usFactory.createReference
		
		// New Reference
		rs.name = s.name + "_" + fk.constraintname
		rs.lowerBound = 0
		rs.upperBound = 1
		rs.refsTo = es
		rs.attributes.addAll(columns2Attributes(fk.columns))
		et.features.add(rs)
		
		trace.addTrace(fk.owner.name+"."+fk.constraintname, fk, rs.owner.name+"."+rs.name, rs)
	}
	
	// R6: 1:N Table
	def r6Table1_N(Table t, FKey fk) {
		val Table s = fk.refsTo.owner
		val EntityType et = trace.getTargetInstance(t.name).head as EntityType
		val EntityType es = trace.getTargetInstance(s.name).head as EntityType
		val Reference rt = usFactory.createReference
		val Key pk = findPK(t);
		
		// New Reference
		rt.name = t.name + 's'
		rt.lowerBound = 0
		rt.upperBound = -1 // Many (n)
		rt.refsTo = et
		es.features.add(rt)
		
		trace.addTrace(fk.owner.name+"."+fk.constraintname, fk, rt.owner.name+"."+rt.name, rt)
		
		// New Attributes
		pk.columns.forEach[ col |
			val Attribute at = usFactory.createAttribute
			val PrimitiveType primitiveType = usFactory.createPrimitiveType
			primitiveType.name = typeConversionRelToUsc(col.datatype)
		
			at.name = col.name + et.name + 's'
			rt.attributes.add(at)
			es.features.add(at)
			
			trace.addTrace(fk.owner.name+"."+fk.constraintname, fk, at.owner.name+"."+at.name, at)
			
			at.type = primitiveType
			
			trace.addTrace(fk.owner.name+"."+fk.constraintname, fk, at.owner.name+"."+at.name+"."+primitiveType.name, at.type)
		]
	}
	
	// Checks whether a table satisfies the weak condition
	def boolean isWeakTable(Table t) {
		val List<FKey> fKs = getFKsInPK(t)

		if (fKs.size == 1)
			return true
		
		return false
	}
	
	// Checks whether a table satisfies the M:N condition
	def boolean isMNTable(Table t) {
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
	
	// Returns true whether a FK is found within any UK
	def boolean isFKInUKs(FKey fk, List<Key> uKs) {
		for (uk : uKs) {
			if (uk.columns.containsAll(fk.columns))
				return true;
		}
		return false;
	}
	
	// Columns to attributes
	def List<Attribute> columns2Attributes(List<Column> columns) {
		return columns.map[ c | 
			val String columnName = c.owner.name+"."+c.name
			val at = trace.getTargetInstance(columnName).head as Attribute
			
			return at
		]
	}
	
	// Types conversion from Relational to USchema
	def static String typeConversionRelToUsc(String dataType) {
		val String dtUp = dataType.toUpperCase
		switch dtUp {
		 	case "VARCHAR": return "String"
		 	case "INT": return "int"
		 	case "DOUBLE": return "double"
		 	case "BOOLEAN": return "boolean"
		 	case "DATE": return "Date"
 		}
	}
}