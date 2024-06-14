package juanfran.um

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import relationalschema.Table
import uschema.EntityType
import uschema.RelationshipType
import relationalschema.Column
import uschema.Attribute
import uschema.PrimitiveType
import relationalschema.Key
import uschema.Aggregate
import relationalschema.FKey
import uschema.Reference
import uschema.SchemaType
import java.util.List

class MappingRelational2UschemaTest {
	
	static val RELATIONAL_SCHEMA_0 = "test-input-files/Relational_0.xmi"
	static val RELATIONAL_SCHEMA_1 = "test-input-files/Relational_1.xmi"
	static val RELATIONAL_SCHEMA_2 = "test-input-files/Relational_2.xmi"
	static val RELATIONAL_SCHEMA_3 = "test-input-files/Relational_3.xmi"
	static val RELATIONAL_SCHEMA_4 = "test-input-files/Relational_4.xmi"
	static val RELATIONAL_SCHEMA_5 = "test-input-files/Relational_5.xmi"
	static val RELATIONAL_SCHEMA_6_1_1 = "test-input-files/Relational_6-1_1.xmi"
	static val RELATIONAL_SCHEMA_6_1_N = "test-input-files/Relational_6-1_N.xmi"
	static val RELATIONAL_SCHEMA_INTEGRATION = "test-input-files/Relational_integration.xmi"
	
	val MappingRelational2Uschema mapping = new MappingRelational2Uschema;
	
