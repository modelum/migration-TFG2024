package juanfran.um;

import documentschema.DocumentSchema;
import documentschema.DocumentschemaFactory;
import juanfran.um.trace.Trace;
import uschema.USchema;

@SuppressWarnings("all")
public class MappingUschema2Document {
  private final DocumentschemaFactory dsFactory;

  private final Trace trace;

  public MappingUschema2Document() {
    this.dsFactory = DocumentschemaFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public DocumentSchema USchema2DocumentSchema(final USchema uSchema) {
    final DocumentSchema documentSchema = this.dsFactory.createDocumentSchema();
    documentSchema.setName(uSchema.getName());
    this.trace.addTrace(uSchema.getName(), uSchema, documentSchema.getName(), documentSchema);
    this.trace.printDirectTraceTypes();
    return documentSchema;
  }
}
