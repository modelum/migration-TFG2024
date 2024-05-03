package juanfran.um;

import documentschema.DocumentSchema;
import documentschema.DocumentschemaFactory;
import juanfran.um.trace.Trace;
import org.eclipse.emf.common.util.EList;
import uschema.EntityType;
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

  public DocumentSchema USchema2DocumentSchema(final USchema us) {
    final DocumentSchema ds = this.dsFactory.createDocumentSchema();
    ds.setName(us.getName());
    EList<EntityType> _entities = us.getEntities();
    for (final EntityType e : _entities) {
      boolean _isRoot = e.isRoot();
      if (_isRoot) {
        this.entityType2EntityType(e, ds);
      }
    }
    this.trace.addTrace(us.getName(), us, ds.getName(), ds);
    this.trace.printDirectTraceTypes();
    return ds;
  }

  public void entityType2EntityType(final EntityType e, final DocumentSchema ds) {
    final documentschema.EntityType d = this.dsFactory.createEntityType();
    ds.getEntities().add(d);
    d.setName(e.getName());
    this.trace.addTrace(e.getName(), e, d.getName(), d);
  }
}
