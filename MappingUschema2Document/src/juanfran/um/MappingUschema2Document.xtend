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
import uschema.Key
import java.util.List
import java.util.HashMap

class MappingUschema2Document {
	
	var USchema uSchema;
	var DocumentSchema documentSchema;
	val HashMap<documentschema.DataType, documentschema.PrimitiveType> docTypes;
	val DocumentschemaFactory dsFactory;
	val Trace trace;
	
	new() {
		this.docTypes = new HashMap
		this.dsFactory = DocumentschemaFactory.eINSTANCE
		this.trace = new Trace()
	}
	
	// R0: USchema to DocumentSchema
	def DocumentSchema USchema2DocumentSchema(USchema us) {
		uSchema = us
		documentSchema = dsFactory.createDocumentSchema
		createPrimitiveTypes()
		
		documentSchema.name = uSchema.name
		
		// r1
		for (e : uSchema.entities) {
			if (e.root) {
				entityType2EntityType(e)
			}
		}
		
		trace.addTrace(uSchema.name, uSchema, documentSchema.name, documentSchema)
		trace.printDirectTraceTypes

		return documentSchema
	}
	
	// R1: uschema.EntityType to documentschema.EntityType
	def void entityType2EntityType(uschema.EntityType e) {
		val documentschema.EntityType d = dsFactory.createEntityType
		
		d.name = e.name
		documentSchema.entities.add(d)
		
		// r2
		for (f : e.features) {
			switch f {
				uschema.Attribute: attribute2Attribute(f as uschema.Attribute, d)
				uschema.Aggregate: aggregate2Aggregate(f as uschema.Aggregate, d)
			}
			
		}
		//TODO: r4
		for (f: e.features) {
			switch f {
				uschema.Key: key2Attribute(f as uschema.Key, d)
			}
		}
		
		trace.addTrace(e.name, e, d.name, d)
	}
	
	// R2: uschema.Attribute to documentSchema.Attribute
	def void attribute2Attribute(uschema.Attribute f_at, documentschema.EntityType d) {
		val documentschema.Attribute p_at = dsFactory.createAttribute
		
		p_at.name = f_at.name
		p_at.type = datatype2Type(f_at.type) // p_at.type = f_at.type
		d.properties.add(p_at)
		
		// (r4)
		if (f_at.key !== null) {
			p_at.name = p_at.name + "_id"
			p_at.isKey = true
		}
		
		trace.addTrace(f_at.owner.name+"."+f_at.name, f_at, p_at.owner.name+"."+p_at.name, p_at)
		//trace.addTrace(f_at.owner.name+"."+f_at.name, f_at, p_at.owner.name+"."+p_at.name, p_at.type) // TODO: esto en principio no se hace
	}
	
	// R3: Aggregate to Aggregate
	def void aggregate2Aggregate(uschema.Aggregate f_ag, documentschema.EntityType d) {
		val documentschema.Aggregate p_ag = dsFactory.createAggregate
		val uschema.EntityType uet = f_ag.specifiedBy as uschema.EntityType
		
		d.properties.add(p_ag)
		p_ag.name = f_ag.name
		if (f_ag.upperBound > 1 || f_ag.upperBound == -1) {
			p_ag.isMany = true
		} else if (f_ag.upperBound == 1) {
			p_ag.isMany = false
		}
		
		p_ag.aggregates //TODO
		
	}
	
	// R4: Key (isKey=true) to documentschema.Attribute
	def void key2Attribute(Key f_key, documentschema.EntityType d) {
		if (f_key.attributes.size > 1) {
			val documentschema.Attribute p_at = dsFactory.createAttribute

			p_at.name = d.name + "_id"
			p_at.type = docTypes.get(documentschema.DataType::STRING)
			p_at.isKey = true
			d.properties.add(p_at)
			
			var String name = p_at.owner.name+"."
			for (uAt : f_key.attributes) {
				val dAt = trace.getTargetInstance(uAt.owner.name+"."+uAt.name).head as documentschema.Attribute
				name += dAt.name + ","
			}
			name = name.substring(0, name.length - 1) // Substracts the last comma
			
			trace.addTrace(f_key.owner.name+"."+f_key.name, f_key, name, p_at) //TODO: no sé si está bien la formación del nombre con el que se guarda p_at
		}
			
	}
	
	// R7: Datatype to Type   TODO: en este método se pasa por alto el añadir el primitiveType del Array (en caso de Array) a la traza, no sé si es correcto
	def Type datatype2Type(uschema.DataType dt) {
		if(dt instanceof uschema.PrimitiveType)
			return primitiveTypeConversionUsc2Doc(dt)
		else {
			val ar = dsFactory.createArray
			var uschema.PrimitiveType upt
			
			switch dt {	
				PList: {
					upt = dt.elementType as uschema.PrimitiveType
				}
				PSet: {
					upt = dt.elementType as uschema.PrimitiveType
				}
				PTuple: {
					upt = dt.elements.head as uschema.PrimitiveType
				}
				PMap: {
					upt = dt.valueType as uschema.PrimitiveType
				}
			}
			ar.type = primitiveTypeConversionUsc2Doc(upt)
			documentSchema.types.add(ar)
			
			return ar
		}
	}
	
	//TODO def documentschema.Property feature2Property()
	
	// Initialize all PrimitiveType instances for DocumentSchema and add them to the HashMap docTypes
	def createPrimitiveTypes() {
		val string = dsFactory.createPrimitiveType
		val integer = dsFactory.createPrimitiveType
		val doubl = dsFactory.createPrimitiveType
		val bool = dsFactory.createPrimitiveType
		
		string.datatype = documentschema.DataType::STRING
		integer.datatype = documentschema.DataType::INTEGER
		doubl.datatype = documentschema.DataType::DOUBLE
		bool.datatype = documentschema.DataType::BOOLEAN
		
		documentSchema.types.addAll(List.of(string, integer, doubl, bool))
		
		docTypes.put(string.datatype, string)
		docTypes.put(integer.datatype, integer)
		docTypes.put(doubl.datatype, doubl)
		docTypes.put(bool.datatype, bool)
	}
	
	// Types conversion from uschema.PrimitiveType to documentschema.PrimitiveType
	def documentschema.PrimitiveType primitiveTypeConversionUsc2Doc(uschema.PrimitiveType uDt) {
		var documentschema.DataType docDt;
		
		val String uDtUp = uDt.name.toUpperCase
		switch uDtUp {
			case "STRING": docDt = documentschema.DataType::STRING
			case "INT": docDt = documentschema.DataType::INTEGER
			case "DOUBLE": docDt = documentschema.DataType::DOUBLE
			case "BOOLEAN": docDt = documentschema.DataType::BOOLEAN
			case "DATE": docDt = documentschema.DataType::STRING
		}
		
		return docTypes.get(docDt)
	}
	
	// Separate a list of string with dots
	def static String dot(String... strings) {
		return strings.join('.')
	}
	
}