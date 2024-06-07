package es.um.dsdm.uschema2relational.test

import es.um.dsdm.uschema2relational.MappingUSchema2Relational
import org.junit.Before
import static org.junit.Assert.*
import org.junit.Test
import uschema.Attribute
import relationalschema.Table
import java.util.List
import uschema.Reference
import relationalschema.ReferentialAction
import uschema.Aggregate

class MappingUSchema2RelationalTest {
	
	static val USCHEMA_0 = "inputs/USchema-Regla0.xmi"
	static val USCHEMA_1 = "inputs/USchema-Regla1.xmi"
	static val USCHEMA_2 = "inputs/USchema-Regla2.xmi"
	static val USCHEMA_3 = "inputs/USchema-Regla3.xmi"
	static val USCHEMA_4 = "inputs/USchema-Regla4.xmi"
	static val USCHEMA_5 = "inputs/USchema-Regla5.xmi"
	static val USCHEMA_6 = "inputs/USchema-Regla6.xmi"
	
	
	MappingUSchema2Relational mapper
	
	@Before
	def void setUp() {
		mapper = new MappingUSchema2Relational
	}
	
	// R0
	@Test
	def void testUSchema2RelationalSchema() {
		mapper.loadSchema(USCHEMA_0)
		mapper.uschema2relational()
		assertNotNull(mapper.relationalSchema)
		assertEquals(mapper.relationalSchema.name, mapper.uschema.name)
	}
	
	// R1
	@Test
	def void testEntityType2Table() {
		mapper.loadSchema(USCHEMA_1)
		mapper.uschema2relational()
		val entidad1 = mapper.uschema.entities.get(0)
		val entidad2 = mapper.uschema.entities.get(1)
		mapper.entityType2Table(entidad1)
		mapper.entityType2Table(entidad2)
		assertEquals(entidad1.name, mapper.relationalSchema.tables.get(0).name)
		assertEquals(entidad2.name, mapper.relationalSchema.tables.get(1).name)
	}
	
	// R2
	@Test
	def void testAttribute2Column() {
		mapper.loadSchema(USCHEMA_2)
		mapper.uschema2relational()
		val entidad1 = mapper.uschema.entities.get(0)
		val entidad2 = mapper.uschema.entities.get(1)
		mapper.entityType2Table(entidad1)
		// Atributo 1 - String
		val Attribute atributo1 = entidad1.features.get(0) as Attribute
		mapper.attribute2Column(atributo1)
		assertEquals(atributo1.name, mapper.relationalSchema.tables.get(0).columns.get(0).name)
		assertEquals(255, mapper.relationalSchema.tables.get(0).columns.get(0).size)
		assertTrue(mapper.relationalSchema.tables.get(0).columns.get(0).nullable)
		assertNull(mapper.relationalSchema.tables.get(0).columns.get(0).defaultvalue)
		assertEquals(mapper.relationalSchema.tables.get(0).columns.get(0).datatype, "VARCHAR")
		// Atributo 2 - Numerico (int)
		val Attribute atributo2 = entidad1.features.get(1) as Attribute
		mapper.attribute2Column(atributo2)
		assertEquals(atributo2.name, mapper.relationalSchema.tables.get(0).columns.get(1).name)
		assertEquals(38, mapper.relationalSchema.tables.get(0).columns.get(1).size)
		assertTrue(mapper.relationalSchema.tables.get(0).columns.get(1).nullable)
		assertNull(mapper.relationalSchema.tables.get(0).columns.get(1).defaultvalue)
		assertEquals(mapper.relationalSchema.tables.get(0).columns.get(1).datatype, "NUMERIC")
		// Atributo 3 - Numerico (double)
		val Attribute atributo3 = entidad1.features.get(2) as Attribute
		mapper.attribute2Column(atributo3)
		assertEquals(atributo3.name, mapper.relationalSchema.tables.get(0).columns.get(2).name)
		assertEquals(38, mapper.relationalSchema.tables.get(0).columns.get(2).size)
		assertTrue(mapper.relationalSchema.tables.get(0).columns.get(2).nullable)
		assertNull(mapper.relationalSchema.tables.get(0).columns.get(2).defaultvalue)
		assertEquals(mapper.relationalSchema.tables.get(0).columns.get(2).datatype, "NUMERIC")
		// Tabla 2
		mapper.entityType2Table(entidad2)
		// Atributo 4 - Date
		val Attribute atributo4 = entidad2.features.get(0) as Attribute
		mapper.attribute2Column(atributo4)
		assertEquals(atributo4.name, mapper.relationalSchema.tables.get(1).columns.get(0).name)
		assertEquals(10, mapper.relationalSchema.tables.get(1).columns.get(0).size)
		assertTrue(mapper.relationalSchema.tables.get(1).columns.get(0).nullable)
		assertNull(mapper.relationalSchema.tables.get(1).columns.get(0).defaultvalue)
		assertEquals(mapper.relationalSchema.tables.get(1).columns.get(0).datatype, "DATE")		
		// Atributo 5 - Boolean
		val Attribute atributo5 = entidad2.features.get(1) as Attribute
		mapper.attribute2Column(atributo5)
		assertEquals(atributo5.name, mapper.relationalSchema.tables.get(1).columns.get(1).name)
		assertEquals(1, mapper.relationalSchema.tables.get(1).columns.get(1).size)
		assertTrue(mapper.relationalSchema.tables.get(1).columns.get(1).nullable)
		assertNull(mapper.relationalSchema.tables.get(1).columns.get(1).defaultvalue)
		assertEquals(mapper.relationalSchema.tables.get(1).columns.get(1).datatype, "BOOLEAN")
	}
	
