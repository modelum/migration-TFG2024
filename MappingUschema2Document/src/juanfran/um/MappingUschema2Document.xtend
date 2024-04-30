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
	def DocumentSchema USchema2DocumentSchema(USchema uSchema) {
		val DocumentSchema documentSchema = dsFactory.createDocumentSchema
		
		documentSchema.name = uSchema.name
		
		trace.addTrace(uSchema.name, uSchema, documentSchema.name, documentSchema);
		trace.printDirectTraceTypes

		return documentSchema
	}
	
}