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
import documentschema.Array
import uschema.RelationshipType
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import uschema.UschemaPackage
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

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
	
	// Creation of the mapping
	def DocumentSchema executeMapping() {
		uSchema2DocumentSchema() // r0
		
		for (e : uSchema.entities) {
			if (e.root)
				entityType2EntityType(e) // r1
		}
		
		for (d : documentSchema.entities) {
			val uschema.EntityType uet = trace.getSourceInstance(d.name).head as uschema.EntityType
		
			for (f : uet.features) {
				switch f {
					uschema.Attribute: attribute2Attribute(f, d) // r2
					uschema.Aggregate: aggregate2Aggregate(f, d) // r3
				}
				
			}
			
			for (f: uet.features) {
				switch f {
					uschema.Key: { 
						if (f.isID)
							key2Attribute(f, d) // r4
					}
				}
			}
			
			for (f: uet.features) {
				switch f {
					uschema.Reference: reference2Reference(f, d) // r5
				}
			}
		}
		
		trace.printDirectTraceTypes
		
		return documentSchema
	}
	
	// R0: USchema to DocumentSchema
	def void uSchema2DocumentSchema() {
		documentSchema = dsFactory.createDocumentSchema
		createPrimitiveTypes()
		
		documentSchema.name = uSchema.name
		
		trace.addTrace(uSchema.name, uSchema, documentSchema.name, documentSchema)
	}
	
	// R1: uschema.EntityType to EntityType
	def void entityType2EntityType(uschema.EntityType e) {
		val EntityType d = dsFactory.createEntityType
		
		d.name = e.name
		documentSchema.entities.add(d)
		
		trace.addTrace(e.name, e, d.name, d)
	}
	
	// R2: uschema.Attribute to Attribute from EntityType
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
	}
	
	// R2: uschema.Attribute to Attribute from Aggregate
	def void attribute2Attribute(uschema.Attribute f_at, Aggregate ag) {
		val Attribute p_at = dsFactory.createAttribute
		
		p_at.name = f_at.name
		p_at.type = datatype2Type(f_at.type) // p_at.type = f_at.type
		ag.aggregates.add(p_at)
		
		// (r4)
		if (f_at.key !== null && f_at.key.attributes.size == 1 && f_at.key.isID) {
			p_at.name = p_at.name + "_id"
			p_at.isKey = true
		}
		
		var String p_atTraceName = getAggregateRecursiveTraceName(ag)+ "." + p_at.name
		
		trace.addTrace(f_at.owner.name+"."+f_at.name, f_at, p_atTraceName, p_at)
	}
	
	// R3: uschema.Aggregate to Aggregate from EntityType
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
		
		trace.addTrace(f_ag.owner.name+"."+f_ag.name, f_ag, d.name+"."+p_ag.name, p_ag)
		
		// EntityType non-root
		features2Properties(uet, p_ag)
	}
	
	// R3: uschema.Aggregate to Aggregate from Aggregate
	def void aggregate2Aggregate(uschema.Aggregate f_ag, Aggregate ag) {
		val Aggregate p_ag = dsFactory.createAggregate
		val uschema.EntityType uet = f_ag.specifiedBy as uschema.EntityType
		
		ag.aggregates.add(p_ag)
		p_ag.name = f_ag.name
		if (f_ag.upperBound > 1 || f_ag.upperBound == -1) {
			p_ag.isMany = true
		} else if (f_ag.upperBound == 1) {
			p_ag.isMany = false
		}
		
		val String p_agTraceName = getAggregateRecursiveTraceName(p_ag)
		
		trace.addTrace(f_ag.owner.name+"."+f_ag.name, f_ag, p_agTraceName, p_ag)
		
		// EntityType non-root
		features2Properties(uet, p_ag)
	}
	
	// R4: Key (isKey=true) to Attribute from EntityType
	def void key2Attribute(Key f_key, EntityType d) {
		if (f_key.attributes.size > 1) {
			val Attribute p_at = dsFactory.createAttribute

			p_at.name = d.name + "_id"
			p_at.type = docTypes.get(DataType::STRING)
			p_at.isKey = true
			d.properties.add(p_at)
			
			val p_atTraceName = p_at.owner.name + "." + getKeyAttributesName(f_key)
			
			trace.addTrace(f_key.owner.name+"."+f_key.name, f_key, p_atTraceName, p_at) //TODO: no sé si está bien la formación del nombre con el que se guarda p_at
		}
	}
	
	// R4: Key (isKey=true) to Attribute from Aggregate
	def void key2Attribute(Key f_key, Aggregate ag) {
		if (f_key.attributes.size > 1) {
			val Attribute p_at = dsFactory.createAttribute

			p_at.name = ag.name + "_id" //TODO: no sé si es el nombre del Aggregate o el nombre del f_key.owner (EntityType non-root)
			p_at.type = docTypes.get(DataType::STRING)
			p_at.isKey = true
			ag.aggregates.add(p_at)
			
			var String p_atTraceName = getAggregateRecursiveTraceName(ag)
			p_atTraceName += p_atTraceName+"."+getKeyAttributesName(f_key)
			
			trace.addTrace(f_key.owner.name+"."+f_key.name, f_key, p_atTraceName, p_at) //TODO: no sé si está bien la formación del nombre con el que se guarda p_at
		}
	}
	
	// R5: uschema.Reference to Reference from EntityType
	def void reference2Reference(uschema.Reference f_ref, EntityType d) {
		val Reference p_ref = dsFactory.createReference
		val EntityType target = trace.getTargetInstance(f_ref.refsTo.name).head as EntityType
		val PrimitiveType primitiveType = findAttributeKey(target).type as PrimitiveType
		
		p_ref.name = f_ref.name
		p_ref.target = target
		
		// Reference.type
		if (f_ref.upperBound == 1) {
			p_ref.type = primitiveType
		} else if (f_ref.upperBound == -1 || f_ref.upperBound > 1) {
			val Array array = dsFactory.createArray
			
			documentSchema.types.add(array)
			array.type = primitiveType
			p_ref.type = array
		}
		
		d.properties.add(p_ref)
		
		trace.addTrace(f_ref.owner.name+"."+f_ref.name, f_ref, p_ref.owner.name+"."+p_ref.name, p_ref)
	}
	
	// R5: uschema.Reference to Reference from Aggregate
	def void reference2Reference(uschema.Reference f_ref, Aggregate ag) {
		val Reference p_ref = dsFactory.createReference
		val EntityType target = trace.getTargetInstance(f_ref.refsTo.name).head as EntityType
		val PrimitiveType primitiveType = findAttributeKey(target).type as PrimitiveType
		
		p_ref.name = f_ref.name
		p_ref.target = target
		
		// Reference.type
		if (f_ref.upperBound == 1) {
			p_ref.type = primitiveType
		} else if (f_ref.upperBound == -1 || f_ref.upperBound > 1) {
			val Array array = dsFactory.createArray
			
			documentSchema.types.add(array)
			array.type = primitiveType
			p_ref.type = array
		}
		
		ag.aggregates.add(p_ref)
		
		val String p_refTraceName = getAggregateRecursiveTraceName(ag)+ "." + p_ref.name
		
		trace.addTrace(f_ref.owner.name+"."+f_ref.name, f_ref, p_refTraceName, p_ref)
	}
	
	// R6: RelationshipType to EntityType
	def void relationshipType2EntityType(RelationshipType rt) {
		val EntityType c = dsFactory.createEntityType
		
		c.name = rt.name
		documentSchema.entities.add(c)
		
		// Map Attributes
		for (f : rt.features) {
			switch f {
				uschema.Attribute: attribute2Attribute(f, c)
			}
		}
		
		// New Attribute
		val Attribute at = dsFactory.createAttribute
		at.name = c.name + "_id"
		at.isKey = true
		at.type = docTypes.get(DataType::STRING)
		c.properties.add(at)
		//TODO: traza del nuevo atributo
		
		for (r : rt.reference) {
			val Reference rf = dsFactory.createReference
			val EntityType refsTo = trace.getTargetInstance(r.refsTo.name).head as EntityType
			val EntityType owner = trace.getTargetInstance(r.owner.name).head as EntityType
			val Reference p = trace.getTargetInstance(r.owner.name+"."+c.name).head as Reference

			rf.target = refsTo
			rf.type = findAttributeKey(rf.target).type as PrimitiveType
			c.properties.add(rf)
			
			// Remove Reference p
			if (p !== null)
				owner.properties.remove(p)
			
			// New Reference q
			val Reference q = dsFactory.createReference
			val array = dsFactory.createArray
			documentSchema.types.add(array)
			array.type = findAttributeKey(q.target) as PrimitiveType
			
			q.name = c.name
			q.target = c
			q.type = array
			owner.properties.add(q)
			
			//TODO: traza de la referencia
		}
		
		//TODO: traza del nuevo EntityType
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
	
	// Creates the mapped properties from non-root uschema.EntityType.features and asign them to Aggregate
	def void features2Properties(uschema.EntityType uet, Aggregate ag) {		
		// r2, r3
		for (f : uet.features) {
			switch f {
				uschema.Attribute: attribute2Attribute(f, ag)
				uschema.Aggregate: aggregate2Aggregate(f, ag)
			}
			
		}
		
		// r4
		for (f: uet.features) {
			switch f {
				uschema.Key: { 
					if (f.isID)
						key2Attribute(f, ag)
				}
			}
		}
		
		// r5
		for (f: uet.features) {
			switch f {
				uschema.Reference: reference2Reference(f, ag)
			}
		}
	}
	
	// Obtains the recursive name for Aggregate that could be aggregatedBy another Aggregate
	def String getAggregateRecursiveTraceName(Aggregate ag) {
		var String docAgTraceName = ag.name
		var boolean exit = false
		
		while (!exit) {
			if (ag.owner !== null) {
				docAgTraceName += ag.owner.name+"."+docAgTraceName
				exit = true
			} else {
				docAgTraceName += ag.aggregatedBy.name+"."+docAgTraceName
			}
		}
		
		return docAgTraceName
	}
	
	// Returns a comma-separated String containing the names of all uschema.Attribute instances within a given uschema.Key
	def String getKeyAttributesName(uschema.Key f_key) {
		var String name = ""
		for (uAt : f_key.attributes) {
				val dAt = trace.getTargetInstance(uAt.owner.name+"."+uAt.name).head as Attribute
				name += dAt.name + ","
			}
			name = name.substring(0, name.length - 1) // Substracts the last comma
	}
	
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
	
	// Find the Attribute that has isKey==true from an EntityType
	def Attribute findAttributeKey(EntityType et) {
		return et.properties.findFirst[ p |
			p instanceof Attribute &&
			(p as Attribute).isIsKey
		] as Attribute
	}
	
	// Separate a list of string with dots
	def static String dot(String... strings) {
		return strings.join('.')
	}
	
	// Loads the USchema from the input path
	def void loadSchema(String path) {
		var ResourceSet resourceSet
		var Resource uscResource
		var URI uscUri = URI.createFileURI(path)
		
		UschemaPackage.eINSTANCE.eClass()
		
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		
		uscResource = resourceSet.getResource(uscUri, true)
		
		uSchema = uscResource.contents.head as USchema
	}
	
	// Saves the DocumentSchema to the output path
	def void saveSchema(String output) {
		if (documentSchema !== null) {
			var ResourceSet resourceSet
			var Resource docResource
			var URI docUri = URI.createFileURI(output)
			
			UschemaPackage.eINSTANCE.eClass()
			
			resourceSet = new ResourceSetImpl()
			resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
			
			docResource = resourceSet.createResource(docUri)
			docResource.contents.add(documentSchema)
			docResource.save(null)
		}
	}
	
}