package juanfran.um.trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class Trace {
  private SortedMap<String, List<String>> T_sourceElement = new TreeMap<String, List<String>>();

  private SortedMap<String, List<String>> T_targetElement = new TreeMap<String, List<String>>();

  private SortedMap<String, List<Object>> T_sourceInstance = new TreeMap<String, List<Object>>();

  private SortedMap<String, List<Object>> T_targetInstance = new TreeMap<String, List<Object>>();

  public void addTrace(final String sourceElement, final Object sourceInstance, final String targetElement, final Object targetInstance) {
    List<String> auxTargetE = this.T_targetElement.get(sourceElement);
    if ((auxTargetE == null)) {
      List<String> _asList = Arrays.<String>asList(targetElement);
      ArrayList<String> _arrayList = new ArrayList<String>(_asList);
      this.T_targetElement.put(sourceElement, _arrayList);
    } else {
      auxTargetE.add(targetElement);
    }
    List<Object> auxTargetI = this.T_targetInstance.get(sourceElement);
    if ((auxTargetI == null)) {
      List<Object> _asList_1 = Arrays.<Object>asList(targetInstance);
      ArrayList<Object> _arrayList_1 = new ArrayList<Object>(_asList_1);
      this.T_targetInstance.put(sourceElement, _arrayList_1);
    } else {
      auxTargetI.add(targetInstance);
    }
    List<String> auxSourceE = this.T_sourceElement.get(targetElement);
    if ((auxSourceE == null)) {
      List<String> _asList_2 = Arrays.<String>asList(sourceElement);
      ArrayList<String> _arrayList_2 = new ArrayList<String>(_asList_2);
      this.T_sourceElement.put(targetElement, _arrayList_2);
    } else {
      auxSourceE.add(sourceElement);
    }
    List<Object> auxSourceI = this.T_sourceInstance.get(targetElement);
    if ((auxSourceI == null)) {
      List<Object> _asList_3 = Arrays.<Object>asList(sourceInstance);
      ArrayList<Object> _arrayList_3 = new ArrayList<Object>(_asList_3);
      this.T_sourceInstance.put(targetElement, _arrayList_3);
    } else {
      auxSourceI.add(sourceInstance);
    }
  }

  public List<String> getSourceElement(final String target) {
    return this.T_sourceElement.get(target);
  }

  public List<Object> getSourceInstance(final String target) {
    return this.T_sourceInstance.get(target);
  }

  public List<String> getTargetElement(final String source) {
    return this.T_targetElement.get(source);
  }

  public List<Object> getTargetInstance(final String source) {
    return this.T_targetInstance.get(source);
  }

  public String getType(final Object o) {
    return IterableExtensions.<Class<?>>head(((Iterable<Class<?>>)Conversions.doWrapArray(o.getClass().getInterfaces()))).getName();
  }

  public SortedMap<String, List<String>> getDirectTraceString() {
    return this.T_targetElement;
  }

  public void printDirectTraceTypes() {
    SortedMap<String, List<String>> resultado = this.T_targetElement;
    Set<String> entradas = resultado.keySet();
    Iterator<String> iterador = entradas.iterator();
    System.out.println();
    System.out.println("Traza");
    System.out.println("-----");
    while (iterador.hasNext()) {
      {
        String entrada = iterador.next();
        List<String> salida = resultado.get(entrada);
        final Function1<Object, String> _function = (Object obj) -> {
          return this.getType(obj);
        };
        final List<String> salidaTipos = ListExtensions.<Object, String>map(this.getTargetInstance(entrada), _function);
        final HashSet<String> entradaTipos = new HashSet<String>();
        for (final String e : salida) {
          List<Object> _sourceInstance = this.getSourceInstance(e);
          for (final Object obj : _sourceInstance) {
            entradaTipos.add(this.getType(obj));
          }
        }
        String _join = String.join(", ", salida);
        String _plus = (_join + " (");
        String _join_1 = String.join(", ", salidaTipos);
        String _plus_1 = (_plus + _join_1);
        String auxSal = (_plus_1 + ")");
        String _join_2 = String.join(", ", entradaTipos);
        String _plus_2 = ((entrada + "(") + _join_2);
        String _plus_3 = (_plus_2 + ")");
        entrada = _plus_3;
        String _format = String.format("%70s", entrada);
        String _plus_4 = (_format + " -> ");
        String _plus_5 = (_plus_4 + auxSal);
        System.out.println(_plus_5);
      }
    }
  }
}
