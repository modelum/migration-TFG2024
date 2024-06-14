package juanfran.um

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*
import documentschema.EntityType
import documentschema.Attribute
import documentschema.DataType
import documentschema.Aggregate
import documentschema.PrimitiveType
import documentschema.Reference
import documentschema.Type
import documentschema.Array

class MappingUschema2DocumentTest {
	
	static val USCHEMA_0 = "input-files/USchema_0.xmi"
	static val USCHEMA_1 = "input-files/USchema_1.xmi"
	static val USCHEMA_2 = "input-files/USchema_2.xmi"
	static val USCHEMA_3 = "input-files/USchema_3.xmi"
	static val USCHEMA_4_1 = "input-files/USchema_4-1.xmi"
	static val USCHEMA_4_2 = "input-files/USchema_4-2.xmi"
	static val USCHEMA_5 = "input-files/USchema_5.xmi"
	static val USCHEMA_6 = "input-files/USchema_6.xmi"
	static val USCHEMA_7 = "input-files/USchema_7.xmi"
	static val USCHEMA_INTEGRATION = "input-files/USchema_integration.xmi"
	
	val MappingUschema2Document mapping = new MappingUschema2Document;
	
	// R0: USchema to DocumentSchema
	@Test
	def void uSchema2DocumentSchema_OK() {
		mapping.loadSchema(USCHEMA_0)
		
		// R0
		mapping.uSchema2DocumentSchema()
		assertNotNull(mapping.documentSchema)
		assertEquals(mapping.USchema.name, mapping.documentSchema.name)
	}
	
	// R1: uschema.EntityType to EntityType
	@Test
	def void entityType2EntityType_OK() {
		mapping.loadSchema(USCHEMA_1)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		entityType2EntityType_asserts(e)
	}
	
	// R2: uschema.Attribute to Attribute from EntityType
	@Test
	def void attribute2Attribute_OK() {
		mapping.loadSchema(USCHEMA_2)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		entityType2EntityType_asserts(e)
		
		// R2
		val EntityType d = mapping.documentSchema.entities.head
		val uschema.Attribute f_at = e.features.head as uschema.Attribute
		attribute2Attribute_asserts(f_at, d)
	}
	
	// R3: uschema.Aggregate to Aggregate from EntityType
	@Test
	def void aggregate2Aggregate_OK() {
		mapping.loadSchema(USCHEMA_3)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.findFirst[ et | et.root ]
		mapping.entityType2EntityType(e)
		
		// R3
		val EntityType d = mapping.documentSchema.entities.findFirst[ et | et.name == "Usuario" ]
		val uschema.Aggregate f_ag = e.features.head as uschema.Aggregate
		aggregate2Aggregate_asserts(f_ag, d)
	}
	
	// R4: Key (simple) to Attribute from EntityType
	@Test
	def void key2Attribute_Simple_OK() {
		mapping.loadSchema(USCHEMA_4_1)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		entityType2EntityType_asserts(e)
		
		// R2, R4 (simple)
		val EntityType d = mapping.documentSchema.entities.head
		val uschema.Attribute f_at = e.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		val int numProperties1 = d.properties.size
		
		// In this method the Attribute (simple Key) is created
		mapping.attribute2Attribute(f_at, d)
		
		val Attribute key_at = d.properties.head as Attribute
		val int numProperties2 = d.properties.size
		
		assertNotNull(key_at)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(f_at.name+"_id", key_at.name)
		assertTrue(key_at.isIsKey)
	}
	
	// R4: Key (composite) to Attribute from EntityType
	@Test
	def void key2Attribute_Composite_OK() {
		mapping.loadSchema(USCHEMA_4_2)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		entityType2EntityType_asserts(e)
		
		// R2
		val EntityType d = mapping.documentSchema.entities.head
		for (f : e.features) {
			switch f {
				uschema.Attribute: attribute2Attribute_asserts(f, d)
			}
		}
		
		// R4 (composite)
		val uschema.Key f_key = e.features.findFirst[ f | f.name == "Libro_PK" ] as uschema.Key
		key2Attribute_asserts(f_key, d)
	}
	
