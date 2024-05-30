package juanfran.um

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach
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
import uschema.Feature

class MappingRelational2UschemaTest {
	
	static val RELATIONAL_SCHEMA_0 = "test-input-files/Relational_0.xmi"
	static val RELATIONAL_SCHEMA_1 = "test-input-files/Relational_1.xmi"
	static val RELATIONAL_SCHEMA_2 = "test-input-files/Relational_2.xmi"
	static val RELATIONAL_SCHEMA_3 = "test-input-files/Relational_3.xmi"
	static val RELATIONAL_SCHEMA_4 = "test-input-files/Relational_4.xmi"
	static val RELATIONAL_SCHEMA_5 = "test-input-files/Relational_5.xmi"
	static val RELATIONAL_SCHEMA_6_1_1 = "test-input-files/Relational_6-1_1.xmi"
	static val RELATIONAL_SCHEMA_6_1_N = "test-input-files/Relational_6-1_N.xmi"
	
	val MappingRelational2Uschema mapping = new MappingRelational2Uschema;
	
	@Test
	def void relationalSchema2USchemaOK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_0)
		mapping.relationalSchema2USchema()
		assertNotNull(mapping.USchema)
		assertEquals(mapping.USchema.name, mapping.relationalSchema.name)
	}
	
	@Test
	def void table2SchemaType_EntityType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_1)
		mapping.relationalSchema2USchema()
		
		val int numEntities1 = mapping.USchema.entities.size
		val Table t = mapping.relationalSchema.tables.head
		
		mapping.table2SchemaType(t)
		
		val int numEntities2 = mapping.USchema.entities.size
		val EntityType et = mapping.USchema.entities.head
		
		assertEquals(numEntities1+1, numEntities2)
		assertEquals(t.name, et.name)
		assertTrue(et.root)
	}
	
	@Test
	def void table2SchemaType_RelationshipType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_5)
		mapping.relationalSchema2USchema()
		
		val int numRelationships1 = mapping.USchema.relationships.size
		val Table t = mapping.relationalSchema.tables.findFirst[ t | mapping.isMNTable(t) ]

		mapping.table2SchemaType(t)
		
		val int numRelationships2 = mapping.USchema.relationships.size
		val RelationshipType rt = mapping.USchema.relationships.head

		assertEquals(numRelationships1+1, numRelationships2)
		assertEquals(t.name, rt.name)
	}
	
	@Test
	def void column2Attribute_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_2)
		mapping.relationalSchema2USchema()
		val Table t = mapping.relationalSchema.tables.head
		mapping.table2SchemaType(t)
	
		val Column c = t.columns.head
		val EntityType et = mapping.USchema.entities.head
		val int etFeaturesSize1 = et.features.size
		
		mapping.column2Attribute(c)
		
		val Attribute at = et.features.head as Attribute
		val int etFeaturesSize2 = et.features.size
		
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		assertEquals(c.name, at.name)
		assertEquals("int", (at.type as PrimitiveType).name)
		assertEquals(c.nullable, at.optional)
	}
	
	@Test
	def void key2Key_PK_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_3)
		mapping.relationalSchema2USchema()
		val Table t = mapping.relationalSchema.tables.head
		mapping.table2SchemaType(t)
		t.columns.forEach[ c | mapping.column2Attribute(c) ]
		
		val Key pK = mapping.findPK(t)
		val EntityType et = mapping.USchema.entities.head
		val int etFeaturesSize1 = et.features.size

		mapping.key2Key(pK)
		
		val uschema.Key k = et.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		val int etFeaturesSize2 = et.features.size
		
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		assertEquals(pK.constraintname, k.name)
		assertTrue(k.isIsID)
		assertEquals(pK.columns.size, k.attributes.size)
	}
	
	@Test
	def void key2Key_UK_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_3)
		mapping.relationalSchema2USchema()
		val Table t = mapping.relationalSchema.tables.head
		mapping.table2SchemaType(t)
		t.columns.forEach[ c | mapping.column2Attribute(c) ]
		
		val Key uK = mapping.findUKs(t).head
		val EntityType et = mapping.USchema.entities.head
		val int etFeaturesSize1 = et.features.size

		mapping.key2Key(uK)
		
		val uschema.Key k = et.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		val int etFeaturesSize2 = et.features.size
		
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		assertEquals(uK.constraintname, k.name)
		assertFalse(k.isIsID)
		assertEquals(uK.columns.size, k.attributes.size)
	}
	
	@Test
	def void weakTable2Aggregate_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_4)
		mapping.relationalSchema2USchema()
		mapping.relationalSchema.tables.forEach[ t | mapping.table2SchemaType(t) ]
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		
		val Table w = mapping.trace.getSourceInstance("DetallesUsuario").head as Table
		val Table s = mapping.trace.getSourceInstance("Usuario").head as Table
		val EntityType ew = mapping.trace.getTargetInstance(w.name).head as EntityType
		val EntityType es = mapping.trace.getTargetInstance(s.name).head as EntityType
		val int esFeaturesSize1 = es.features.size
		
		mapping.weakTable2Aggregate(w)
		
		val int esFeaturesSize2 = es.features.size
		val Aggregate g = mapping.trace.getTargetInstance("DetallesUsuario.Usuario_FK").head as Aggregate
		
		assertEquals(esFeaturesSize1+1, esFeaturesSize2)
		assertEquals(w.name+"s", g.name)
		assertEquals(0, g.lowerBound)
		assertEquals(-1, g.upperBound)
		assertEquals(ew, g.specifiedBy)
		assertFalse(ew.root)
	}
	
	@Test
	def void mNTable2RelationshipType_OK() {
		fail("Not yet implemented")
	}
	
	@Test
	def void r6Table1_1_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_6_1_1)
		mapping.relationalSchema2USchema()
		mapping.relationalSchema.tables.forEach[ t | mapping.table2SchemaType(t) ]
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		
		val Table t = mapping.trace.getSourceInstance("Reserva").head as Table
		val FKey t_fk = t.fks.head
		val Table s = mapping.trace.getSourceInstance("Libro").head as Table
		val EntityType et = mapping.trace.getTargetInstance(t.name).head as EntityType
		val EntityType es = mapping.trace.getTargetInstance(s.name).head as EntityType
		val int etFeaturesSize1 = et.features.size
		
		mapping.r6Table1_1(t, t_fk)
		
		val int etFeaturesSize2 = et.features.size
		val Reference rs = mapping.trace.getTargetInstance("Reserva.Libro_FK").head as Reference
		
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		assertTrue(et.features.contains(rs))
		assertEquals(s.name+"_"+t_fk.constraintname, rs.name)
		assertEquals(0, rs.lowerBound)
		assertEquals(1, rs.upperBound)
		assertEquals(es, rs.refsTo)
		assertTrue(rs.attributes.containsAll(mapping.columns2Attributes(t_fk.columns)))
	}
	
	@Test
	def void r6Table1_N_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_6_1_N)
		mapping.relationalSchema2USchema()
		mapping.relationalSchema.tables.forEach[ t | mapping.table2SchemaType(t) ]
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		
		val Table t = mapping.trace.getSourceInstance("Reserva").head as Table
		val FKey t_fk = t.fks.head
		val Table s = mapping.trace.getSourceInstance("Libro").head as Table
		val EntityType et = mapping.trace.getTargetInstance(t.name).head as EntityType
		val EntityType es = mapping.trace.getTargetInstance(s.name).head as EntityType
		val int esFeaturesSize1 = es.features.size
		
		mapping.r6Table1_N(t, t_fk)
		
		val int esFeaturesSize2 = es.features.size
		val features = mapping.trace.getTargetInstance("Reserva.Libro_FK")
		var Reference rt;
		var Attribute at;
		for (ft : features) {
			switch ft {
				Reference: { rt = ft }
				Attribute: { at = ft }
			}
		}
		val Column col = mapping.findPK(t).columns.head
		
		assertEquals(esFeaturesSize1+2, esFeaturesSize2)
		assertEquals(t.name+"s", rt.name)
		assertEquals(0, rt.lowerBound)
		assertEquals(-1, rt.upperBound)
		assertEquals(et, rt.refsTo)
		assertEquals(col.name+et.name+"s", at.name)
		assertEquals("int", (at.type as PrimitiveType).name)
		assertTrue(rt.attributes.contains(at))
		assertTrue(at.references.contains(rt))
		assertTrue(es.features.contains(at))
	}
	
}