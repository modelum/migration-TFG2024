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
		val int numEntities1 = mapping.USchema.entities.size
		val Table t = mapping.relationalSchema.tables.head
		
		mapping.table2SchemaType(t)
		
		val int numEntities2 = mapping.USchema.entities.size
		val EntityType et = mapping.USchema.entities.head
		
		assertNotNull(et)
		assertEquals(numEntities1+1, numEntities2)
		assertEquals(t.name, et.name)
		assertTrue(et.root)
	}
	
	// R1: Table to RelationshipType
	@Test
	def void table2SchemaType_RelationshipType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_5)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1 (RelationshipType)
		val int numRelationships1 = mapping.USchema.relationships.size
		val Table t = mapping.relationalSchema.tables.findFirst[ t | mapping.isMNTable(t) ]

		mapping.table2SchemaType(t)
		
		val int numRelationships2 = mapping.USchema.relationships.size
		val RelationshipType rt = mapping.USchema.relationships.head

		assertNotNull(rt)
		assertEquals(numRelationships1+1, numRelationships2)
		assertEquals(t.name, rt.name)
	}
	
	// R2: Column to Attribute (EntityType)
	@Test
	def void column2Attribute_EntityType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_2)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		val Table t = mapping.relationalSchema.tables.head
		mapping.table2SchemaType(t)
	
		val Column c = t.columns.head
		val EntityType et = mapping.USchema.entities.head
		val int etFeaturesSize1 = et.features.size
		
		mapping.column2Attribute(c)
		
		val Attribute at = et.features.head as Attribute
		val int etFeaturesSize2 = et.features.size
		
		assertNotNull(at)
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		assertEquals(c.name, at.name)
		assertEquals("int", (at.type as PrimitiveType).name)
		assertEquals(c.nullable, at.optional)
	}
	
	// R3: Key to uschema.Key
	@Test
	def void key2Key_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_3)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		val Table t = mapping.relationalSchema.tables.head
		mapping.table2SchemaType(t)
		
		// R2
		t.columns.forEach[ c | mapping.column2Attribute(c) ]
		
		// R3
		val Key pK = mapping.findPK(t)
		val Key uK = mapping.findUKs(t).head
		val EntityType et = mapping.USchema.entities.head
		val int etFeaturesSize1 = et.features.size

		mapping.key2Key(pK)
		mapping.key2Key(uK)
		
		val int etFeaturesSize2 = et.features.size
		assertEquals(etFeaturesSize1+2, etFeaturesSize2)
		
		// PK
		val uschema.Key u_pk = et.features.findFirst[ f | f.name == "Usuario_PK" ] as uschema.Key
		assertNotNull(u_pk)
		assertEquals(pK.constraintname, u_pk.name)
		assertTrue(u_pk.isIsID)
		assertEquals(pK.columns.size, u_pk.attributes.size)
		
		// UK
		val uschema.Key u_uk = et.features.findFirst[ f | f.name == "EmailTelefono_UK" ] as uschema.Key
		assertNotNull(u_uk)
		assertEquals(uK.constraintname, u_uk.name)
		assertFalse(u_uk.isIsID)
		assertEquals(uK.columns.size, u_uk.attributes.size)
		
	}
	
	// R4: Weak Table to Aggregate
	@Test
	def void weakTable2Aggregate_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_4)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		val Table w = mapping.relationalSchema.tables.findFirst[ t | t.name == "DetallesUsuario" ]
		val Table s = mapping.relationalSchema.tables.findFirst[ t | t.name == "Usuario" ]
		mapping.table2SchemaType(w)
		mapping.table2SchemaType(s)
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		
		// R4
		val EntityType ew = mapping.USchema.entities.findFirst[ et | et.name == "DetallesUsuario" ]
		val EntityType es = mapping.USchema.entities.findFirst[ et | et.name == "Usuario" ]
		val int esFeaturesSize1 = es.features.size
		
		mapping.weakTable2Aggregate(w)
		
		val int esFeaturesSize2 = es.features.size
		val Aggregate g = mapping.trace.getTargetInstance("DetallesUsuario.Usuario_FK").head as Aggregate
		
		assertNotNull(g)
		assertEquals(esFeaturesSize1+1, esFeaturesSize2)
		assertEquals(w.name+"s", g.name)
		assertEquals(0, g.lowerBound)
		assertEquals(-1, g.upperBound)
		assertEquals(ew, g.specifiedBy)
		assertFalse(ew.root)
	}
	
	// R5: M:N Table to RelationshipType
	@Test
	def void mNTable2RelationshipType_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_5)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | mapping.table2SchemaType(t) ]
		
		// R2, R3
		val RelationshipType rm = mapping.USchema.relationships.findFirst[ r | r.name == "Libros_Autores" ]
		val int rmNumFeatures1 = rm.features.size
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		val int rmNumFeatures2 = rm.features.size
		assertEquals(rmNumFeatures1+1, rmNumFeatures2)
		
		// Only one Attribute added (which is not part of a Key)
		val Attribute libro = rm.features.findFirst[ f | f.name == "Libro" ] as Attribute
		val Attribute autor = rm.features.findFirst[ f | f.name == "Autor" ] as Attribute
		val Attribute editorial = rm.features.findFirst[ f | f.name == "Editorial" ] as Attribute
		assertNull(libro)
		assertNull(autor)
		assertNotNull(editorial)
		
		// Key not added
		val Key libros_Autores_PK = rm.features.findFirst[ f | f.name == "Libros_Autores_PK" ] as Key
		assertNull(libros_Autores_PK)
		
		// R5
		val Table m = mapping.relationalSchema.tables.findFirst[ t | t.name == "Libros_Autores" ]
		val FKey fk1 = m.fks.get(0) //Libro_FK
		val FKey fk2 = m.fks.get(1) //Autor_FK
		val EntityType et1 = mapping.USchema.entities.findFirst[ r | r.name == "Libro" ]
		val EntityType et2 = mapping.USchema.entities.findFirst[ r | r.name == "Autor" ]
		val int rmNumReferences1 = rm.reference.size
		
		mapping.mNTable2RelationshipType(m)
		val int rmNumReferences2 = rm.reference.size
		assertEquals(rmNumReferences1+2, rmNumReferences2)
		
		// Reference 1
		val Reference ref1 = et1.features.findFirst[ f | f.name == "Autor_FK" ] as Reference
		assertNotNull(ref1)
		assertEquals(fk2.constraintname, ref1.name)
		assertEquals(1, ref1.lowerBound)
		assertEquals(-1, ref1.upperBound)
		assertTrue(rm.reference.contains(ref1))
		
		// Attribute into Reference 1
		val Attribute at1 = et2.features.findFirst[ f | f.name == "AutorID" ] as Attribute
		assertNotNull(at1)
		assertTrue(ref1.attributes.contains(at1))
		
		// Reference 2
		val Reference ref2 = et2.features.findFirst[ f | f.name == "Libro_FK" ] as Reference
		assertNotNull(ref2)
		assertEquals(fk1.constraintname, ref2.name)		
		assertEquals(1, ref2.lowerBound)		
		assertEquals(-1, ref2.upperBound)		
		assertTrue(rm.reference.contains(ref2))
		
		// Attribute into Reference 2
		val Attribute at2 = et1.features.findFirst[ f | f.name == "LibroID" ] as Attribute
		assertNotNull(at2)
		assertTrue(ref2.attributes.contains(at2))
	}
	
	// R6: 1:1 Table
	@Test
	def void r6Table1_1_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_6_1_1)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | mapping.table2SchemaType(t) ]
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		
		// R6 (1:1)
		val Table t = mapping.relationalSchema.tables.findFirst[ t | t.name == "Reserva" ]
		val FKey t_fk = t.fks.head
		val Table s = mapping.relationalSchema.tables.findFirst[ table | table.name == "Libro" ]
		val EntityType et = mapping.USchema.entities.findFirst[ r | r.name == "Reserva" ]
		val EntityType es = mapping.USchema.entities.findFirst[ r | r.name == "Libro" ]
		val int etFeaturesSize1 = et.features.size
		
		mapping.r6Table1_1(t, t_fk)
		
		val int etFeaturesSize2 = et.features.size
		assertEquals(etFeaturesSize1+1, etFeaturesSize2)
		
		// Reference
		val Reference rs = et.features.findFirst[ f | f.name == "Libro_Libro_FK" ] as Reference
		assertNotNull(rs)
		assertTrue(et.features.contains(rs))
		assertEquals(s.name+"_"+t_fk.constraintname, rs.name)
		assertEquals(0, rs.lowerBound)
		assertEquals(1, rs.upperBound)
		assertEquals(es, rs.refsTo)
		
		// Attribute into Reference
		val Attribute at = et.features.findFirst[ f | f.name == "Libro" ] as Attribute
		assertNotNull(at)
		assertTrue(rs.attributes.contains(at))
	}
	
	// R6: 1:N Table
	@Test
	def void r6Table1_N_OK() {
		mapping.loadSchema(RELATIONAL_SCHEMA_6_1_N)
		
		// R0
		mapping.relationalSchema2USchema()
		
		// R1
		mapping.relationalSchema.tables.forEach[ t | mapping.table2SchemaType(t) ]
		
		// R2, R3
		mapping.relationalSchema.tables.forEach[ t |
			t.columns.forEach [ c | mapping.column2Attribute(c) ]
			t.keys.forEach[ k | mapping.key2Key(k) ]
		]
		
		// R6 (1:N)
		val Table t = mapping.relationalSchema.tables.findFirst[ t | t.name == "Reserva" ]
		val FKey t_fk = t.fks.head
		val Table s = mapping.relationalSchema.tables.findFirst[ table | table.name == "Libro" ]
		val EntityType et = mapping.USchema.entities.findFirst[ r | r.name == "Reserva" ]
		val EntityType es = mapping.USchema.entities.findFirst[ r | r.name == "Libro" ]
		val int esFeaturesSize1 = es.features.size
		
		mapping.r6Table1_N(t, t_fk)
		
		val int esFeaturesSize2 = es.features.size
		assertEquals(esFeaturesSize1+2, esFeaturesSize2)
		
		// Reference
		val Reference rt = es.features.findFirst[ f | f instanceof Reference ] as Reference
		assertNotNull(rt)
		assertEquals(t.name+"s", rt.name)
		assertEquals(0, rt.lowerBound)
		assertEquals(-1, rt.upperBound)
		assertEquals(et, rt.refsTo)
		
		// Attribute
		val Column col = t.columns.findFirst[ c | c.name == "ReservaID" ]
		var Attribute at = es.features.findFirst[ f | f.name == "ReservaIDReservas" ] as Attribute
		assertNotNull(at)
		assertEquals(col.name+et.name+"s", at.name)
		assertEquals("int", (at.type as PrimitiveType).name)
		assertTrue(rt.attributes.contains(at))
		assertTrue(at.references.contains(rt))
	}
	
}