	// R5: uschema.Reference to Reference from EntityType
	@Test
	def void reference2Reference_OK() {
		mapping.loadSchema(USCHEMA_5)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e1 = mapping.USchema.entities.findFirst[ et | et.name == "Libro" ]
		val uschema.EntityType e2 = mapping.USchema.entities.findFirst[ et | et.name == "Editor" ]
		mapping.entityType2EntityType(e1)
		mapping.entityType2EntityType(e2)
		mapping.USchema.entities.forEach[ e | entityType2EntityType_asserts(e) ]
		
		// R2
		val EntityType d1 = mapping.documentSchema.entities.findFirst[ et | et.name == "Libro" ]
		val EntityType d2 = mapping.documentSchema.entities.findFirst[ et | et.name == "Editor" ]
		val uschema.Attribute f_at1 = e1.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		val uschema.Attribute f_at2 = e2.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		attribute2Attribute_asserts(f_at1, d1)
		attribute2Attribute_asserts(f_at2, d2)
		
		// R4
		val uschema.Key f_key1 = e1.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		val uschema.Key f_key2 = e2.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		key2Attribute_asserts(f_key1, d1)
		key2Attribute_asserts(f_key2, d2)
		
		// R5
		val uschema.Reference f_ref = e1.features.findFirst[ f | f instanceof uschema.Reference ] as uschema.Reference
		reference2Reference_asserts(f_ref, d1)
	}
	
	// R6: RelationshipType to EntityType
	@Test
	def void relationshipType2EntityType_OK() {
		mapping.loadSchema(USCHEMA_6)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e1 = mapping.USchema.entities.findFirst[ et | et.name == "Autor" ]
		val uschema.EntityType e2 = mapping.USchema.entities.findFirst[ et | et.name == "Libro" ]
		entityType2EntityType_asserts(e1)
		entityType2EntityType_asserts(e2)
		
		// R2
		val EntityType d1 = mapping.documentSchema.entities.findFirst[ et | et.name == "Autor" ]
		val EntityType d2 = mapping.documentSchema.entities.findFirst[ et | et.name == "Libro" ]
		val uschema.Attribute f_at1 = e1.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		val uschema.Attribute f_at2 = e2.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		attribute2Attribute_asserts(f_at1, d1)
		attribute2Attribute_asserts(f_at2, d2)
		
		// R4
		val uschema.Key f_key1 = e1.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		val uschema.Key f_key2 = e2.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		key2Attribute_asserts(f_key1, d1)
		key2Attribute_asserts(f_key2, d2)
		
		// R5
		val uschema.Reference f_ref1 = e1.features.findFirst[ f | f instanceof uschema.Reference ] as uschema.Reference
		val uschema.Reference f_ref2 = e2.features.findFirst[ f | f instanceof uschema.Reference ] as uschema.Reference
		reference2Reference_asserts(f_ref1, d1)
		reference2Reference_asserts(f_ref2, d2)
		
		// R6
		val uschema.RelationshipType rt = mapping.USchema.relationships.head
		relationshipType2EntityType_asserts(rt) 
	}
	
	// R7: Datatype to Type
	@Test
	def void datatype2Type_OK() {
		mapping.loadSchema(USCHEMA_7)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		entityType2EntityType_asserts(e)
		
		// R7
		val uschema.PrimitiveType f_primitive = (e.features.findFirst[ f | f.name == "AutorID" ] as uschema.Attribute).type as uschema.PrimitiveType
		val uschema.PTuple f_tuple = (e.features.findFirst[ f | f.name == "NombreCompleto" ] as uschema.Attribute).type as uschema.PTuple
		val uschema.PSet f_set = (e.features.findFirst[ f | f.name == "Géneros" ] as uschema.Attribute).type as uschema.PSet
		val uschema.PList f_list = (e.features.findFirst[ f | f.name == "Libros" ] as uschema.Attribute).type as uschema.PList
		val uschema.PMap f_map = (e.features.findFirst[ f | f.name == "Premios" ] as uschema.Attribute).type as uschema.PMap
		
		val Type p_primitive = mapping.datatype2Type(f_primitive)
		val Type p_tuple = mapping.datatype2Type(f_tuple)
		val Type p_set = mapping.datatype2Type(f_set)
		val Type p_list = mapping.datatype2Type(f_list)
		val Type p_map = mapping.datatype2Type(f_map)
		
		assertTrue(p_primitive instanceof PrimitiveType)
		assertTrue(p_tuple instanceof Array)
		assertTrue(p_set instanceof Array)
		assertTrue(p_list instanceof Array)
		assertTrue(p_map instanceof Array)
		assertEquals(DataType::INTEGER, (p_primitive as PrimitiveType).datatype)
		assertEquals(DataType::STRING, (p_tuple as Array).type.datatype)
		assertEquals(DataType::STRING, (p_set as Array).type.datatype)
		assertEquals(DataType::STRING, (p_list as Array).type.datatype)
		assertEquals(DataType::STRING, (p_map as Array).type.datatype)
	}
	
