package juanfran.um

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import relationalschema.RelationalschemaPackage
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.common.util.URI
import relationalschema.RelationalSchema
import uschema.UschemaPackage
import uschema.USchema
import uschema.UschemaFactory
import uschema.EntityType
import uschema.RelationshipType
import uschema.Attribute
import uschema.PrimitiveType
import relationalschema.Table
import uschema.Key
import uschema.SchemaType
import uschema.Aggregate

class Transformation {
	
	static val INPUT_XMI_PATH = "modelos/RelationalTienda.xmi"
	static val OUTPUT_XMI_PATH = "modelos/USchemaTienda.xmi"
	
	// Types conversion from Relational to USchema
	def static String typeConversionRelToUsc(String dataType) {
		switch dataType {
		 	case "VARCHAR": return "String"
		 	case "INT": return "int"
		 	case "DOUBLE": return "double"
		 	case "BOOLEAN": return "boolean"
 		}
	}
	
	// Weak condition: returns Strong table
	def static Table weakCondition(Table table) {
		val relationalschema.Key pk = table.keys.findFirst[ key | key.isIsPK == true ]
		if (pk === null) // TODO ¿qué hacer en caso de que no exista clave primaria?
			return null
		
		for (fk : table.fks) {
			if (pk.columns.containsAll(fk.columns))
				return fk.refsTo.owner
		}
		
		return null
	}
	
	def static EntityType findEntityType(Table table, USchema uSchema) {
		return uSchema.entities.findFirst[ et | et.name == table.name ]
	}
	
	// R0: RelationalSchema -> USchema
	def static r0(Resource relResource, Resource uscResource) {
		println('Starting R0...')
		
		// Get schemas
		var RelationalSchema relationalSchema = relResource.contents.head as RelationalSchema
		var USchema uSchema = UschemaFactory.eINSTANCE.createUSchema
		
		// Apply rule
		uSchema.name = relationalSchema.name
		
		// Add to resource
		uscResource.contents.add(uSchema)
	}
	
	// R2: Column -> Attribute
	def static r2(Table table, SchemaType schemaType) {
		println('Starting R2...')
		
		for (column : table.columns) {
			var Attribute attribute = UschemaFactory.eINSTANCE.createAttribute
			attribute.name = column.name
			
			var PrimitiveType primitiveType = UschemaFactory.eINSTANCE.createPrimitiveType
			primitiveType.name = typeConversionRelToUsc(column.datatype)
			attribute.type = primitiveType
			
			attribute.optional = column.nullable
			
			schemaType.features.add(attribute);
		}
	}
	
	// R3: PK -> Key | UK -> Key
	def static r3(Table table, SchemaType schemaType) {
		println('Starting R3...')
		
		for (rKey : table.keys) {
			var Key uKey = UschemaFactory.eINSTANCE.createKey

			uKey.name = rKey.constraintname
			
			uKey.isID = rKey.isIsPK
			
			// Asociar atributos con la clave uKey: ek.attributes <- uk.columns
			val attributes = schemaType.features.filter[ f | f instanceof Attribute ]
			for (column : rKey.columns) {
				val Attribute attribute = attributes.findFirst[feature | feature.name == column.name] as Attribute
    			if (attribute !== null) {
    				uKey.attributes.add(attribute)
    			}
			}
			
			schemaType.features.add(uKey)
		}
		
		print('final features: [')
		schemaType.features.forEach[ c | print(c.name + ", ")]
		println(']')
	}
	
	// R4: Weak Table -> Aggregate
	def static r4(Table weakTable, Table strongTable, EntityType weakEntityType, USchema uSchema) {
		var Aggregate aggregate = UschemaFactory.eINSTANCE.createAggregate
		
		aggregate.name = weakTable.name + "s"
		
		aggregate.lowerBound = 0
		aggregate.upperBound = 3 //TODO "n" qué significa aquí?
		
		aggregate.aggregates = weakEntityType
		
		
		var EntityType strongEntityType = findEntityType(strongTable, uSchema)
		strongEntityType.features.add(aggregate)
		
		weakEntityType.root = false
		strongEntityType.root = true //TODO Si la tabla strong es a su vez weak, será false también.
	}
	
	// R1: Table -> SchemaType
	def static r1(Resource relResource, Resource uscResource) {
		println('Starting R1...')
		
		// Get schemas
		var RelationalSchema relationalSchema = relResource.contents.head as RelationalSchema
		var USchema uSchema = uscResource.contents.head as USchema
		
		// Apply rule
		for (table : relationalSchema.tables) {
			//TODO PRINTS
			print(table.name + ": [")
			table.columns.forEach[ c | print(c.name + ", ")]
			println("]")
			
			var SchemaType schemaType;
			
			if (false) { //TODO si table cumple la condición M:N entonces:
				schemaType = UschemaFactory.eINSTANCE.createRelationshipType
				
				// Add relationshipType to uSchema.relationships
				uSchema.relationships.add(schemaType as RelationshipType)
				
			} else { //TODO si no:
				schemaType = UschemaFactory.eINSTANCE.createEntityType
				
				// Add entityType to uSchema.entities
				uSchema.entities.add(schemaType as EntityType)
			}
			
			schemaType.name = table.name
			
			// R2
			r2(table, schemaType)
			
			// R3
			r3(table, schemaType)
			
			//TODO Esto habrá que hacerlo en otro recorrido de las tablas (otra función), ya que puede no haberse recorrido todavía la tabla strong, por lo que no la encontrará 
			if (schemaType instanceof EntityType) {
				// R4
				var Table strongTable = weakCondition(table)
				if (strongTable !== null) //TODO posible necesidad de tratamiento de errores
					r4(table, strongTable, schemaType, uSchema)
				
			}
		}
		
	}
	
	def static void main(String[] args) {
		
	// Get resources
		var ResourceSet resourceSet // resource set
		var Resource relResource // relational resource
		var Resource uscResource // uschema resource
	
		// Se carga en memoria el metamodelo relacional
		RelationalschemaPackage.eINSTANCE.eClass()
		// Lo mismo para USchemaPackage
		UschemaPackage.eINSTANCE.eClass()
		
		// Creamos un resourceSet
		resourceSet = new ResourceSetImpl()
		
		// El objeto que le pasamos es para decirle a resourceSet que sea capaz de leer archivos xmi
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
	
		// Cargamos el fichero xmi indicando su path
		var relUri = URI.createFileURI(INPUT_XMI_PATH)
		var uscUri = URI.createFileURI(OUTPUT_XMI_PATH)
	
		// Leemos el archivo xmi Relacional y lo cargamos en memoria
		relResource = resourceSet.getResource(relUri, true); // loading relational xmi
		// Creamos un nuevo xmi USchema en memoria (no se crea el fichero aún)
		uscResource = resourceSet.createResource(uscUri); // uschema xmi
		
		
	// Apply rules to the USchema resource
		r0(relResource, uscResource)
		r1(relResource, uscResource)


	// Save USchema resource
		uscResource.save(null);
	}
}