	// R0: RelationalSchema to USchema
	@Test
	def void relationalSchema2USchemaOK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_0)
		
		// R0
		mapping.relationalSchema2USchema()
		assertNotNull(mapping.USchema)
		assertEquals(mapping.USchema.name, mapping.relationalSchema.name)
	}
	
	// R1: Table to EntityType
	@Test
	def void table2SchemaType_EntityType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_1)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1 (EntityType)
		val Table t = mapping.relationalSchema.tables.head
		table2EntityType_asserts(t)
	}
	
	// R1: Table to RelationshipType
	@Test
	def void table2SchemaType_RelationshipType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_5)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1 (RelationshipType)
		val Table t = mapping.relationalSchema.tables.findFirst[ t | mapping.isMNTable(t) ]
		table2RelationshipType_asserts(t)
	}
	
	// R2: Column to Attribute (EntityType)
	@Test
	def void column2Attribute_EntityType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_2)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		val Table t = mapping.relationalSchema.tables.head
		table2EntityType_asserts(t)
	
		// R2
		val Column c = t.columns.head
		column2Attribute_asserts(c)
	}
	
	// R3: Key to uschema.Key
	@Test
	def void key2Key_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_3)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		val Table t = mapping.relationalSchema.tables.head
		table2EntityType_asserts(t)
		
		// R2
		t.columns.forEach[ c | column2Attribute_asserts(c) ]
		
		// R3 (PK)
		val Key pK = mapping.findPK(t)
		key2Key_asserts(pK)
		
		// R3 (UK)
		val Key uK = mapping.findUKs(t).head
		key2Key_asserts(uK)
	}
	
	// R4: Weak Table to Aggregate
	@Test
	def void weakTable2Aggregate_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_4)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | table2EntityType_asserts(t) ]
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | column2Attribute_asserts(c) ]
			t.keys.forEach[ k | key2Key_asserts(k) ]
		]
		
		// R4
		val Table w = mapping.relationalSchema.tables.findFirst[ t | t.name == "DetallesUsuario" ]
		weakTable2Aggregate_asserts(w)
	}
	
	// R5: M:N Table to RelationshipType
	@Test
	def void mNTable2RelationshipType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_5)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | 
			if (mapping.isMNTable(t))
				table2RelationshipType_asserts(t)
			else
				table2EntityType_asserts(t)
		]
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | column2Attribute_asserts(c) ]
			t.keys.forEach[ k | key2Key_asserts(k) ]
		]
		
		// R5
		val Table m = mapping.relationalSchema.tables.findFirst[ t | t.name == "Libros_Autores" ]
		mNTable2RelationshipType_asserts(m)
	}
	
	// R6: 1:1 Table
	@Test
	def void r6Table1_1_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_6_1_1)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | table2EntityType_asserts(t) ]
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | column2Attribute_asserts(c) ]
			t.keys.forEach[ k | key2Key_asserts(k) ]
		]
		
		// R6 (1:1)
		val Table t = mapping.relationalSchema.tables.findFirst[ t | t.name == "Reserva" ]
		val FKey fk = t.fks.head
		r6Table1_1_asserts(t, fk)
	}
	
	// R6: 1:N Table
	@Test
	def void r6Table1_N_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_6_1_N)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | table2EntityType_asserts(t) ]
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | column2Attribute_asserts(c) ]
			t.keys.forEach[ k | key2Key_asserts(k) ]
		]
		
		// R6 (1:N)
		val Table t = mapping.relationalSchema.tables.findFirst[ t | t.name == "Reserva" ]
		val FKey fk = t.fks.head
		r6Table1_N_asserts(t, fk)
	}
	
	// Integration test
	@Test
	def void relationalSchema2USchema_integration_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_INTEGRATION)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | 
			if (mapping.isMNTable(t))
				table2RelationshipType_asserts(t)
			else
				table2EntityType_asserts(t)
		]
		
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | column2Attribute_asserts(c) ] // r2
			t.keys.forEach[ k | key2Key_asserts(k) ] // r3
		]
		
		mapping.relationalSchema.tables.forEach[ t | 
			if (mapping.isWeakTable(t))
				weakTable2Aggregate_asserts(t) // r4
			else if (mapping.isMNTable(t))
				mNTable2RelationshipType_asserts(t) // r5
			else {
				// r6
				val List<FKey> fKs = mapping.getFKsNotInPK(t)
				val List<Key> uKs = mapping.findUKs(t)
				
				fKs.forEach[ fk |
					if (mapping.isFKInUKs(fk, uKs))
						r6Table1_1_asserts(t, fk)
					else
						r6Table1_N_asserts(t, fk)
				]
			}
		]
	}
	
	
	
	//----- PRIVATE METHODS -----//
	
	// R1 with asserts (EntityType)
	private def EntityType table2EntityType_asserts(Table t) {
		val int numEntities1 = mapping.USchema.entities.size
		
		mapping.table2SchemaType(t)
		
		val EntityType et = mapping.USchema.entities.findFirst[ r | r.name == t.name ]
		val int numEntities2 = mapping.USchema.entities.size
		
		assertNotNull(et)
		assertEquals(numEntities1+1, numEntities2)
		assertTrue(et.root)
		
		return et
	}
	
	// R1 with asserts (EntityType)
	private def void table2RelationshipType_asserts(Table t) {
		val int numRelationships1 = mapping.USchema.relationships.size
		
		mapping.table2SchemaType(t)
		
		val RelationshipType rt = mapping.USchema.relationships.findFirst[ r | r.name == t.name ]
		val int numRelationship2 = mapping.USchema.relationships.size
		
		assertNotNull(rt)
		assertEquals(numRelationships1+1, numRelationship2)
	}
	
	// R2 with asserts
	private def void column2Attribute_asserts(Column c) {
		var SchemaType st = mapping.USchema.entities.findFirst[ e | e.name == c.owner.name ]
		if (st === null)
			st = mapping.USchema.relationships.findFirst[ r | r.name == c.owner.name ]
		val int stFeaturesSize1 = st.features.size
		
		mapping.column2Attribute(c)
		
		val Attribute at = st.features.findFirst[ f | f.name == c.name ] as Attribute
		val int stFeaturesSize2 = st.features.size
		
		if (st instanceof RelationshipType && mapping.isColumnInFKsOrPKs(c)) {
			assertNull(at)
		} else {
			assertNotNull(at)
			assertEquals(stFeaturesSize1+1, stFeaturesSize2)
			assertEquals(MappingRelational2Uschema.typeConversionRelToUsc(c.datatype), (at.type as PrimitiveType).name)
			assertEquals(c.nullable, at.optional)
		}
	}
	
	// R3 with asserts
	private def void key2Key_asserts(Key k) {
		var EntityType et = mapping.USchema.entities.findFirst[ e | e.name == k.owner.name ]
		if (et === null)
			return
		val int etFeaturesSize1 = et.features.size

		mapping.key2Key(k)
		
		val int etFeaturesSize2 = et.features.size
		val uschema.Key u_k = et.features.findFirst[ f | f.name == k.constraintname ] as uschema.Key
		
		assertNotNull(u_k)
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		assertEquals(k.isIsPK, u_k.isIsID)
		assertEquals(k.columns.size, u_k.attributes.size)
	}
	
	// R4 with asserts
	private def void weakTable2Aggregate_asserts(Table w) {
		val FKey fk = mapping.getFKsInPK(w).head
		val Table s = fk.refsTo.owner
		val EntityType ew = mapping.USchema.entities.findFirst[ et | et.name == w.name ]
		val EntityType es = mapping.USchema.entities.findFirst[ et | et.name == s.name ]
		val int esFeaturesSize1 = es.features.size
		
		mapping.weakTable2Aggregate(w)
		
		val int esFeaturesSize2 = es.features.size
		val Aggregate g = es.features.findFirst[ f | f.name == w.name+"s" ] as Aggregate
		
		assertNotNull(g)
		assertEquals(esFeaturesSize1+1, esFeaturesSize2)
		assertEquals(w.name+"s", g.name)
		assertEquals(0, g.lowerBound)
		assertEquals(-1, g.upperBound)
		assertEquals(ew, g.specifiedBy)
		assertFalse(ew.root)
	}
	
	// R5 with asserts
	private def void mNTable2RelationshipType_asserts(Table m) {
		val FKey fk1 = m.fks.get(0) //Libro_FK
		val FKey fk2 = m.fks.get(1) //Autor_FK
		val Table t1 = fk1.refsTo.owner
		val Table t2 = fk2.refsTo.owner
		val EntityType et1 = mapping.USchema.entities.findFirst[ r | r.name == t1.name ]
		val EntityType et2 = mapping.USchema.entities.findFirst[ r | r.name == t2.name ]
		val RelationshipType rm = mapping.USchema.relationships.findFirst[ r | r.name == m.name ]
		val int rmNumReferences1 = rm.reference.size
		
		mapping.mNTable2RelationshipType(m)
		
		val int rmNumReferences2 = rm.reference.size
		assertEquals(rmNumReferences1+2, rmNumReferences2)
		
		// Reference 1
		val Reference ref1 = et1.features.findFirst[ f | f.name == fk2.constraintname ] as Reference
		assertNotNull(ref1)
		assertEquals(1, ref1.lowerBound)
		assertEquals(-1, ref1.upperBound)
		assertTrue(rm.reference.contains(ref1))
		
		// Attributes into Reference 1
		for (c : fk2.refsTo.columns) {
			val Attribute at = et2.features.findFirst[ f | f.name == c.name ] as Attribute
			assertNotNull(at)
			assertTrue(ref1.attributes.contains(at))
		}
		
		// Reference 2
		val Reference ref2 = et2.features.findFirst[ f | f.name == fk1.constraintname ] as Reference
		assertNotNull(ref2)
		assertEquals(1, ref2.lowerBound)		
		assertEquals(-1, ref2.upperBound)		
		assertTrue(rm.reference.contains(ref2))
		
		// Attributes into Reference 2
		for (c : fk1.refsTo.columns) {
			val Attribute at = et1.features.findFirst[ f | f.name == c.name ] as Attribute
			assertNotNull(at)
			assertTrue(ref2.attributes.contains(at))
		}
	}
	
	// R6 with asserts (1:1 table)
	private def void r6Table1_1_asserts(Table t, FKey fk) {
		val Table s = fk.refsTo.owner
		val EntityType et = mapping.USchema.entities.findFirst[ r | r.name == t.name ]
		val EntityType es = mapping.USchema.entities.findFirst[ r | r.name == s.name ]
		val int etFeaturesSize1 = et.features.size
		
		mapping.r6Table1_1(t, fk)
		
		val int etFeaturesSize2 = et.features.size
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		
		// Reference
		val Reference rs = et.features.findFirst[ f | f.name == s.name+"_"+fk.constraintname ] as Reference
		assertNotNull(rs)
		assertEquals(0, rs.lowerBound)
		assertEquals(1, rs.upperBound)
		assertEquals(es, rs.refsTo)
		
		// Attributes into Reference
		for (c : fk.columns) {
			val Attribute at = et.features.findFirst[ f | f.name == c.name ] as Attribute
			assertNotNull(at)
			assertTrue(rs.attributes.contains(at))
		}
	}
	
	// R6 with asserts (1:N table)
	private def void r6Table1_N_asserts(Table t, FKey fk) {
		val Table s = fk.refsTo.owner
		val EntityType et = mapping.USchema.entities.findFirst[ r | r.name == t.name ]
		val EntityType es = mapping.USchema.entities.findFirst[ r | r.name == s.name ]
		val Key pk = mapping.findPK(t)
		val int esFeaturesSize1 = es.features.size
		
		mapping.r6Table1_N(t, fk)
		
		val int esFeaturesSize2 = es.features.size
		assertEquals(esFeaturesSize1+2, esFeaturesSize2)
		
		// Reference
		val Reference rt = es.features.findFirst[ f | f.name == t.name+"s" ] as Reference
		assertNotNull(rt)
		assertEquals(0, rt.lowerBound)
		assertEquals(-1, rt.upperBound)
		assertEquals(et, rt.refsTo)
		
		// Attribute
		for (c : pk.columns) {
			var Attribute at = es.features.findFirst[ f | f.name == c.name+et.name+"s" ] as Attribute
			assertNotNull(at)
			assertEquals(MappingRelational2Uschema.typeConversionRelToUsc(c.datatype), (at.type as PrimitiveType).name)
			assertTrue(rt.attributes.contains(at))
			assertTrue(at.references.contains(rt))
		}
	}
}