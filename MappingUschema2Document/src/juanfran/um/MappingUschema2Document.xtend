package juanfran.um

import uschema.USchema
import juanfran.um.trace.Trace
import documentschema.DocumentSchema
import documentschema.DocumentschemaFactory

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
		
		trace.addTrace(e.name, e, d.name, d)
	}	
}