package juanfran.um

import uschema.USchema
import juanfran.um.trace.Trace
import documentschema.DocumentSchema
import documentschema.DocumentschemaFactory
import uschema.PList
import uschema.PSet
import uschema.PTuple
import uschema.PMap
import documentschema.Type

class MappingUschema2Document {
	
	val DocumentschemaFactory dsFactory;
	val Trace trace;
	
	new() {
		this.dsFactory = DocumentschemaFactory.eINSTANCE
		this.trace = new Trace()
	}
	
	// R0: USchema to DocumentSchema
	def DocumentSchema USchema2DocumentSchema(USchema us) {
		val DocumentSchema ds = dsFactory.createDocumentSchema
		
		ds.name = us.name
		
		// r1
		for (e : us.entities) {
			if (e.root) {
				entityType2EntityType(e, ds)
			}
		}
		
		trace.addTrace(us.name, us, ds.name, ds);
		trace.printDirectTraceTypes

		return ds
	}
	
	// R1: uschema.EntityType to documentschema.EntityType
	def void entityType2EntityType(uschema.EntityType e, DocumentSchema ds) {
		val documentschema.EntityType d = dsFactory.createEntityType
		
		ds.entities.add(d)
		d.name = e.name
		
		// r2
		for (f : e.features) {
			attribute2Attribute(f as uschema.Attribute, d, ds)
		}
		
		trace.addTrace(e.name, e, d.name, d)
	}
	
	// R2: uschema.Attribute to documentSchema.Attribute
	def attribute2Attribute(uschema.Attribute f_at, documentschema.EntityType d, DocumentSchema ds) {
		val documentschema.Attribute p_at = dsFactory.createAttribute
		
		d.properties.add(p_at)
		p_at.name = f_at.name
		p_at.type = datatype2Type(f_at.type, ds) // p_at.type = f_at.type
		println(p_at.type)
		
		trace.addTrace(
			dot(f_at.owner.name, f_at.name),
			f_at,
			dot(d.name, p_at.name),
			p_at.type
		)
		trace.addTrace(
			dot(f_at.owner.name, f_at.name),
			f_at,
			dot(d.name, p_at.name),
			p_at
		)
	}
	
	// R7: Datatype to Type   TODO: en este método se pasa por alto el añadir el primitiveType del Array (en caso de Array) a la traza, no sé si es correcto
	def Type datatype2Type(uschema.DataType dt, DocumentSchema ds) {
		val pt = dsFactory.createPrimitiveType
		ds.types.add(pt) //TODO estoy añadiendo el primitivetype al DocumentSchema, pero no sé si es la forma correcta
		
		switch dt {
			uschema.PrimitiveType: {
				pt.datatype = typeConversionUsc2Doc(dt)
				return pt
			}
			PList: {
				val ar = dsFactory.createArray
				val upt = dt.elementType as uschema.PrimitiveType
				pt.datatype = typeConversionUsc2Doc(upt)
				ar.type = pt
				ds.types.add(ar)
				return ar
			}
			PSet: {
				val ar = dsFactory.createArray
				val upt = dt.elementType as uschema.PrimitiveType
				pt.datatype = typeConversionUsc2Doc(upt)
				ar.type = pt
				ds.types.add(ar)
				return ar
			}
			PTuple: {
				val ar = dsFactory.createArray
				val upt = dt.elements.head as uschema.PrimitiveType
				pt.datatype = typeConversionUsc2Doc(upt)
				ar.type = pt
				ds.types.add(ar)
				return ar
			}
			PMap: {
				val ar = dsFactory.createArray
				val upt = dt.valueType as uschema.PrimitiveType
				pt.datatype = typeConversionUsc2Doc(upt)
				ar.type = pt
				ds.types.add(ar)
				return ar
			}
		}
	}
	
	// Types conversion from USchema to Document
	def static documentschema.DataType typeConversionUsc2Doc(uschema.PrimitiveType dt) {
		val String dtUp = dt.name.toUpperCase
		switch dtUp {
			case "STRING": return documentschema.DataType::STRING
			case "INT": return documentschema.DataType::INTEGER
			case "DOUBLE": return documentschema.DataType::DOUBLE
			case "BOOLEAN": return documentschema.DataType::BOOLEAN
		}
	}
	
	// Separate a list of string with dots
	def static String dot(String... strings) {
		return strings.join('.')
	}
	
}