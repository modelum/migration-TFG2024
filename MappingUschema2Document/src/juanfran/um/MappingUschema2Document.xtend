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
import documentschema.PrimitiveType
import documentschema.EntityType
import documentschema.Attribute
import documentschema.Aggregate
import documentschema.DataType
import documentschema.Reference
import org.eclipse.emf.common.util.EList
import documentschema.Array

class MappingUschema2Document {
	
	var USchema uSchema;
	var DocumentSchema documentSchema;
	val HashMap<DataType, PrimitiveType> docTypes;
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
		val EntityType d = dsFactory.createEntityType
		
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
				uschema.Key: { 
					if (f.isID)
						key2Attribute(f as uschema.Key, d)
				}
			}
		}
		
		//TODO: r5
		for (f: e.features) {
			switch f {
				uschema.Reference: reference2Reference(f, d)
			}
		}
		
		trace.addTrace(e.name, e, d.name, d)
	}
	
	// R2: uschema.Attribute to documentSchema.Attribute
	def void attribute2Attribute(uschema.Attribute f_at, EntityType d) {
		val Attribute p_at = dsFactory.createAttribute
		
		p_at.name = f_at.name
		p_at.type = datatype2Type(f_at.type) // p_at.type = f_at.type
		d.properties.add(p_at)
		
		// (r4)
		if (f_at.key !== null && f_at.key.attributes.size == 1 && f_at.key.isID) {
			p_at.name = p_at.name + "_id"
			p_at.isKey = true
		}
		
		trace.addTrace(f_at.owner.name+"."+f_at.name, f_at, p_at.owner.name+"."+p_at.name, p_at)
		//trace.addTrace(f_at.owner.name+"."+f_at.name, f_at, p_at.owner.name+"."+p_at.name, p_at.type) // TODO: esto en principio no se hace
	}
	
	// R3: Aggregate to Aggregate
	def void aggregate2Aggregate(uschema.Aggregate f_ag, EntityType d) {
		val Aggregate p_ag = dsFactory.createAggregate
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
	def void key2Attribute(Key f_key, EntityType d) {
		if (f_key.attributes.size > 1) {
			val Attribute p_at = dsFactory.createAttribute

			p_at.name = d.name + "_id"
			p_at.type = docTypes.get(DataType::STRING)
			p_at.isKey = true
			d.properties.add(p_at)
			
			var String name = p_at.owner.name+"."
			for (uAt : f_key.attributes) {
				val dAt = trace.getTargetInstance(uAt.owner.name+"."+uAt.name).head as Attribute
				name += dAt.name + ","
			}
			name = name.substring(0, name.length - 1) // Substracts the last comma
			
			trace.addTrace(f_key.owner.name+"."+f_key.name, f_key, name, p_at) //TODO: no sé si está bien la formación del nombre con el que se guarda p_at
		}
	}
	
	// R5: Reference to Reference
	def void reference2Reference(uschema.Reference f_ref, EntityType d) {
		val Reference p_ref = dsFactory.createReference
		val EntityType target = trace.getTargetInstance(f_ref.refsTo.name).head as EntityType
		val Attribute key = findAttributeKey(target.properties)
		
		p_ref.name = f_ref.name
		p_ref.target = target
		
		// Reference.type
		if (f_ref.upperBound == 1) {
			p_ref.type = key.type as PrimitiveType
		} else if (f_ref.upperBound == -1 || f_ref.upperBound > 1) {
			val Array array = dsFactory.createArray
			
			documentSchema.types.add(array)
			array.type = key.type as PrimitiveType
			p_ref.type = array
		}
		
		d.properties.add(p_ref)
		
		trace.addTrace(f_ref.owner.name+"."+f_ref.name, f_ref, p_ref.owner.name+"."+p_ref.name, p_ref)
	}
	
	// R7: Datatype to Type   TODO: revisar si hay que hacer traza para array o introducirlos de forma global como los PrimitiveType
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
		
		string.datatype = DataType::STRING
		integer.datatype = DataType::INTEGER
		doubl.datatype = DataType::DOUBLE
		bool.datatype = DataType::BOOLEAN
		
		documentSchema.types.addAll(List.of(string, integer, doubl, bool))
		
		docTypes.put(string.datatype, string)
		docTypes.put(integer.datatype, integer)
		docTypes.put(doubl.datatype, doubl)
		docTypes.put(bool.datatype, bool)
	}
	
	// Types conversion from uschema.PrimitiveType to documentschema.PrimitiveType
	def PrimitiveType primitiveTypeConversionUsc2Doc(uschema.PrimitiveType uDt) {
		var DataType docDt;
		
		val String uDtUp = uDt.name.toUpperCase
		switch uDtUp {
			case "STRING": docDt = DataType::STRING
			case "INT": docDt = DataType::INTEGER
			case "DOUBLE": docDt = DataType::DOUBLE
			case "BOOLEAN": docDt = DataType::BOOLEAN
			case "DATE": docDt = DataType::STRING
		}
		
		return docTypes.get(docDt)
	}
	
	// Find the Attribute that has isKey==true from the list of documentschema.EntityType.properties
	def Attribute findAttributeKey(EList<documentschema.Property> properties) {
		return properties.findFirst[ p |
			p instanceof Attribute &&
			(p as Attribute).isIsKey
		] as Attribute
	}
	
	// Separate a list of string with dots
	def static String dot(String... strings) {
		return strings.join('.')
	}
	
}