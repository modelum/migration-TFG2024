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
import documentschema.DocumentschemaPackage

class MappingUschema2Document {
	
	var USchema uSchema;
	var DocumentSchema documentSchema;
	val HashMap<DataType, PrimitiveType> docTypes;
	val HashMap<DataType, Array> docArrayTypes;
	val DocumentschemaFactory dsFactory;
	val Trace trace;
	
	new() {
		this.docTypes = new HashMap
		this.docArrayTypes = new HashMap
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
		}
		
		for (d : documentSchema.entities) {
			val uschema.EntityType uet = trace.getSourceInstance(d.name).head as uschema.EntityType
			
			for (f: uet.features) {
				switch f {
					uschema.Aggregate: aggregate2Aggregate(f, d) // r3
					uschema.Reference: reference2Reference(f, d) // r5
				}
			}
		}
		
		for (rt : uSchema.relationships) {
			relationshipType2EntityType(rt) // r6
		}
		
		trace.printDirectTraceTypes
		
		return documentSchema
	}
	
	// R0: USchema to DocumentSchema
	def void uSchema2DocumentSchema() {
		documentSchema = dsFactory.createDocumentSchema
		createPrimitiveTypes()
		createArrays()
		
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
		
		// uschema.EntityType non-root
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
		
		// uschema.EntityType non-root
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
			
			for (uAt : f_key.attributes) {
				trace.addTrace(uAt.owner.name+"."+uAt.name, uAt, p_at.owner.name+"."+p_at.name, p_at)
			}
		}
	}
	
	// R4: Key (isKey=true) to Attribute from Aggregate
	def void key2Attribute(Key f_key, Aggregate ag) {
		if (f_key.attributes.size > 1) {
			val Attribute p_at = dsFactory.createAttribute

			p_at.name = ag.name + "_id"
			p_at.type = docTypes.get(DataType::STRING)
			p_at.isKey = true
			ag.aggregates.add(p_at)
			
			var String p_atTraceName = getAggregateRecursiveTraceName(ag) + "." + p_at.name
			for (uAt : f_key.attributes) {
				trace.addTrace(uAt.owner.name+"."+uAt.name, uAt, p_atTraceName, p_at)
			}
		}
	}
	
	// R5: uschema.Reference to Reference from EntityType
	def void reference2Reference(uschema.Reference f_ref, EntityType d) {
		if (f_ref.isFeaturedBy !== null)
			return
			
		val Reference p_ref = dsFactory.createReference
		val EntityType target = trace.getTargetInstance(f_ref.refsTo.name).head as EntityType
		val PrimitiveType primitiveType = findAttributeKey(target).type as PrimitiveType
		
		p_ref.name = f_ref.name
		p_ref.target = target
		
		// Reference.type
		if (f_ref.upperBound == 1) {
			p_ref.type = primitiveType
		} else if (f_ref.upperBound == -1 || f_ref.upperBound > 1) {
			val Array array = docArrayTypes.get(primitiveType.datatype)
			array.type = primitiveType
			p_ref.type = array
		}
		
		d.properties.add(p_ref)
		
		trace.addTrace(f_ref.owner.name+"."+f_ref.name, f_ref, p_ref.owner.name+"."+p_ref.name, p_ref)
	}
	
	// R5: uschema.Reference to Reference from Aggregate
	def void reference2Reference(uschema.Reference f_ref, Aggregate ag) {
		if (f_ref.isFeaturedBy !== null)
			return
		
		val Reference p_ref = dsFactory.createReference
		val EntityType target = trace.getTargetInstance(f_ref.refsTo.name).head as EntityType
		val PrimitiveType primitiveType = findAttributeKey(target).type as PrimitiveType
		
		p_ref.name = f_ref.name
		p_ref.target = target
		
		// Reference.type
		if (f_ref.upperBound == 1) {
			p_ref.type = primitiveType
		} else if (f_ref.upperBound == -1 || f_ref.upperBound > 1) {
			val Array array = docArrayTypes.get(primitiveType.datatype)
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
		
		// New EntityType from RelationshipType
		c.name = rt.name
		documentSchema.entities.add(c)
		
		trace.addTrace(rt.name, rt, c.name, c)
		
		// Map Attributes
		for (f : rt.features) {
			switch f {
				uschema.Attribute: attribute2Attribute(f, c)
			}
		}
		
		// New Attribute (id)
		val Attribute at = dsFactory.createAttribute
		at.name = c.name + "_id"
		at.isKey = true
		at.type = docTypes.get(DataType::STRING)
		c.properties.add(at)
		
		trace.addTrace(rt.name, rt, at.owner.name+"."+at.name, at)
		
		// New References
		for (r : rt.reference) {
			val Reference rf = dsFactory.createReference
			val EntityType refsTo = trace.getTargetInstance(r.refsTo.name).head as EntityType

			rf.name = r.name
			rf.target = refsTo
			rf.type = findAttributeKey(rf.target).type as PrimitiveType
			c.properties.add(rf)
			
			trace.addTrace(r.owner.name+"."+r.name, r, rf.owner.name+"."+rf.name, rf)
		}
	}
	
	// R7: Datatype to Type
	def Type datatype2Type(uschema.DataType dt) {
		if(dt instanceof uschema.PrimitiveType)
			return primitiveTypeConversionUsc2Doc(dt)
		else {
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
			val PrimitiveType pt = primitiveTypeConversionUsc2Doc(upt)
			
			return docArrayTypes.get(pt.datatype)
		}
	}
	
	// Creates the mapped properties from non-root uschema.EntityType.features and asign them to Aggregate
	def void features2Properties(uschema.EntityType uet, Aggregate ag) {		
		for (f : uet.features) {
			switch f {
				uschema.Attribute: attribute2Attribute(f, ag) // r2
				uschema.Aggregate: aggregate2Aggregate(f, ag) // r3
			}
			
		}
		
		for (f: uet.features) {
			switch f {
				uschema.Key: { 
					if (f.isID)
						key2Attribute(f, ag) // r4
				}
			}
		}
		
		for (f: uet.features) {
			switch f {
				uschema.Reference: reference2Reference(f, ag) // r5
			}
		}
	}
	
	// Obtains the recursive name for Aggregate that could be aggregatedBy another Aggregate
	def String getAggregateRecursiveTraceName(Aggregate g) {
		var Aggregate ag = g
		var String docAgTraceName = ag.name
		var boolean exit = false
		
		while (!exit) {
			if (ag.owner !== null) {
				docAgTraceName += ag.owner.name+"."+docAgTraceName
				exit = true
			} else {
				docAgTraceName += ag.aggregatedBy.name+"."+docAgTraceName
				ag = ag.aggregatedBy
			}
		}
		
		return docAgTraceName
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
	
	// Initialize all Array instances for DocumentSchema and add them to the HashMap docArrayTypes
	def createArrays() {
		val string = docTypes.get(DataType::STRING)
		val integer = docTypes.get(DataType::INTEGER)
		val doubl = docTypes.get(DataType::DOUBLE)
		val bool = docTypes.get(DataType::BOOLEAN)
		
		val arrayString = dsFactory.createArray
		val arrayInteger = dsFactory.createArray
		val arrayDouble = dsFactory.createArray
		val arrayBoolean = dsFactory.createArray
		
		arrayString.type = string
		arrayInteger.type = integer
		arrayDouble.type = doubl
		arrayBoolean.type = bool
		
		documentSchema.types.addAll(List.of(arrayString, arrayInteger, arrayDouble, arrayBoolean))
		
		docArrayTypes.put(string.datatype, arrayString)
		docArrayTypes.put(integer.datatype, arrayInteger)
		docArrayTypes.put(doubl.datatype, arrayDouble)
		docArrayTypes.put(bool.datatype, arrayBoolean)
	}
	
	// Types conversion from uschema.PrimitiveType to PrimitiveType
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
			
			DocumentschemaPackage.eINSTANCE.eClass()
			
			resourceSet = new ResourceSetImpl()
			resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
			
			docResource = resourceSet.createResource(docUri)
			docResource.contents.add(documentSchema)
			docResource.save(null)
		}
	}
	
	// Getters
	def USchema getUSchema() {
		return this.uSchema
	}
	def DocumentSchema getDocumentSchema() {
		return this.documentSchema
	}
	def Trace getTrace() {
		return this.trace
	}
	def HashMap<DataType, Array> getDocArrayTypes() {
		return this.docArrayTypes
	}
	
}