package es.um.dsdm.document2uschema.test

import static org.junit.Assert.*
import es.um.dsdm.document2uschema.MappingDocument2Uschema
import org.junit.Before
import org.junit.Test

class MappingDocument2UschemaTest {
	
	static val DOCUMENTSCHEMA_0 = "inputs/DocumentSchema-Regla0.xmi"
	static val DOCUMENTSCHEMA_1 = "inputs/DocumentSchema-Regla1.xmi"
	static val DOCUMENTSCHEMA_2 = "inputs/DocumentSchema-Regla2.xmi"
	static val DOCUMENTSCHEMA_3 = "inputs/DocumentSchema-Regla3.xmi"
	static val DOCUMENTSCHEMA_4 = "inputs/DocumentSchema-Regla4.xmi"
	static val DOCUMENTSCHEMA = "inputs/DocumentSchema.xmi"
	
	MappingDocument2Uschema mapper
	
	
	@Before
	def void setUp() {
		mapper = new MappingDocument2Uschema
	}
	
	// R0
	@Test
	def void testDocumentSchema2USchema() {
		mapper.loadSchema(DOCUMENTSCHEMA_0)
		mapper.documentSchema2USchema()
		assertNotNull(mapper.USchema)
		assertEquals(mapper.USchema.name, mapper.documentSchema.name)
	}
	
	// R1
	@Test
	def void testEntityType2entityType() {
		mapper.loadSchema(DOCUMENTSCHEMA_1)
		mapper.documentSchema2USchema()
		val entidad1 = mapper.documentSchema.entities.get(0)
		val entidad2 = mapper.documentSchema.entities.get(1)
		val entidad1Mapeada = mapper.entityType2entityType(entidad1)
		assertEquals(entidad1.name, entidad1Mapeada.name)
		val entidad2Mapeada = mapper.entityType2entityType(entidad2)
		assertEquals(entidad2.name, entidad2Mapeada.name)
		assertEquals(2, mapper.USchema.entities.size)
	}
	
	// R2
	@Test
	def void testAttribute2Attribute() {
		mapper.loadSchema(DOCUMENTSCHEMA_2)
		mapper.documentSchema2USchema()
		val entidad1 = mapper.documentSchema.entities.get(0)
		val entidad2 = mapper.documentSchema.entities.get(1)
		val entidad1Mapeada = mapper.entityType2entityType(entidad1)
		val entidad2Mapeada = mapper.entityType2entityType(entidad2)
		entidad1.properties.forEach[property |
			if (property instanceof documentschema.Attribute) {
				mapper.attribute2attribute(property as documentschema.Attribute)
			}
		]
		entidad2.properties.forEach[property |
			if (property instanceof documentschema.Attribute) {
				mapper.attribute2attribute(property as documentschema.Attribute)
			}
		]
		val atributo1uschema = mapper.USchema.entities.get(0).features.get(0) as uschema.Attribute
		val atributo1documentschema = mapper.documentSchema.entities.get(0).properties.get(0) as documentschema.Attribute
		assertEquals(atributo1uschema.name, atributo1documentschema.name)
		//assertEquals(atributo1uschema.type., mapper.type2datatype(atributo1documentschema.type))
		assertEquals(mapper.USchema.entities.get(0).features.size, 4)
		assertEquals(mapper.USchema.entities.get(1).features.size, 3)
		val key = mapper.USchema.entities.get(0).features.get(2) as uschema.Key
		assertTrue(key.isID)
		assertEquals(key.name, mapper.documentSchema.entities.get(0).properties.get(2).name)
	}
	