	// R3
	@Test
	def void testKey2Key() {
		mapper.loadSchema(USCHEMA_3)
		mapper.uschema2relational()
		val entidad1 = mapper.uschema.entities.get(0)
		mapper.entityType2Table(entidad1)
		val List<uschema.Key> keys = newArrayList
		entidad1.features.forEach[feature|
			if (feature instanceof uschema.Key) {
				keys.add(feature)
				mapper.key2key(feature as uschema.Key)
			} else if (feature instanceof Attribute) {
				mapper.attribute2Column(feature as Attribute)
			}
		]
		// Key 1
		val Table tabla = mapper.relationalSchema.tables.get(0)
		val relationalschema.Key key1 = tabla.keys.get(0)
		assertTrue(key1.isPK)
		assertEquals(tabla, key1.owner)
		assertEquals(key1.constraintname, tabla.name + "_pk")
		assertEquals(key1.columns.size, 1)
		// Key 2
		val relationalschema.Key key2 = tabla.keys.get(1)
		assertFalse(key2.isPK)
		assertEquals(tabla, key2.owner)
		assertEquals(key2.constraintname, keys.get(1).name + "_ak")
		assertEquals(key2.columns.size, 2)
	}
	
	// R4
	@Test
	def void testReference2Fkey() {
		mapper.loadSchema(USCHEMA_4)
		mapper.uschema2relational()
		val entidad1 = mapper.uschema.entities.get(0)
		mapper.entityType2Table(entidad1)
		val entidad2 = mapper.uschema.entities.get(1)
		mapper.entityType2Table(entidad2)
		entidad1.features.forEach[feature |
			if (feature instanceof Attribute) {
				mapper.attribute2Column(feature as Attribute)
			}
		]
		entidad2.features.forEach[feature |
			if (feature instanceof Attribute) {
				mapper.attribute2Column(feature as Attribute)
			}
		]
		entidad1.features.forEach[feature |
			if (feature instanceof uschema.Key) {
				mapper.key2key(feature as uschema.Key)
			}
		]
		entidad2.features.forEach[feature |
			if (feature instanceof uschema.Key) {
				mapper.key2key(feature as uschema.Key)
			}
		]
		entidad1.features.forEach[feature |
			if (feature instanceof Reference) {
				mapper.reference2fkey(feature as Reference)
			}
		]
		entidad2.features.forEach[feature |
			if (feature instanceof Reference) {
				mapper.reference2fkey(feature as Reference)
			}
		]
		val tabla1 = mapper.relationalSchema.tables.get(0)
		val tabla2 = mapper.relationalSchema.tables.get(1)
		// Referencia 1
		val referencia1 = entidad1.features.get(0) as Reference	
		assertEquals(tabla1.fks.get(0).constraintname, mapper.entityType2Table(referencia1.refsTo).name + "_fk")
		assertEquals(tabla1.fks.get(0).owner, tabla1)
		assertEquals(tabla1.fks.get(0).refsTo, tabla2.keys.get(0))
		assertEquals(tabla1.fks.get(0).onDelete, ReferentialAction.NO_ACTION)
		assertEquals(tabla1.fks.get(0).onUpdate, ReferentialAction.CASCADE)
		assertEquals(2, tabla1.fks.get(0).columns.size)
		assertEquals(tabla1.fks.get(0).columns.get(0).name, "atributo2")
		assertEquals(tabla1.fks.get(0).columns.get(1).name, "atributo1")
		// Referencia 2
		val referencia2 = entidad2.features.get(0) as Reference	
		assertEquals(tabla2.fks.get(0).constraintname, mapper.entityType2Table(referencia2.refsTo).name + "_fk")
		assertEquals(tabla2.fks.get(0).owner, tabla2)
		assertEquals(tabla2.fks.get(0).refsTo, tabla1.keys.get(0))
		assertEquals(tabla2.fks.get(0).onDelete, ReferentialAction.NO_ACTION)
		assertEquals(tabla2.fks.get(0).onUpdate, ReferentialAction.CASCADE)
		assertEquals(1, tabla2.fks.get(0).columns.size)
		assertEquals(tabla2.fks.get(0).columns.get(0).name, "atributo1")
	}
	
