package juanfran.um;

import documentschema.Aggregate;
import documentschema.Array;
import documentschema.Attribute;
import documentschema.DataType;
import documentschema.DocumentSchema;
import documentschema.DocumentschemaPackage;
import documentschema.EntityType;
import documentschema.PrimitiveType;
import documentschema.Property;
import documentschema.Reference;
import documentschema.Type;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class MappingDocument2Text {
  private DocumentSchema ds;

  private final Set<Aggregate> aggregates = new HashSet<Aggregate>();

  public void mappingDocument2Text(final String outputFileUri) {
    try {
      int _lastIndexOf = outputFileUri.lastIndexOf("/");
      int _plus = (_lastIndexOf + 1);
      String _substring = outputFileUri.substring(0, _plus);
      new File(_substring).mkdirs();
      File file = new File(outputFileUri);
      file.createNewFile();
      final List<CharSequence> entitiesText = new ArrayList<CharSequence>();
      final List<CharSequence> aggregatesText = new ArrayList<CharSequence>();
      final Consumer<EntityType> _function = (EntityType et) -> {
        entitiesText.add(this.entityType2Text(et));
      };
      this.ds.getEntities().forEach(_function);
      final Consumer<Aggregate> _function_1 = (Aggregate ag) -> {
        aggregatesText.add(this.aggregateSchema(ag));
      };
      this.aggregates.forEach(_function_1);
      final FileWriter fileWriter = new FileWriter(file);
      fileWriter.append(this.mongooseLibrary());
      final Consumer<CharSequence> _function_2 = (CharSequence ag) -> {
        try {
          fileWriter.append(ag);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      aggregatesText.forEach(_function_2);
      final Consumer<CharSequence> _function_3 = (CharSequence et) -> {
        try {
          fileWriter.append(et);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      entitiesText.forEach(_function_3);
      fileWriter.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  public CharSequence mongooseLibrary() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("const mongoose = require(\'mongoose\');");
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }

  public CharSequence entityType2Text(final EntityType et) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("const ");
    String _name = et.getName();
    _builder.append(_name);
    _builder.append(" = new mongoose.Schema({");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    CharSequence _properties = this.properties(et.getProperties());
    _builder.append(_properties, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("})");
    _builder.newLine();
    return _builder;
  }

  public CharSequence properties(final EList<Property> properties) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _hasElements = false;
      for(final Property p : properties) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "");
        }
        CharSequence _switchResult = null;
        boolean _matched = false;
        if (p instanceof Attribute) {
          _matched=true;
          _switchResult = this.attribute(((Attribute)p));
        }
        if (!_matched) {
          if (p instanceof Reference) {
            _matched=true;
            _switchResult = this.reference(((Reference)p));
          }
        }
        if (!_matched) {
          if (p instanceof Aggregate) {
            _matched=true;
            _switchResult = this.aggregate(((Aggregate)p));
          }
        }
        _builder.append(_switchResult);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }

  public CharSequence attribute(final Attribute at) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = at.getName();
    _builder.append(_name);
    _builder.append(": {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("type: ");
    CharSequence _type = this.type(at.getType());
    _builder.append(_type, "\t");
    _builder.append(",");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("required: true,");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("unique: ");
    boolean _isIsKey = at.isIsKey();
    _builder.append(_isIsKey, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }

  public CharSequence reference(final Reference rf) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = rf.getName();
    _builder.append(_name);
    _builder.append(": {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("type: ");
    CharSequence _type = this.type(rf.getType());
    _builder.append(_type, "\t");
    _builder.append(",");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("ref: \'");
    String _name_1 = rf.getTarget().getName();
    _builder.append(_name_1, "\t");
    _builder.append("\',");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("required: true");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }

  public CharSequence aggregate(final Aggregate ag) {
    this.aggregates.add(ag);
    final Consumer<Property> _function = (Property p) -> {
      if ((p instanceof Aggregate)) {
        this.aggregate(((Aggregate)p));
      }
    };
    ag.getAggregates().forEach(_function);
    return this.aggregateSimple(ag);
  }

  public CharSequence aggregateSimple(final Aggregate ag) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = ag.getName();
    _builder.append(_name);
    _builder.append(": [");
    String _name_1 = ag.getName();
    _builder.append(_name_1);
    _builder.append("]");
    return _builder;
  }

  public CharSequence aggregateSchema(final Aggregate ag) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("const ");
    String _name = ag.getName();
    _builder.append(_name);
    _builder.append(" = new mongoose.Schema({");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    CharSequence _properties = this.properties(ag.getAggregates());
    _builder.append(_properties, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("})");
    _builder.newLine();
    return _builder;
  }

  public CharSequence type(final Type t) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _switchResult = null;
    boolean _matched = false;
    if (t instanceof PrimitiveType) {
      _matched=true;
      _switchResult = this.dataType2MongooseType(((PrimitiveType)t).getDatatype());
    }
    if (!_matched) {
      if (t instanceof Array) {
        _matched=true;
        CharSequence _dataType2MongooseType = this.dataType2MongooseType(((Array)t).getType().getDatatype());
        String _plus = ("[" + _dataType2MongooseType);
        _switchResult = (_plus + "]");
      }
    }
    _builder.append(_switchResult);
    return _builder;
  }

  public CharSequence dataType2MongooseType(final DataType dt) {
    StringConcatenation _builder = new StringConcatenation();
    String _switchResult = null;
    if (dt != null) {
      switch (dt) {
        case STRING:
          _switchResult = "String";
          break;
        case INTEGER:
          _switchResult = "Number";
          break;
        case DOUBLE:
          _switchResult = "Number";
          break;
        case BOOLEAN:
          _switchResult = "Boolean";
          break;
        default:
          break;
      }
    }
    _builder.append(_switchResult);
    return _builder;
  }

  public void loadSchema(final String path) {
    ResourceSet resourceSet = null;
    Resource docResource = null;
    URI docUri = URI.createFileURI(path);
    DocumentschemaPackage.eINSTANCE.eClass();
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    resourceSet = _resourceSetImpl;
    Map<String, Object> _extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    docResource = resourceSet.getResource(docUri, true);
    EObject _head = IterableExtensions.<EObject>head(docResource.getContents());
    this.ds = ((DocumentSchema) _head);
  }
}
