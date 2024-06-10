package juanfran.um

import com.google.common.collect.Table
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*
import documentschema.EntityType
import documentschema.Attribute
import documentschema.DataType
import documentschema.Aggregate
import documentschema.PrimitiveType
import uschema.Key
import documentschema.Reference
import documentschema.Type
import documentschema.Array

class MappingUschema2DocumentTest {
	
	static val USCHEMA_0 = "test-input-files/USchema_0.xmi"
	static val USCHEMA_1 = "test-input-files/USchema_1.xmi"
	static val USCHEMA_2 = "test-input-files/USchema_2.xmi"
	static val USCHEMA_3 = "test-input-files/USchema_3.xmi"
	static val USCHEMA_4_1 = "test-input-files/USchema_4-1.xmi"
	static val USCHEMA_4_2 = "test-input-files/USchema_4-2.xmi"
	static val USCHEMA_5 = "test-input-files/USchema_5.xmi"
	static val USCHEMA_6 = "test-input-files/USchema_6.xmi"
	static val USCHEMA_7 = "test-input-files/USchema_7.xmi"
	
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
		val int numEntities1 = mapping.documentSchema.entities.size
		
		mapping.entityType2EntityType(e)
		
		val EntityType d = mapping.documentSchema.entities.head 
		val int numEntities2 = mapping.documentSchema.entities.size
		