	// R3
	@Test
	def void testReference2Reference() {
		mapper.loadSchema(DOCUMENTSCHEMA_3)
		mapper.documentSchema2USchema()
		val entidad1 = mapper.documentSchema.entities.get(0)
		val entidad1Mapeada = mapper.entityType2entityType(entidad1)


		val entidad2 = mapper.documentSchema.entities.get(1)
		val entidad2Mapeada = mapper.entityType2entityType(entidad2)
		val documentschema.Reference referencia1 = entidad1.properties.get(0) as documentschema.Reference
		mapper.reference2reference(referencia1)
		val documentschema.Reference referencia2 = entidad1.properties.get(1) as documentschema.Reference
		mapper.reference2reference(referencia2)
		val documentschema.Reference referencia3 = entidad2.properties.head as documentschema.Reference
		mapper.reference2reference(referencia3)
		
		val uschema.Reference referencia1Mapeada = entidad1Mapeada.features.get(0) as uschema.Reference
		val uschema.Attribute atributo1Mapeado = entidad1Mapeada.features.get(1) as uschema.Attribute
		
		val uschema.Reference referencia2Mapeada = entidad1Mapeada.features.get(2) as uschema.Reference
		val uschema.Attribute atributo2Mapeado = entidad1Mapeada.features.get(3) as uschema.Attribute
		
		val uschema.Reference referencia3Mapeada = entidad2Mapeada.features.get(0) as uschema.Reference
		val uschema.Attribute atributo3Mapeado = entidad2Mapeada.features.get(1) as uschema.Attribute
		// comprobamos los nombres
		// el nombre coincide con dos features ya que se crean una referencia y un atributo
		assertEquals(referencia1.name, referencia1Mapeada.name)
		assertEquals(referencia1.name, atributo1Mapeado.name)
		assertEquals(referencia2.name, referencia2Mapeada.name)
		assertEquals(referencia2.name, atributo2Mapeado.name)
		assertEquals(referencia3.name, referencia3Mapeada.name)
		assertEquals(referencia3.name, atributo3Mapeado.name)
		// comprobamos los bounds
		// limite inferior
		assertEquals(1, referencia1Mapeada.lowerBound)
		assertEquals(1, referencia2Mapeada.lowerBound)
		assertEquals(1, referencia3Mapeada.lowerBound)
		// limite superior
		assertEquals(1, referencia1Mapeada.upperBound)
		assertEquals(-1, referencia2Mapeada.upperBound)
		assertEquals(1, referencia3Mapeada.upperBound)
		// comprobamos la referencia
		assertEquals(referencia1Mapeada.refsTo, mapper.entityType2entityType(referencia1.target))
		assertEquals(referencia2Mapeada.refsTo, mapper.entityType2entityType(referencia2.target))
		assertEquals(referencia3Mapeada.refsTo, mapper.entityType2entityType(referencia3.target))
	}
	
	// R4
	@Test
	def void testAggregate2Aggregate() {
		mapper.loadSchema(DOCUMENTSCHEMA_4)
		mapper.documentSchema2USchema()
		mapper.documentSchema.entities.forEach[entidad |
			mapper.entityType2entityType(entidad)
		]
		mapper.documentSchema.entities.forEach[entidad |
			entidad.properties.forEach[atributo |
				if (atributo instanceof documentschema.Attribute)
					mapper.attribute2attribute(atributo as documentschema.Attribute)
			]
		]		
		mapper.documentSchema.entities.forEach[entidad |
			entidad.properties.forEach[reference |
				if (reference instanceof documentschema.Reference)
					mapper.reference2reference(reference as documentschema.Reference)
			]
		]
		assertEquals(mapper.getUSchema.entities.size, 2)
		//assertEquals(mapper.USchema.entities.get(0).features.size, 3)
		mapper.documentSchema.entities.forEach[entidad |
			entidad.properties.forEach[agregado |
				if (agregado instanceof documentschema.Aggregate) {
					mapper.aggregate2entityType(agregado as documentschema.Aggregate)
				}
			]
		]
		assertEquals(mapper.getUSchema.entities.size, 3)
		//assertEquals(mapper.USchema.entities.get(0).features.size, 2)
		
		val agregadoDocument = mapper.documentSchema.entities.get(0).properties.get(0) as documentschema.Aggregate
		assertEquals(mapper.USchema.entities.get(2).name, agregadoDocument.name)
		assertEquals(mapper.USchema.entities.get(2).features.size, 1)
		assertFalse(mapper.USchema.entities.get(2).root)
	}
	
	@Test
	def void testTransformation() {
		mapper.loadSchema(DOCUMENTSCHEMA)
		mapper.transformacion()
		val documentschema = mapper.documentSchema
		val uschema = mapper.USchema
		assertNotNull(uschema)
		assertEquals(uschema.entities.size, documentschema.entities.size + 1)
		
		val userEntity = uschema.entities.findFirst[e | e.name == "User"]
		assertNotNull(userEntity)
		
		val orderEntity = uschema.entities.findFirst[e | e.name == "Order"]
		assertNotNull(orderEntity)
		
		val productEntity = uschema.entities.findFirst[e | e.name == "Product"]
		assertNotNull(productEntity)
		
		val orderDetailsEntity = uschema.entities.findFirst[e | e.name == "OrderDetails"]
		assertNotNull(orderDetailsEntity)
		
		val shopEntity = uschema.entities.findFirst[e | e.name == "Shop"]
		assertNotNull(shopEntity)
		
		val aggregateEntity = uschema.entities.findFirst[e | e.name == "producto"]
		assertNotNull(aggregateEntity)
	}
	
}