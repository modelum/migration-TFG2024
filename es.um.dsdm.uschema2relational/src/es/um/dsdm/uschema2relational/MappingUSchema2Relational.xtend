package es.um.dsdm.uschema2relational

import relationalschema.RelationalMMFactory
import relationalschema.RelationalSchema
import relationalschema.Table
import relationalschema.Column
import relationalschema.FKey
import relationalschema.ReferentialAction
import es.um.dsdm.trace.Trace
import uschema.EntityType
import uschema.USchema
import uschema.Attribute
import uschema.Reference
import uschema.Aggregate
import uschema.RelationshipType
import uschema.DataType
import uschema.PrimitiveType
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import uschema.UschemaMMPackage
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

class MappingUSchema2Relational {

	val RelationalMMFactory relationalFactory;
	var USchema uSchema
	var RelationalSchema relationalSchema
	val Trace trace

	new() {
		relationalFactory = RelationalMMFactory.eINSTANCE
		trace = new Trace
	}

	def RelationalSchema transformacion() {
		uschema2relational() // r0
		uSchema.entities.forEach[e|entityType2Table(e)] // r1
		uSchema.entities.forEach [ e |
			e.features.forEach [ feature |
				if (feature instanceof uschema.Attribute) {
					attribute2Column(feature) // r2
				}
			]
			e.features.forEach [ feature |
				if (feature instanceof uschema.Key) {
					key2key(feature) // r3
				} else if (feature instanceof uschema.Reference) {
					reference2fkey(feature) // r4
				}
			]
			e.features.forEach [ feature |
				if (feature instanceof uschema.Aggregate) {
					aggregate2weakTable(feature) // r5
				}
			]
		]
		uSchema.relationships.forEach [ r |
			relationshipType2Table(r) // r6
		]
		trace.printDirectTraceTypes
		return relationalSchema
	}

	// R0: USchema to Relational Schema
	def void uschema2relational() {
		relationalSchema = relationalFactory.createRelationalSchema
		relationalSchema.name = uSchema.name
		trace.addTrace(uSchema.name, uSchema, relationalSchema.name, relationalSchema)
	}

	// R1: EntityType to Table
	def Table entityType2Table(EntityType e) {
		if (trace.directTraceString.containsKey(e.name)) {
			return trace.getTargetInstance(e.name).head as Table
		}
		val t = relationalFactory.createTable
		t.name = e.name
		relationalSchema.tables.add(t)
		trace.addTrace(e.name, e, t.name, t)
		return t
	}

	// R2: Attribute to Column
	def Column attribute2Column(Attribute at) {
		if (trace.directTraceString.containsKey(at.owner.name + "." + at.name))
			return trace.getTargetInstance(at.owner.name + "." + at.name).head as Column
		val Table t = trace.getTargetInstance(at.owner.name).head as Table
		val Column col = relationalFactory.createColumn
		col.name = at.name
		col.datatype = mapType2String(at.type)
		col.size = getSizeForDatatype(col.datatype)
		col.nullable = true
		col.defaultvalue = null
		t.columns.add(col)
		trace.addTrace(at.owner.name + "." + at.name, at, col.owner.name + "." + col.name, col)
		return col
	}

	// R3: uschema.Key to relationalschema.Key
	def void key2key(uschema.Key k) {
		val Table t = trace.getTargetInstance(k.owner.name).head as Table
		val relationalschema.Key pk = relationalFactory.createKey
		pk.constraintname = k.isID ? t.name + "_pk" : k.name + "_ak"
		pk.owner = t
		pk.isPK = k.isID
		k.attributes.forEach[pk.columns.add(attribute2Column(it))]
		t.keys.add(pk)
		trace.addTrace(k.owner.name + "." + k.name, k, pk.owner.name + "." + pk.constraintname, pk)
	}

