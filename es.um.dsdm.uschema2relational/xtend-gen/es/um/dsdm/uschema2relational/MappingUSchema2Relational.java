package es.um.dsdm.uschema2relational;

import com.google.common.base.Objects;
import es.um.dsdm.trace.Trace;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import relationalschema.Column;
import relationalschema.FKey;
import relationalschema.ReferentialAction;
import relationalschema.RelationalMMFactory;
import relationalschema.RelationalSchema;
import relationalschema.Table;
import uschema.Aggregate;
import uschema.Attribute;
import uschema.DataType;
import uschema.EntityType;
import uschema.Feature;
import uschema.Key;
import uschema.PrimitiveType;
import uschema.Reference;
import uschema.RelationshipType;
import uschema.USchema;
import uschema.UschemaMMPackage;

@SuppressWarnings("all")
public class MappingUSchema2Relational {
  private final RelationalMMFactory relationalFactory;

  private USchema uSchema;

  private RelationalSchema relationalSchema;

  private final Trace trace;

  public MappingUSchema2Relational() {
    this.relationalFactory = RelationalMMFactory.eINSTANCE;
    Trace _trace = new Trace();
    this.trace = _trace;
  }

  public RelationalSchema transformacion() {
    this.uschema2relational();
    final Consumer<EntityType> _function = (EntityType e) -> {
      this.entityType2Table(e);
    };
    this.uSchema.getEntities().forEach(_function);
    final Consumer<EntityType> _function_1 = (EntityType e) -> {
      final Consumer<Feature> _function_2 = (Feature feature) -> {
        if ((feature instanceof Attribute)) {
          this.attribute2Column(((Attribute)feature));
        }
      };
      e.getFeatures().forEach(_function_2);
      final Consumer<Feature> _function_3 = (Feature feature) -> {
        if ((feature instanceof Key)) {
          this.key2key(((Key)feature));
        } else {
          if ((feature instanceof Reference)) {
            this.reference2fkey(((Reference)feature));
          }
        }
      };
      e.getFeatures().forEach(_function_3);
      final Consumer<Feature> _function_4 = (Feature feature) -> {
        if ((feature instanceof Aggregate)) {
          this.aggregate2weakTable(((Aggregate)feature));
        }
      };
      e.getFeatures().forEach(_function_4);
    };
    this.uSchema.getEntities().forEach(_function_1);
    final Consumer<RelationshipType> _function_2 = (RelationshipType r) -> {
      this.relationshipType2Table(r);
    };
    this.uSchema.getRelationships().forEach(_function_2);
    this.trace.printDirectTraceTypes();
    return this.relationalSchema;
  }

  public void uschema2relational() {
    this.relationalSchema = this.relationalFactory.createRelationalSchema();
    this.relationalSchema.setName(this.uSchema.getName());
    this.trace.addTrace(this.uSchema.getName(), this.uSchema, this.relationalSchema.getName(), this.relationalSchema);
  }

