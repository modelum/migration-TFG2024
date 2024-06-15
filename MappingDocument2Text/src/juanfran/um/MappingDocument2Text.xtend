package juanfran.um

import documentschema.EntityType
import documentschema.Property
import documentschema.Attribute
import documentschema.Reference
import documentschema.Aggregate
import documentschema.DocumentSchema
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.Resource
import documentschema.DocumentschemaPackage
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.common.util.URI
import documentschema.PrimitiveType
import documentschema.Type
import documentschema.Array
import java.io.File
import java.io.FileWriter
import documentschema.DataType
import org.eclipse.emf.common.util.EList
import java.util.Set
import java.util.HashSet
import java.util.List
import java.util.ArrayList

class MappingDocument2Text {
	
	var DocumentSchema ds
	val Set<Aggregate> aggregates = new HashSet
	
	def mappingDocument2Text(String outputFileUri) {
		new File(outputFileUri.substring(0, outputFileUri.lastIndexOf("/") + 1)).mkdirs
		
		var file = new File(outputFileUri)
		file.createNewFile
		
		// Mapping
		val List<CharSequence> entitiesText = new ArrayList
		val List<CharSequence> aggregatesText = new ArrayList
		ds.entities.forEach[ et | entitiesText.add(entityType2Text(et)) ]
		aggregates.forEach[ ag | aggregatesText.add(aggregateSchema(ag)) ]
		
		val FileWriter fileWriter = new FileWriter(file)
		fileWriter.append(mongooseLibrary)
		aggregatesText.forEach[ ag | fileWriter.append(ag) ]
		entitiesText.forEach[ et | fileWriter.append(et) ]
		
		fileWriter.close
	}
	
	def mongooseLibrary() '''
		const mongoose = require('mongoose');
		
	'''
	
	def entityType2Text(EntityType et) '''
		const «et.name» = new mongoose.Schema({
			«properties(et.properties)»
		})
	'''
	
	def properties(EList<Property> properties) '''
		«FOR p : properties SEPARATOR ','»
			«switch p {
				Attribute: attribute(p)
				Reference: reference(p)
				Aggregate: aggregate(p)
			}»
		«ENDFOR»
	'''
	
	def attribute(Attribute at) '''
		«at.name»: {
			type: «type(at.type)»,
			required: true,
			unique: «at.isIsKey»
		}
	'''
	
	def reference(Reference rf) '''
		«rf.name»: {
			type: «type(rf.type)»,
			ref: '«rf.target.name»',
			required: true
		}
	'''
	
	def CharSequence aggregate(Aggregate ag) {
		aggregates.add(ag)

		ag.aggregates.forEach[ p | 
			if (p instanceof Aggregate)
				aggregate(p)
		]
		
		return aggregateSimple(ag)
	}
	
	def aggregateSimple(Aggregate ag) '''
		«ag.name»: [«ag.name»]'''
		
	def aggregateSchema(Aggregate ag) '''
		const «ag.name» = new mongoose.Schema({
			«properties(ag.aggregates)»
		})
	'''
	
	def type(Type t) '''
		«switch t {
			PrimitiveType: dataType2MongooseType(t.datatype)
			Array: "[" + dataType2MongooseType(t.type.datatype) + "]"
		}»'''
	
	def dataType2MongooseType(DataType dt) '''
		«switch dt {
			case DataType::STRING: "String"
			case DataType::INTEGER: "Number"
			case DataType::DOUBLE: "Number"
			case DataType::BOOLEAN: "Boolean"
		}»'''
	
	def void loadSchema(String path) { //TODO: MAIN
		var ResourceSet resourceSet
		var Resource docResource
		var URI docUri = URI.createFileURI(path)
		
		DocumentschemaPackage.eINSTANCE.eClass()
		
		resourceSet = new ResourceSetImpl()
		resourceSet.resourceFactoryRegistry.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl())
		
		docResource = resourceSet.getResource(docUri, true)
		
		ds = docResource.contents.head as DocumentSchema
	}
}