	// R4: Reference to Fkey [+ columns]
	def void reference2fkey(Reference ref) {
		val Table t = trace.getTargetInstance(ref.owner.name).head as Table
		val FKey fk = relationalFactory.createFKey
		val tr = trace.getTargetInstance(ref.refsTo.name).head as Table
		fk.constraintname = tr.name + "_fk"
		fk.owner = t
		fk.refsTo = tr.keys.findFirst[pk|pk.isPK]
		// if (ref.attributes !== null) {
		if (ref.attributes.size !== 0) {
			ref.attributes.forEach [ attribute |
				fk.columns.add(attribute2Column(attribute))
			]
		} else {
			fk.refsTo.columns.forEach [ column |
				var columna = relationalFactory.createColumn
				columna = column
				// t.columns.add(columna)
				fk.columns.add(columna)
				trace.addTrace(ref.owner.name + "." + ref.name, ref, columna.owner.name + "." + columna.name, columna)
			]
		}
		fk.onDelete = ReferentialAction.NO_ACTION
		fk.onUpdate = ReferentialAction.CASCADE
		t.fks.add(fk)
		trace.addTrace(ref.owner.name + "." + ref.name, ref, fk.owner.name + "." + fk.constraintname, fk)
	}

	// R5: Aggregate [+ EntityType non root] to WeakTable
	def void aggregate2weakTable(Aggregate ag) {
		val tg = trace.getTargetInstance(ag.specifiedBy.name).head as Table
		val fk = relationalFactory.createFKey
		val ts = trace.getTargetInstance(ag.owner.name).head as Table
		fk.constraintname = ts.name + "_fk"
		fk.refsTo = ts.keys.findFirst[pk|pk.isPK]
		fk.refsTo.columns.forEach [ column |
			var columna = relationalFactory.createColumn
			columna = column
			// tg.columns.add(columna)
			fk.columns.add(columna)
			trace.addTrace(ag.owner.name + "." + ag.name, ag, columna.owner.name + "." + columna.name, columna)
		]
		fk.onDelete = ReferentialAction.NO_ACTION
		fk.onUpdate = ReferentialAction.CASCADE
		tg.fks.add(fk)
		trace.addTrace(ag.owner.name + "." + ag.name, ag, fk.owner.name + "." + fk.constraintname, fk)
		val k = relationalFactory.createKey
		k.constraintname = tg.name + "_pk"
		k.isPK = true
		val col = relationalFactory.createColumn
		col.name = "id"
		col.datatype = "NUMERIC"
		col.size = 38
		col.nullable = false
		col.defaultvalue = "1"
		fk.columns.forEach [ column |
			k.columns.add(column)
		]
		k.columns.add(col)
		tg.keys.add(k)
		tg.columns.add(col)
		trace.addTrace(ag.owner.name + "." + ag.name, ag, k.owner.name + "." + k.constraintname, k)
		trace.addTrace(ag.owner.name + "." + ag.name, ag, col.owner.name + "." + col.name, col)
	}