	// R5
	@Test
	def void testAggregate2WeakTable() {
		mapper.loadSchema(USCHEMA_5)
		mapper.uschema2relational()
		val entidad1 = mapper.uschema.entities.get(0)
		val tabla1 = mapper.entityType2Table(entidad1)
		val entidad2 = mapper.uschema.entities.get(1)
		val tabla2 = mapper.entityType2Table(entidad2)
		entidad1.features.forEach[feature |
			if (feature instanceof Attribute) {
				mapper.attribute2Column(feature as Attribute)
			}
		]
		entidad1.features.forEach[feature |
			if (feature instanceof uschema.Key) {
				mapper.key2key(feature as uschema.Key)
			}
		]
		entidad1.features.forEach[feature |
			if (feature instanceof Aggregate) {
				mapper.aggregate2weakTable(feature as Aggregate)
			}
		]
		assertEquals(1, tabla2.fks.size)
		assertEquals(tabla2.fks.get(0).constraintname, tabla1.name + "_fk")
		assertEquals(tabla2.fks.get(0).refsTo, tabla1.keys.get(0))
		assertEquals(2, tabla2.fks.get(0).columns.size)
		assertEquals(tabla2.fks.get(0).columns.get(0), tabla1.keys.get(0).columns.get(0))
		assertEquals(tabla2.fks.get(0).columns.get(1), tabla1.keys.get(0).columns.get(1))
		assertEquals(tabla2.fks.get(0).onDelete, ReferentialAction.NO_ACTION)
		assertEquals(tabla2.fks.get(0).onUpdate, ReferentialAction.CASCADE)
		assertEquals(1, tabla2.keys.size)
		assertEquals(1, tabla2.columns.size)
		assertEquals(tabla2.keys.get(0).constraintname, tabla2.name + "_pk")
		assertTrue(tabla2.keys.get(0).isPK)
		assertEquals(tabla2.columns.get(0).name, "id")
		assertEquals(tabla2.columns.get(0).datatype, "NUMERIC")
		assertEquals(tabla2.columns.get(0).size, 38)
		assertFalse(tabla2.columns.get(0).nullable)
		assertEquals(tabla2.columns.get(0).defaultvalue, "1")
		assertEquals(3, tabla2.keys.get(0).columns.size)
	}
	