	// Integration test
	@Test
	def void uSchema2DocumentSchema_integration_OK() {
		mapping.loadSchema(USCHEMA_INTEGRATION)
		
		mapping.uSchema2DocumentSchema() // r0
		
		for (e : mapping.USchema.entities) {
			if (e.root)
				entityType2EntityType_asserts(e) // r1
		}
		
		for (d : mapping.documentSchema.entities) {
			val uschema.EntityType uet = mapping.USchema.entities.findFirst[ e | e.name == d.name ] as uschema.EntityType
		
			for (f : uet.features) {
				switch f {
					uschema.Attribute: attribute2Attribute_asserts(f, d) // r2
				}
				
			}
			
			for (f: uet.features) {
				switch f {
					uschema.Key: { 
						if (f.isID)
							key2Attribute_asserts(f, d) // r4
					}
				}
			}
		}
		
		for (d : mapping.documentSchema.entities) {
			val uschema.EntityType uet = mapping.USchema.entities.findFirst[ e | e.name == d.name ] as uschema.EntityType
			
			for (f: uet.features) {
				switch f {
					uschema.Aggregate: aggregate2Aggregate_asserts(f, d) // r3
					uschema.Reference: reference2Reference_asserts(f, d) // r5
				}
			}
		}
		
		for (rt : mapping.USchema.relationships) {
			relationshipType2EntityType_asserts(rt) // r6
		}
	}
	
	
	
	//----- PRIVATE METHODS -----//
	
	// R1 with asserts
	private def void entityType2EntityType_asserts(uschema.EntityType e) {
		val int numEntities1 = mapping.documentSchema.entities.size
		
		mapping.entityType2EntityType(e)
		
		val EntityType d = mapping.documentSchema.entities.findFirst[ et | et.name == e.name ]
		val int numEntities2 = mapping.documentSchema.entities.size
		
		assertNotNull(d)
		assertEquals(numEntities1+1, numEntities2)
	}
	
	// R2 with asserts
	private def void attribute2Attribute_asserts(uschema.Attribute f_at, EntityType d) {
		val int numProperties1 = d.properties.size
		
		mapping.attribute2Attribute(f_at, d)
		
		var Attribute p_at
		if (f_at.key !== null && f_at.key.attributes.size == 1 && f_at.key.isID) {
			p_at = d.properties.findFirst[ f | f.name == f_at.name+"_id" ] as Attribute
		} else {
			p_at = p_at = d.properties.findFirst[ f | f.name == f_at.name ] as Attribute
		}
		
		val int numProperties2 = d.properties.size

		assertNotNull(p_at)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(mapping.datatype2Type(f_at.type), p_at.type)
	}
	
	// R3 with asserts
	private def void aggregate2Aggregate_asserts(uschema.Aggregate f_ag, EntityType d) {
		val uschema.EntityType w = f_ag.specifiedBy as uschema.EntityType
		val int numProperties1 = d.properties.size
		
		mapping.aggregate2Aggregate(f_ag, d)
		
		val Aggregate p_ag = d.properties.findFirst[ p | p.name == f_ag.name ] as Aggregate
		val int numProperties2 = d.properties.size 
		
		assertNotNull(p_ag)
		assertEquals(numProperties1+1, numProperties2)
		assertTrue(p_ag.isIsMany)
		
		// Check Attributes are correctly added into Aggregate
		for (f : w.features) {
			switch f {
				uschema.Attribute: {
					var Attribute p_at
					if (f.key !== null && f.key.attributes.size == 1 && f.key.isID) {
						p_at = p_ag.aggregates.findFirst[ at | at.name == f.name+"_id" ] as Attribute
					} else {
						p_at = p_ag.aggregates.findFirst[ at | at.name == f.name ] as Attribute
					}
					assertNotNull(p_at)
					assertEquals(1, p_ag.aggregates.size)
					assertEquals(mapping.datatype2Type(f.type), p_at.type)
				}
			}
		}
	}
	