  public Table entityType2Table(final EntityType e) {
    boolean _containsKey = this.trace.getDirectTraceString().containsKey(e.getName());
    if (_containsKey) {
      Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(e.getName()));
      return ((Table) _head);
    }
    final Table t = this.relationalFactory.createTable();
    t.setName(e.getName());
    this.relationalSchema.getTables().add(t);
    this.trace.addTrace(e.getName(), e, t.getName(), t);
    return t;
  }

  public Column attribute2Column(final Attribute at) {
    SortedMap<String, List<String>> _directTraceString = this.trace.getDirectTraceString();
    String _name = at.getOwner().getName();
    String _plus = (_name + ".");
    String _name_1 = at.getName();
    String _plus_1 = (_plus + _name_1);
    boolean _containsKey = _directTraceString.containsKey(_plus_1);
    if (_containsKey) {
      String _name_2 = at.getOwner().getName();
      String _plus_2 = (_name_2 + ".");
      String _name_3 = at.getName();
      String _plus_3 = (_plus_2 + _name_3);
      Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(_plus_3));
      return ((Column) _head);
    }
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(at.getOwner().getName()));
    final Table t = ((Table) _head_1);
    final Column col = this.relationalFactory.createColumn();
    col.setName(at.getName());
    col.setDatatype(this.mapType2String(at.getType()));
    col.setSize(this.getSizeForDatatype(col.getDatatype()));
    col.setNullable(true);
    col.setDefaultvalue(null);
    t.getColumns().add(col);
    String _name_4 = at.getOwner().getName();
    String _plus_4 = (_name_4 + ".");
    String _name_5 = at.getName();
    String _plus_5 = (_plus_4 + _name_5);
    String _name_6 = col.getOwner().getName();
    String _plus_6 = (_name_6 + ".");
    String _name_7 = col.getName();
    String _plus_7 = (_plus_6 + _name_7);
    this.trace.addTrace(_plus_5, at, _plus_7, col);
    return col;
  }

  public void key2key(final Key k) {
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(k.getOwner().getName()));
    final Table t = ((Table) _head);
    final relationalschema.Key pk = this.relationalFactory.createKey();
    String _xifexpression = null;
    boolean _isIsID = k.isIsID();
    if (_isIsID) {
      String _name = t.getName();
      _xifexpression = (_name + "_pk");
    } else {
      String _name_1 = k.getName();
      _xifexpression = (_name_1 + "_ak");
    }
    pk.setConstraintname(_xifexpression);
    pk.setOwner(t);
    pk.setIsPK(k.isIsID());
    final Consumer<Attribute> _function = (Attribute it) -> {
      pk.getColumns().add(this.attribute2Column(it));
    };
    k.getAttributes().forEach(_function);
    t.getKeys().add(pk);
    String _name_2 = k.getOwner().getName();
    String _plus = (_name_2 + ".");
    String _name_3 = k.getName();
    String _plus_1 = (_plus + _name_3);
    String _name_4 = pk.getOwner().getName();
    String _plus_2 = (_name_4 + ".");
    String _constraintname = pk.getConstraintname();
    String _plus_3 = (_plus_2 + _constraintname);
    this.trace.addTrace(_plus_1, k, _plus_3, pk);
  }

  public void reference2fkey(final Reference ref) {
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(ref.getOwner().getName()));
    final Table t = ((Table) _head);
    final FKey fk = this.relationalFactory.createFKey();
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(ref.getRefsTo().getName()));
    final Table tr = ((Table) _head_1);
    String _name = tr.getName();
    String _plus = (_name + "_fk");
    fk.setConstraintname(_plus);
    fk.setOwner(t);
    final Function1<relationalschema.Key, Boolean> _function = (relationalschema.Key pk) -> {
      return Boolean.valueOf(pk.isIsPK());
    };
    fk.setRefsTo(IterableExtensions.<relationalschema.Key>findFirst(tr.getKeys(), _function));
    int _size = ref.getAttributes().size();
    boolean _tripleNotEquals = (_size != 0);
    if (_tripleNotEquals) {
      final Consumer<Attribute> _function_1 = (Attribute attribute) -> {
        fk.getColumns().add(this.attribute2Column(attribute));
      };
      ref.getAttributes().forEach(_function_1);
    } else {
      final Consumer<Column> _function_2 = (Column column) -> {
        Column columna = this.relationalFactory.createColumn();
        columna = column;
        fk.getColumns().add(columna);
        String _name_1 = ref.getOwner().getName();
        String _plus_1 = (_name_1 + ".");
        String _name_2 = ref.getName();
        String _plus_2 = (_plus_1 + _name_2);
        String _name_3 = columna.getOwner().getName();
        String _plus_3 = (_name_3 + ".");
        String _name_4 = columna.getName();
        String _plus_4 = (_plus_3 + _name_4);
        this.trace.addTrace(_plus_2, ref, _plus_4, columna);
      };
      fk.getRefsTo().getColumns().forEach(_function_2);
    }
    fk.setOnDelete(ReferentialAction.NO_ACTION);
    fk.setOnUpdate(ReferentialAction.CASCADE);
    t.getFks().add(fk);
    String _name_1 = ref.getOwner().getName();
    String _plus_1 = (_name_1 + ".");
    String _name_2 = ref.getName();
    String _plus_2 = (_plus_1 + _name_2);
    String _name_3 = fk.getOwner().getName();
    String _plus_3 = (_name_3 + ".");
    String _constraintname = fk.getConstraintname();
    String _plus_4 = (_plus_3 + _constraintname);
    this.trace.addTrace(_plus_2, ref, _plus_4, fk);
  }

  public void aggregate2weakTable(final Aggregate ag) {
    Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(ag.getSpecifiedBy().getName()));
    final Table tg = ((Table) _head);
    final FKey fk = this.relationalFactory.createFKey();
    Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(ag.getOwner().getName()));
    final Table ts = ((Table) _head_1);
    String _name = ts.getName();
    String _plus = (_name + "_fk");
    fk.setConstraintname(_plus);
    final Function1<relationalschema.Key, Boolean> _function = (relationalschema.Key pk) -> {
      return Boolean.valueOf(pk.isIsPK());
    };
    fk.setRefsTo(IterableExtensions.<relationalschema.Key>findFirst(ts.getKeys(), _function));
    final Consumer<Column> _function_1 = (Column column) -> {
      Column columna = this.relationalFactory.createColumn();
      columna = column;
      fk.getColumns().add(columna);
      String _name_1 = ag.getOwner().getName();
      String _plus_1 = (_name_1 + ".");
      String _name_2 = ag.getName();
      String _plus_2 = (_plus_1 + _name_2);
      String _name_3 = columna.getOwner().getName();
      String _plus_3 = (_name_3 + ".");
      String _name_4 = columna.getName();
      String _plus_4 = (_plus_3 + _name_4);
      this.trace.addTrace(_plus_2, ag, _plus_4, columna);
    };
    fk.getRefsTo().getColumns().forEach(_function_1);
    fk.setOnDelete(ReferentialAction.NO_ACTION);
    fk.setOnUpdate(ReferentialAction.CASCADE);
    tg.getFks().add(fk);
    String _name_1 = ag.getOwner().getName();
    String _plus_1 = (_name_1 + ".");
    String _name_2 = ag.getName();
    String _plus_2 = (_plus_1 + _name_2);
    String _name_3 = fk.getOwner().getName();
    String _plus_3 = (_name_3 + ".");
    String _constraintname = fk.getConstraintname();
    String _plus_4 = (_plus_3 + _constraintname);
    this.trace.addTrace(_plus_2, ag, _plus_4, fk);
    final relationalschema.Key k = this.relationalFactory.createKey();
    String _name_4 = tg.getName();
    String _plus_5 = (_name_4 + "_pk");
    k.setConstraintname(_plus_5);
    k.setIsPK(true);
    final Column col = this.relationalFactory.createColumn();
    col.setName("id");
    col.setDatatype("NUMERIC");
    col.setSize(38);
    col.setNullable(false);
    col.setDefaultvalue("1");
    final Consumer<Column> _function_2 = (Column column) -> {
      k.getColumns().add(column);
    };
    fk.getColumns().forEach(_function_2);
    k.getColumns().add(col);
    tg.getKeys().add(k);
    tg.getColumns().add(col);
    String _name_5 = ag.getOwner().getName();
    String _plus_6 = (_name_5 + ".");
    String _name_6 = ag.getName();
    String _plus_7 = (_plus_6 + _name_6);
    String _name_7 = k.getOwner().getName();
    String _plus_8 = (_name_7 + ".");
    String _constraintname_1 = k.getConstraintname();
    String _plus_9 = (_plus_8 + _constraintname_1);
    this.trace.addTrace(_plus_7, ag, _plus_9, k);
    String _name_8 = ag.getOwner().getName();
    String _plus_10 = (_name_8 + ".");
    String _name_9 = ag.getName();
    String _plus_11 = (_plus_10 + _name_9);
    String _name_10 = col.getOwner().getName();
    String _plus_12 = (_name_10 + ".");
    String _name_11 = col.getName();
    String _plus_13 = (_plus_12 + _name_11);
    this.trace.addTrace(_plus_11, ag, _plus_13, col);
  }

  public void relationshipType2Table(final RelationshipType rt) {
    final Table t = this.relationalFactory.createTable();
    t.setName(rt.getName());
    this.trace.addTrace(rt.getName(), rt, t.getName(), t);
    final Consumer<Feature> _function = (Feature feature) -> {
      if ((feature instanceof Attribute)) {
        t.getColumns().add(this.attribute2Column(((Attribute) feature)));
      }
    };
    rt.getFeatures().forEach(_function);
    final Consumer<Reference> _function_1 = (Reference reference) -> {
      RelationshipType _isFeaturedBy = reference.getIsFeaturedBy();
      boolean _equals = Objects.equal(_isFeaturedBy, rt);
      if (_equals) {
        Object _head = IterableExtensions.<Object>head(this.trace.getTargetInstance(reference.getRefsTo().getName()));
        final Table td = ((Table) _head);
        final FKey fk1 = this.relationalFactory.createFKey();
        this.setFk(fk1, td, rt);
        t.getFks().add(fk1);
        String _name = rt.getName();
        String _name_1 = fk1.getOwner().getName();
        String _plus = (_name_1 + ".");
        String _constraintname = fk1.getConstraintname();
        String _plus_1 = (_plus + _constraintname);
        this.trace.addTrace(_name, rt, _plus_1, fk1);
        Object _head_1 = IterableExtensions.<Object>head(this.trace.getTargetInstance(reference.getOwner().getName()));
        final Table to = ((Table) _head_1);
        final FKey fk2 = this.relationalFactory.createFKey();
        this.setFk(fk2, to, rt);
        t.getFks().add(fk2);
        String _name_2 = rt.getName();
        String _name_3 = fk2.getOwner().getName();
        String _plus_2 = (_name_3 + ".");
        String _constraintname_1 = fk2.getConstraintname();
        String _plus_3 = (_plus_2 + _constraintname_1);
        this.trace.addTrace(_name_2, rt, _plus_3, fk2);
        final relationalschema.Key pk = this.relationalFactory.createKey();
        String _name_4 = t.getName();
        String _plus_4 = (_name_4 + "_pk");
        pk.setConstraintname(_plus_4);
        pk.setIsPK(true);
        this.addColumns(fk1, pk);
        this.addColumns(fk2, pk);
        t.getKeys().add(pk);
        String _name_5 = rt.getName();
        String _name_6 = pk.getOwner().getName();
        String _plus_5 = (_name_6 + ".");
        String _constraintname_2 = pk.getConstraintname();
        String _plus_6 = (_plus_5 + _constraintname_2);
        this.trace.addTrace(_name_5, rt, _plus_6, pk);
      }
    };
    rt.getReference().forEach(_function_1);
    this.relationalSchema.getTables().add(t);
  }

  public String mapType2String(final DataType datatype) {
    if ((((PrimitiveType) datatype) != null)) {
      String _upperCase = ((PrimitiveType) datatype).getName().toUpperCase();
      if (_upperCase != null) {
        switch (_upperCase) {
          case "STRING":
            return "VARCHAR";
          case "INTEGER":
            return "NUMERIC";
          case "BOOLEAN":
            return "BOOLEAN";
          case "DOUBLE":
            return "NUMERIC";
          case "DATE":
            return "DATE";
        }
      }
    }
    return null;
  }

  public void imprimirTraza() {
    this.trace.printDirectTraceTypes();
  }

  private void setFk(final FKey fk, final Table table, final RelationshipType rt) {
    String _name = table.getName();
    String _plus = (_name + "_fk");
    fk.setConstraintname(_plus);
    final Function1<relationalschema.Key, Boolean> _function = (relationalschema.Key pk) -> {
      return Boolean.valueOf(pk.isIsPK());
    };
    fk.setRefsTo(IterableExtensions.<relationalschema.Key>findFirst(table.getKeys(), _function));
    relationalschema.Key _refsTo = fk.getRefsTo();
    boolean _tripleNotEquals = (_refsTo != null);
    if (_tripleNotEquals) {
      final Consumer<Column> _function_1 = (Column column) -> {
        Column columna = this.relationalFactory.createColumn();
        columna = column;
        fk.getColumns().add(columna);
        String _name_1 = rt.getName();
        String _constraintname = fk.getConstraintname();
        String _plus_1 = (_constraintname + ".");
        String _name_2 = column.getName();
        String _plus_2 = (_plus_1 + _name_2);
        this.trace.addTrace(_name_1, rt, _plus_2, column);
      };
      fk.getRefsTo().getColumns().forEach(_function_1);
    }
  }

  private void addColumns(final FKey fk, final relationalschema.Key pk) {
    final Consumer<Column> _function = (Column column) -> {
      pk.getColumns().add(column);
    };
    fk.getColumns().forEach(_function);
  }

  public int getSizeForDatatype(final String datatype) {
    if (datatype != null) {
      switch (datatype) {
        case "VARCHAR":
          return 255;
        case "NUMERIC":
          return 38;
        case "BOOLEAN":
          return 1;
        case "DATE":
          return 10;
        default:
          return 0;
      }
    } else {
      return 0;
    }
  }

  public void loadSchema(final String path) {
    ResourceSet resourceSet = null;
    Resource uschemaResource = null;
    URI docUri = URI.createFileURI(path);
    UschemaMMPackage.eINSTANCE.eClass();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    resourceSet = _resourceSetImpl;
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    uschemaResource = resourceSet.getResource(docUri, true);
    EObject _head = IterableExtensions.<EObject>head(uschemaResource.getContents());
    this.uSchema = ((USchema) _head);
  }

  public USchema getUschema() {
    return this.uSchema;
  }

  public RelationalSchema getRelationalSchema() {
    return this.relationalSchema;
  }
}
