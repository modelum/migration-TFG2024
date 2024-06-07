package es.um.dsdm.document2uschema

import documentschema.DocumentSchema

import es.um.dsdm.trace.Trace
import uschema.USchema
import uschema.UschemaMMFactory
import uschema.PrimitiveType
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.common.util.URI
import documentschema.DocumentMMPackage
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import java.util.List

class MappingDocument2Uschema {

	var USchema uSchema
	var DocumentSchema documentSchema
	val UschemaMMFactory uschemaFactory;
	val Trace trace

	new() {
		uschemaFactory = UschemaMMFactory.eINSTANCE
		trace = new Trace()
	}

	def USchema transformacion() {
		// r0
		documentSchema2USchema()
		// r1
		documentSchema.entities.forEach[e|entityType2entityType(e)]
		documentSchema.entities.forEach [ e |
			e.properties.forEach [ p |
				switch p {
					// r2
					documentschema.Attribute: attribute2attribute(p as documentschema.Attribute)
					// r3
					documentschema.Reference: reference2reference(p as documentschema.Reference)
				}
			]
		]
		documentSchema.entities.forEach [ e |
			e.properties.forEach [ p |
				if (p instanceof documentschema.Aggregate)
					// r4
					aggregate2entityType(p as documentschema.Aggregate)
			]
		]
		trace.printDirectTraceTypes
		return uSchema
	}

	// R0: DocumentSchema to USchema
	def void documentSchema2USchema() {
		uSchema = uschemaFactory.createUSchema
		uSchema.name = documentSchema.name
		trace.addTrace(documentSchema.name, documentSchema, uSchema.name, uSchema)
	}

	// R1: DocumentSchema EntityType to USchema EntityType
	def uschema.EntityType entityType2entityType(documentschema.EntityType d) {
		if (trace.directTraceString.containsKey(d.name))
			return trace.getTargetInstance(d.name).head as uschema.EntityType

		val ed = uschemaFactory.createEntityType
		ed.name = d.name
		ed.root = true
		trace.addTrace(d.name, d, ed.name, ed)
		uSchema.entities.add(ed)
		return ed
	}

	// R2: Attribute to Attribute or Attribute + Key
	def void attribute2attribute(documentschema.Attribute a) {
		val ea = uschemaFactory.createAttribute
		val uschema.EntityType ed = trace.getTargetInstance(a.owner.name).head as uschema.EntityType
		ea.name = a.name
		ea.type = type2datatype(a.type)
		if (a.isKey) {
			var k = uschemaFactory.createKey
			k.name = a.name
			k.isID = true
			k.attributes.add(ea)
			ea.key = k
			ed.features.add(k)
			trace.addTrace(a.owner.name + "." + a.name, a, k.owner.name + "." + k.name + ".key", k)
		}
		ed.features.add(ea)
		trace.addTrace(a.owner.name + "." + a.name, a, ea.owner.name + "." + ea.name + ".attribute", ea)
	}

	// R3: Reference to Reference + Attribute
	def void reference2reference(documentschema.Reference r) {
		val er = uschemaFactory.createReference
		val ea = uschemaFactory.createAttribute
		val uschema.EntityType ed = trace.getTargetInstance(r.owner.name).head as uschema.EntityType
		er.name = r.name
		ea.name = r.name
		er.lowerBound = 1
		er.upperBound = r.type instanceof documentschema.PrimitiveType ? 1 : -1
		er.attributes.add(ea)
		ea.references.add(er)
		er.refsTo = trace.getTargetInstance(r.target.name).head as uschema.EntityType
		ed.features.add(er)
		trace.addTrace(r.owner.name + "." + r.name, r, er.owner.name + "." + er.name + ".reference", er)
		ed.features.add(ea)
		trace.addTrace(r.owner.name + "." + r.name, r, ea.owner.name + "." + ea.name + ".attribute", ea)
	}