	// R4 (composite) with asserts
	private def void key2Attribute_asserts(uschema.Key f_key, EntityType d) {
		if (f_key.attributes.size <= 1)
			return
			
		val int numProperties1 = d.properties.size
		
		mapping.key2Attribute(f_key, d)
		
		val Attribute p_at = d.properties.findFirst[ p | p.name == d.name+"_id" ] as Attribute
		val int numProperties2 = d.properties.size
		
		assertNotNull(p_at)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(DataType::STRING, (p_at.type as PrimitiveType).datatype)
		assertTrue(p_at.isIsKey)
	}
	
	// R5 with asserts
	private def void reference2Reference_asserts(uschema.Reference f_ref, EntityType d) {
		if (f_ref.isFeaturedBy !== null)
			return
			
		val int numProperties1 = d.properties.size
		
		mapping.reference2Reference(f_ref, d)
		
		val Reference p_ref = d.properties.findFirst[ p | p.name == f_ref.name ] as Reference
		val EntityType d_target = mapping.documentSchema.entities.findFirst[ e | e.name == f_ref.refsTo.name ]
		val int numProperties2 = d.properties.size
		
		assertNotNull(p_ref)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(d_target, p_ref.target)

		// Type check
		var Type t
		if (f_ref.upperBound == 1) {
			t = mapping.findAttributeKey(d_target).type
		} else if (f_ref.upperBound == -1 || f_ref.upperBound > 1) {
			val PrimitiveType pt = mapping.findAttributeKey(d_target).type as PrimitiveType
			t = mapping.docArrayTypes.get(pt.datatype)
		}
		assertEquals(t, p_ref.type)
	}
	
	// R6 with asserts
	private def void relationshipType2EntityType_asserts(uschema.RelationshipType rt) {
		mapping.relationshipType2EntityType(rt)
		
		val EntityType c = mapping.documentSchema.entities.findFirst[ et | et.name == "Libros_Autores" ]
		assertNotNull(c)
		assertEquals(rt.name, c.name)
		
		// Attributes into RelationshipType.features
		for (f : rt.features) {
			val uschema.Attribute rt_at = f as uschema.Attribute
			val Attribute c_at = c.properties.findFirst[ p | p.name == rt_at.name ] as Attribute
			assertNotNull(c_at)
			assertEquals(mapping.datatype2Type(rt_at.type), c_at.type)
		}
		
		// Attribute ID
		val Attribute at = c.properties.findFirst[ p | p.name == c.name+"_id" ] as Attribute
		assertNotNull(at)
		assertTrue(at.isIsKey)
		assertEquals(DataType::STRING, (at.type as PrimitiveType).datatype) // In this case is STRING
		
		// References
		val uschema.Reference u_r1 = rt.reference.get(0)
		val uschema.Reference u_r2 = rt.reference.get(1)
		val EntityType p_d1 = mapping.documentSchema.entities.findFirst[ e | e.name == u_r1.owner.name ]
		val EntityType p_d2 = mapping.documentSchema.entities.findFirst[ e | e.name == u_r2.owner.name ]
		val Reference p_ref1 = c.properties.findFirst[ p | p.name == u_r1.name ] as Reference
		val Reference p_ref2 = c.properties.findFirst[ p | p.name == u_r2.name ] as Reference
		assertNotNull(p_ref1)
		assertNotNull(p_ref2)
		assertEquals(p_d1, p_ref2.target)
		assertEquals(p_d2, p_ref1.target)
		assertEquals(mapping.findAttributeKey(p_ref1.target).type, p_ref1.type)
		assertEquals(mapping.findAttributeKey(p_ref2.target).type, p_ref2.type)
		
		assertEquals(rt.features.size + 3, c.properties.size)
	}
}