	// R6
	@Test
	def void testRelationshipType2Table() {
		mapper.loadSchema(USCHEMA_6)
		mapper.uschema2relational()
		mapper.entityType2Table(mapper.uschema.entities.get(0))
		mapper.entityType2Table(mapper.uschema.entities.get(1))
		mapper.uschema.entities.get(0).features.forEach[feature |
			if (feature instanceof Attribute) {
				mapper.attribute2Column(feature as Attribute)
			}
		]
		mapper.uschema.entities.get(1).features.forEach[feature |
			if (feature instanceof Attribute) {
				mapper.attribute2Column(feature as Attribute)
			}
		]
		mapper.uschema.entities.get(0).features.forEach[feature |
			if (feature instanceof uschema.Key) {
				mapper.key2key(feature as uschema.Key)
			}
		]
		mapper.uschema.entities.get(1).features.forEach[feature |
			if (feature instanceof uschema.Key) {
				mapper.key2key(feature as uschema.Key)
			}
		]
		mapper.uschema.entities.get(0).features.forEach[feature |
			if (feature instanceof Reference) {
				mapper.reference2fkey(feature as Reference)
			}
		]
		mapper.uschema.entities.get(1).features.forEach[feature |
			if (feature instanceof Reference) {
				mapper.reference2fkey(feature as Reference)
			}
		]
		mapper.relationshipType2Table(mapper.uschema.relationships.get(0))
		// compruebo que se han generado 3 tables
		assertEquals(3, mapper.relationalSchema.tables.size)
		// compruebo el nombre de la tabla generada por la relacion
		assertEquals("Comprar", mapper.relationalSchema.tables.get(2).name)
		// compruebo  que la tabla de la relacion tiene 2 columnas por los 2 atributos  de la relacion
		assertEquals(2, mapper.relationalSchema.tables.get(2).columns.size)
		// compruebo que se han generado 2 fks
		assertEquals(2, mapper.relationalSchema.tables.get(2).fks.size)
		// compuebro los nombres de las fks
		assertEquals(mapper.relationalSchema.tables.get(0).name + "_fk", mapper.relationalSchema.tables.get(2).fks.get(0).constraintname)
		assertEquals(mapper.relationalSchema.tables.get(1).name + "_fk", mapper.relationalSchema.tables.get(2).fks.get(1).constraintname)
		// compruebo la pk a la que referencia la fk
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(0).refsTo, mapper.relationalSchema.tables.get(0).keys.get(0))
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(1).refsTo, mapper.relationalSchema.tables.get(1).keys.get(0))
		// compruebo que la fk1 tiene las mismas columnas que pk.columns
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(0).columns.size, 2)
		// compruebo que dichas columnas coinciden
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(0).columns.get(0), mapper.relationalSchema.tables.get(0).keys.get(0).columns.get(0))
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(0).columns.get(1), mapper.relationalSchema.tables.get(0).keys.get(0).columns.get(1))
		// compruebo que la fk2 tiene las mismas columnas que pk.columns
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(1).columns.size, 2)
		// compruebo que dichas columnas coinciden
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(1).columns.get(0), mapper.relationalSchema.tables.get(1).keys.get(0).columns.get(0))
		assertEquals(mapper.relationalSchema.tables.get(2).fks.get(1).columns.get(1), mapper.relationalSchema.tables.get(1).keys.get(0).columns.get(1))
		// comprobamos la pk
		assertEquals(mapper.relationalSchema.tables.get(2).keys.get(0).constraintname, mapper.relationalSchema.tables.get(2).name + "_pk")
		assertTrue(mapper.relationalSchema.tables.get(2).keys.get(0).isPK)
		assertEquals(4, mapper.relationalSchema.tables.get(2).keys.get(0).columns.size)
	}
	
}