	// R6: RelationshipType to Table (+2FKey + Key)
	def void relationshipType2Table(RelationshipType rt) {
		val t = relationalFactory.createTable
		t.name = rt.name
		trace.addTrace(rt.name, rt, t.name, t)
		rt.features.forEach [ feature |
			if (feature instanceof Attribute) {
				// r2
				t.columns.add(attribute2Column(feature as Attribute))
			}
		]
		rt.reference.forEach [ reference |
			if (reference.isFeaturedBy == rt) {
				val td = trace.getTargetInstance(reference.refsTo.name).head as Table
				val fk1 = relationalFactory.createFKey
				setFk(fk1, td, rt)
				t.fks.add(fk1)
				trace.addTrace(rt.name, rt, fk1.owner.name + "." + fk1.constraintname, fk1)
				val to = trace.getTargetInstance(reference.owner.name).head as Table
				val fk2 = relationalFactory.createFKey
				setFk(fk2, to, rt)
				t.fks.add(fk2)
				trace.addTrace(rt.name, rt, fk2.owner.name + "." + fk2.constraintname, fk2)
				val pk = relationalFactory.createKey
				pk.constraintname = t.name + "_pk"
				pk.isPK = true
				addColumns(fk1, pk)
				addColumns(fk2, pk)
				t.keys.add(pk)
				trace.addTrace(rt.name, rt, pk.owner.name + "." + pk.constraintname, pk)
			}
		]
		/* 	else if (feature instanceof Reference) {
		 * 		if (feature.isFeaturedBy == rt) {
		 * 			val td = trace.getTargetInstance(feature.refsTo.name).head as Table
		 * 			val fk1 = relationalFactory.createFKey
		 * 			fk1.constraintname = td.name + "_fk"
		 * 			fk1.refsTo = td.keys.findFirst[pk |
		 * 				pk.isPK
		 * 			]
		 * 			if (fk1.refsTo !== null) {
		 * 				fk1.refsTo.columns.forEach[column |
		 * 					var columna = relationalFactory.createColumn
		 * 					columna = column
		 * 					fk1.columns.add(columna)
		 * 					trace.addTrace(rt.name, rt, fk1.constraintname + "." + column.name, column)
		 * 				]
		 * 			}
		 * 			t.fks.add(fk1)
		 * 			trace.addTrace(rt.name, rt, fk1.owner.name + "." + fk1.constraintname, fk1)
		 * 			val to = trace.getTargetInstance(feature.owner.name).head as Table
		 * 			val fk2 = relationalFactory.createFKey
		 * 			fk2.constraintname = to.name + "_fk"
		 * 			fk2.refsTo = to.keys.findFirst[pk |
		 * 				pk.isPK
		 * 			]
		 * 			if (fk2.refsTo !== null) {
		 * 				fk2.refsTo.columns.forEach[column |
		 * 					var columna = relationalFactory.createColumn
		 * 					columna = column
		 * 					fk2.columns.add(columna)
		 * 					trace.addTrace(rt.name, rt, fk2.constraintname + "." + column.name, column)
		 * 				]
		 * 			}
		 * 			t.fks.add(fk2)
		 * 			trace.addTrace(rt.name, rt, fk2.owner.name + "." + fk2.constraintname, fk2)
		 * 			val pk = relationalFactory.createKey
		 * 			pk.constraintname = t.name + "_pk"
		 * 			pk.isPK = true
		 * 			fk1.columns.forEach[column |
		 * 				pk.columns.add(column)
		 * 			]
		 * 			fk2.columns.forEach[column |
		 * 				pk.columns.add(column)
		 * 			]
		 * 			t.keys.add(pk)
		 * 			trace.addTrace(rt.name, rt, pk.owner.name + "." + pk.constraintname, pk)
		 * 		}
		 }*/
		relationalSchema.tables.add(t)
	}

	// R7
	def String mapType2String(DataType datatype) {
		if ((datatype as PrimitiveType) !== null) {
			switch ((datatype as PrimitiveType).name.toUpperCase) {
				case "STRING": return "VARCHAR"
				case "INTEGER": return "NUMERIC"
				case "BOOLEAN": return "BOOLEAN"
				case "DOUBLE": return "NUMERIC"
				case "DATE": return "DATE"
			}
		}
	}

	def void imprimirTraza() {
		trace.printDirectTraceTypes
	}

	private def void setFk(FKey fk, Table table, RelationshipType rt) {
		fk.constraintname = table.name + "_fk"
		fk.refsTo = table.keys.findFirst [ pk |
			pk.isPK
		]
		if (fk.refsTo !== null) {
			fk.refsTo.columns.forEach [ column |
				var columna = relationalFactory.createColumn
				columna = column
				fk.columns.add(columna)
				trace.addTrace(rt.name, rt, fk.constraintname + "." + column.name, column)
			]
		}
	}

	private def void addColumns(FKey fk, relationalschema.Key pk) {
		fk.columns.forEach [ column |
			pk.columns.add(column)
		]
	}

	def int getSizeForDatatype(String datatype) {
		switch (datatype) {
			case "VARCHAR": return 255
			case "NUMERIC": return 38
			case "BOOLEAN": return 1
			case "DATE": return 10
			default: return 0
		}
	}

	def void loadSchema(String path) {
		var ResourceSet resourceSet
		var Resource uschemaResource
		var URI docUri = URI.createFileURI(path)
		UschemaMMPackage.eINSTANCE.eClass()
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		uschemaResource = resourceSet.getResource(docUri, true)
		uSchema = uschemaResource.contents.head as USchema
	}

	def USchema getUschema() {
		return this.uSchema
	}

	def RelationalSchema getRelationalSchema() {
		return this.relationalSchema
	}

}