		assertNotNull(d)
		assertEquals(numEntities1+1, numEntities2)
		assertEquals(e.name, d.name)
	}
	
	// R2: uschema.Attribute to Attribute from EntityType
	@Test
	def void attribute2Attribute_OK() {
		mapping.loadSchema(USCHEMA_2)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		mapping.entityType2EntityType(e)
		
		// R2
		val EntityType d = mapping.documentSchema.entities.head
		val uschema.Attribute f_at = e.features.head as uschema.Attribute
		val int numProperties1 = d.properties.size
		
		mapping.attribute2Attribute(f_at, d)
		
		val Attribute p_at = d.properties.head as Attribute
		val int numProperties2 = d.properties.size
		
		assertNotNull(p_at)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(f_at.name, p_at.name)
		assertEquals(DataType::INTEGER, (p_at.type as PrimitiveType).datatype)
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
		val uschema.EntityType w = f_ag.specifiedBy as uschema.EntityType
		val uschema.Attribute f_at = w.features.head as uschema.Attribute
		val int numProperties1 = d.properties.size
		
		mapping.aggregate2Aggregate(f_ag, d)
		
		val Aggregate p_ag = d.properties.head as Aggregate
		val int numProperties2 = d.properties.size 
		
		assertNotNull(p_ag)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(f_ag.name, p_ag.name)
		assertTrue(p_ag.isIsMany)
		
		// Check Attribute is correctly added into Aggregate
		val Attribute p_at = p_ag.aggregates.head as Attribute
		
		assertNotNull(p_at)
		assertEquals(1, p_ag.aggregates.size)
		assertEquals(f_at.name, p_at.name)
		assertEquals(DataType::INTEGER, (p_at.type as PrimitiveType).datatype)
	}
	
	// R4: Key (simple) to Attribute from EntityType
	@Test
	def void key2Attribute_Simple_OK() {
		mapping.loadSchema(USCHEMA_4_1)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		mapping.entityType2EntityType(e)
		
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
		mapping.entityType2EntityType(e)
		
		// R2
		val EntityType d = mapping.documentSchema.entities.head
		for (f : e.features) {
			switch f {
				uschema.Attribute: mapping.attribute2Attribute(f, d)
			}
			
		}
		
		// R4 (composite)
		val uschema.Key f_key = e.features.findFirst[ f | f.name == "Libro_PK" ] as uschema.Key
		val int numProperties1 = d.properties.size
		
		mapping.key2Attribute(f_key, d)
		
		val Attribute p_at = d.properties.findFirst[ p | p.name == "Libro_id" ] as Attribute
		val int numProperties2 = d.properties.size
		
		assertNotNull(p_at)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(d.name+"_id", p_at.name)
		assertEquals(DataType::STRING, (p_at.type as PrimitiveType).datatype)
		assertTrue(p_at.isIsKey)
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
		
		// R2
		val EntityType d1 = mapping.documentSchema.entities.findFirst[ et | et.name == "Libro" ]
		val EntityType d2 = mapping.documentSchema.entities.findFirst[ et | et.name == "Editor" ]
		val uschema.Attribute f_at1 = e1.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		val uschema.Attribute f_at2 = e2.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		mapping.attribute2Attribute(f_at1, d1)
		mapping.attribute2Attribute(f_at2, d2)
		
		// R4
		val uschema.Key f_key1 = e1.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		val uschema.Key f_key2 = e2.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		mapping.key2Attribute(f_key1, d1)
		mapping.key2Attribute(f_key2, d2)
		
		// R5
		val uschema.Reference f_ref = e1.features.findFirst[ f | f instanceof uschema.Reference ] as uschema.Reference
		val int numProperties1 = d1.properties.size
		
		mapping.reference2Reference(f_ref, d1)
		
		val Reference p_ref = d1.properties.findFirst[ p | p instanceof Reference ] as Reference
		val int numProperties2 = d1.properties.size
		
		assertNotNull(p_ref)
		assertEquals(numProperties1+1, numProperties2)
		assertEquals(f_ref.name, p_ref.name)
		assertEquals(d2, p_ref.target)
		assertEquals(DataType::STRING, (p_ref.type as PrimitiveType).datatype)
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
		mapping.entityType2EntityType(e1)
		mapping.entityType2EntityType(e2)
		
		// R2
		val EntityType d1 = mapping.documentSchema.entities.findFirst[ et | et.name == "Autor" ]
		val EntityType d2 = mapping.documentSchema.entities.findFirst[ et | et.name == "Libro" ]
		val uschema.Attribute f_at1 = e1.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		val uschema.Attribute f_at2 = e2.features.findFirst[ f | f instanceof uschema.Attribute ] as uschema.Attribute
		mapping.attribute2Attribute(f_at1, d1)
		mapping.attribute2Attribute(f_at2, d2)
		
		// R4
		val uschema.Key f_key1 = e1.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		val uschema.Key f_key2 = e2.features.findFirst[ f | f instanceof uschema.Key ] as uschema.Key
		mapping.key2Attribute(f_key1, d1)
		mapping.key2Attribute(f_key2, d2)
		
		// R5
		val uschema.Reference f_ref1 = e1.features.findFirst[ f | f instanceof uschema.Reference ] as uschema.Reference
		val uschema.Reference f_ref2 = e2.features.findFirst[ f | f instanceof uschema.Reference ] as uschema.Reference
		mapping.reference2Reference(f_ref1, d1)
		mapping.reference2Reference(f_ref2, d2)
		assertNull(d1.properties.findFirst[ p | p instanceof Reference ] as Reference)
		assertNull(d2.properties.findFirst[ p | p instanceof Reference ] as Reference)
		
		// R6
		val uschema.RelationshipType rt = mapping.USchema.relationships.head
		mapping.relationshipType2EntityType(rt)
		val EntityType c = mapping.documentSchema.entities.findFirst[ et | et.name == "Libros_Autores" ]
		assertNotNull(c)
		assertEquals(rt.name, c.name)
		
		// Attribute into RelationshipType.features
		val uschema.Attribute rt_at = rt.features.head as uschema.Attribute
		val Attribute c_at = c.properties.findFirst[ p | p.name == "Editorial" ] as Attribute
		assertNotNull(c_at)
		assertEquals(rt_at.name, c_at.name)
		assertEquals(DataType::STRING, (c_at.type as PrimitiveType).datatype)
		
		// Attribute ID
		val Attribute at = c.properties.findFirst[ p | p.name == "Libros_Autores_id" ] as Attribute
		assertNotNull(at)
		assertEquals(c.name+"_id", at.name)
		assertTrue(at.isIsKey)
		assertEquals(DataType::STRING, (at.type as PrimitiveType).datatype) // In this case is STRING
		
		// References
		val Reference p_ref1 = c.properties.findFirst[ p | p.name == "Libro_FK" ] as Reference
		val Reference p_ref2 = c.properties.findFirst[ p | p.name == "Autor_FK" ] as Reference
		assertNotNull(p_ref1)
		assertNotNull(p_ref2)
		assertEquals(d1, p_ref2.target)
		assertEquals(d2, p_ref1.target)
		assertEquals(DataType::STRING, (p_ref1.type as PrimitiveType).datatype)
		assertEquals(DataType::INTEGER, (p_ref2.type as PrimitiveType).datatype)
		
		//TODO si añaden algo después de la modificación de R6, hay que actualizar el test
		
		assertEquals(4, c.properties.size)
	}
	
	// R7: Datatype to Type
	@Test
	def void datatype2Type_OK() {
		mapping.loadSchema(USCHEMA_7)
		
		// R0
		mapping.uSchema2DocumentSchema
		
		// R1
		val uschema.EntityType e = mapping.USchema.entities.head
		mapping.entityType2EntityType(e)
		
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
}