	// R4: Aggregate to EntityType + Aggregate
	def void aggregate2entityType(documentschema.Aggregate g) {
		val uschema.EntityType ed = trace.getTargetInstance(g.owner.name).head as uschema.EntityType
		// Comprobamos si el agregado ya se mapeo
		val eg = trace.directTraceString.containsKey(g.name)
			? trace.getTargetInstance(g.name).head as uschema.EntityType
			: uschemaFactory.createEntityType
		eg.name = g.name
		eg.root = false
		if (!trace.directTraceString.containsKey(g.name)) {
			uSchema.entities.add(eg)
			trace.addTrace(g.owner.name + "." + g.name, g, eg.name, eg)
		}
		addAggregateFeatures(g, eg)
		val ag = uschemaFactory.createAggregate
		ag.name = g.name
		ag.lowerBound = 1
		ag.upperBound = g.isMany ? -1 : 1
		ag.specifiedBy = eg
		ed.root = true
		ed.features.add(ag)
		trace.addTrace(g.owner.name + "." + g.name, g, ag.owner.name + "." + ag.name + ".aggregate", ag)
	}

	// R5
	def uschema.DataType type2datatype(documentschema.Type type) {
		switch type {
			case documentschema.PrimitiveType: return createPrimitiveType(type as documentschema.PrimitiveType)
			case documentschema.Array: return createArrayType(type as documentschema.Array)
		}
	}

	private def void addAggregateFeatures(documentschema.Aggregate g, uschema.EntityType eg) {
		if (g.aggregates !== null) {
			switch g.aggregates {
				documentschema.Attribute: {
					// Buscamos en la lista de la traza el elemento que sea de tipo Attribute
					val atributo = trace.getTargetInstance(g.aggregates.owner.name + "." + g.aggregates.name).findFirst [ e |
						e instanceof uschema.Attribute
					] as uschema.Attribute
					eg.features.add(atributo)
				}
				documentschema.Reference: {
					// Buscamos en la lista de la traza el elemento que sea de tipo Reference
					val referencia = trace.getTargetInstance(g.aggregates.owner.name + "." + g.aggregates.name).
						findFirst [ e |
							e instanceof uschema.Reference
						] as uschema.Reference
					eg.features.add(referencia)
				}
				documentschema.Aggregate: {
					// El agregado es posible que no esté mapeado y puede ser recursivo asi que lo procesamos
					aggregate2entityType(g.aggregates as documentschema.Aggregate)
				}
			}
		}
	}

	private def uschema.PrimitiveType createPrimitiveType(documentschema.PrimitiveType type) {
		val et = uschemaFactory.createPrimitiveType
		et.name = mapPrimitiveTypeName(type.datatype)
		trace.addTrace(type.datatype.getName, type, et.name, et)
		return et
	}

	private def uschema.PList createArrayType(documentschema.Array type) {
		val plist = uschemaFactory.createPList
		plist.elementType = type2datatype(type.type)
		val primitivo = plist.elementType as PrimitiveType
		trace.addTrace(type.type.datatype.getName, type, primitivo.name, plist)
		return plist
	}

	def private String mapPrimitiveTypeName(documentschema.DataType datatype) {
		switch (datatype) {
			case documentschema.DataType::BOOLEAN: return "BOOLEAN"
			case documentschema.DataType::INTEGER: return "INTEGER"
			case documentschema.DataType::DOUBLE: return "DOUBLE"
			case documentschema.DataType::STRING: return "STRING"
		}
	}

	def void loadSchema(String path) {
		var ResourceSet resourceSet
		var Resource docResource
		var URI docUri = URI.createFileURI(path)
		DocumentMMPackage.eINSTANCE.eClass()
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		docResource = resourceSet.getResource(docUri, true)
		documentSchema = docResource.contents.head as DocumentSchema
	}

	def void imprimirTraza() {
		trace.printDirectTraceTypes
	}

	def USchema getUSchema() {
		return this.uSchema
	}

	def DocumentSchema getDocumentSchema() {
		return this.documentSchema
	}
}
