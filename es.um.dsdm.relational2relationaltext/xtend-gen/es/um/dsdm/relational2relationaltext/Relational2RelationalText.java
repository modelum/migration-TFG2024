package es.um.dsdm.relational2relationaltext;

import com.google.common.base.Objects;
import java.io.File;
import java.io.FileWriter;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import relationalschema.Column;
import relationalschema.FKey;
import relationalschema.Key;
import relationalschema.ReferentialAction;
import relationalschema.RelationalSchema;
import relationalschema.Table;

@SuppressWarnings("all")
public class Relational2RelationalText {
  private static final String slash = "/";

  public void relational2RelationalText(final RelationalSchema relationalSchema, final String outputFileUri) {
    try {
      int _lastIndexOf = outputFileUri.lastIndexOf(Relational2RelationalText.slash);
      int _plus = (_lastIndexOf + 1);
      String _substring = outputFileUri.substring(0, _plus);
      new File(_substring).mkdirs();
      File file = new File(outputFileUri);
      file.createNewFile();
      final FileWriter fileWriter = new FileWriter(file);
      final Consumer<Table> _function = (Table t) -> {
        try {
          fileWriter.append(this.table2text(t));
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      relationalSchema.getTables().forEach(_function);
      fileWriter.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }

  private CharSequence table2text(final Table table) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("CREATE TABLE ");
    String _name = table.getName();
    _builder.append(_name);
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _columns = this.columns(table);
    _builder.append(_columns, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _primaryKeys = this.primaryKeys(table);
    _builder.append(_primaryKeys, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _foreignKeys = this.foreignKeys(table);
    _builder.append(_foreignKeys, "\t");
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  /**
   * def private columns(Table table) '''
   * «FOR c : table.columns SEPARATOR ','»
   * «c.name» «c.datatype»(«c.size»)«if (!c.nullable) ' NOT NULL' else ''»«if (c.defaultvalue != 'NULL') ' DEFAULT ' + c.defaultvalue else ''»
   * «ENDFOR»
   * '''
   */
  private String columns(final Table table) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<Column> _columns = table.getColumns();
      boolean _hasElements = false;
      for(final Column c : _columns) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "");
        }
        String _name = c.getName();
        _builder.append(_name);
        _builder.append(" ");
        String _formatDatatype = this.formatDatatype(c);
        _builder.append(_formatDatatype);
        String _xifexpression = null;
        boolean _isNullable = c.isNullable();
        boolean _not = (!_isNullable);
        if (_not) {
          _xifexpression = " NOT NULL";
        } else {
          _xifexpression = "";
        }
        _builder.append(_xifexpression);
        String _xifexpression_1 = null;
        String _defaultvalue = c.getDefaultvalue();
        boolean _notEquals = (!Objects.equal(_defaultvalue, "NULL"));
        if (_notEquals) {
          String _defaultvalue_1 = c.getDefaultvalue();
          _xifexpression_1 = (" DEFAULT " + _defaultvalue_1);
        } else {
          _xifexpression_1 = "";
        }
        _builder.append(_xifexpression_1);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder.toString();
  }

  private String primaryKeys(final Table table) {
    StringConcatenation _builder = new StringConcatenation();
    {
      final Function1<Key, Boolean> _function = (Key it) -> {
        return Boolean.valueOf(it.isIsPK());
      };
      int _size = IterableExtensions.size(IterableExtensions.<Key>filter(table.getKeys(), _function));
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        {
          final Function1<Key, Boolean> _function_1 = (Key it) -> {
            return Boolean.valueOf(it.isIsPK());
          };
          Iterable<Key> _filter = IterableExtensions.<Key>filter(table.getKeys(), _function_1);
          boolean _hasElements = false;
          for(final Key k : _filter) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "");
            }
            _builder.append("PRIMARY KEY (");
            final Function1<Column, String> _function_2 = (Column c) -> {
              return c.getName();
            };
            String _join = IterableExtensions.join(ListExtensions.<Column, String>map(k.getColumns(), _function_2), ", ");
            _builder.append(_join);
            _builder.append(")");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder.toString();
  }

  private String foreignKeys(final Table table) {
    StringConcatenation _builder = new StringConcatenation();
    {
      int _size = table.getFks().size();
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        {
          EList<FKey> _fks = table.getFks();
          boolean _hasElements = false;
          for(final FKey fkey : _fks) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "");
            }
            _builder.append("CONSTRAINT ");
            String _constraintname = fkey.getConstraintname();
            _builder.append(_constraintname);
            _builder.append(" FOREIGN KEY (");
            final Function1<Column, String> _function = (Column c) -> {
              return c.getName();
            };
            String _join = IterableExtensions.join(ListExtensions.<Column, String>map(fkey.getColumns(), _function), ", ");
            _builder.append(_join);
            _builder.append(") REFERENCES ");
            String _name = fkey.getRefsTo().getOwner().getName();
            _builder.append(_name);
            _builder.append(" (");
            final Function1<Column, String> _function_1 = (Column c) -> {
              return c.getName();
            };
            String _join_1 = IterableExtensions.join(ListExtensions.<Column, String>map(fkey.getRefsTo().getColumns(), _function_1), ", ");
            _builder.append(_join_1);
            _builder.append(") ON DELETE ");
            ReferentialAction _onDelete = fkey.getOnDelete();
            _builder.append(_onDelete);
            _builder.append(" ON UPDATE ");
            ReferentialAction _onUpdate = fkey.getOnUpdate();
            _builder.append(_onUpdate);
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder.toString();
  }

  private String formatDatatype(final Column column) {
    String _datatype = column.getDatatype();
    if (_datatype != null) {
      switch (_datatype) {
        case "VARCHAR":
          String _datatype_1 = column.getDatatype();
          String _plus = (_datatype_1 + "(");
          int _size = column.getSize();
          String _plus_1 = (_plus + Integer.valueOf(_size));
          return (_plus_1 + ")");
        default:
          return column.getDatatype();
      }
    } else {
      return column.getDatatype();
    }
  }
}
