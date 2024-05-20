package juanfran.um.trace

import java.util.TreeMap
import java.util.List
import java.util.Arrays
import java.util.ArrayList
import java.util.SortedMap
import java.util.Set
import java.util.HashSet

class Trace {
	//T_sourceElement (destination, sources)->devuelve el/los elementos origen del elemento destino
	//T_targetElement (origin, targets)->devuelve el/los elementos destino del elemento origen
	SortedMap <String,List<String>> T_sourceElement=new TreeMap<String,List<String>>(); 
	SortedMap <String,List<String>> T_targetElement=new TreeMap<String,List<String>>();
	SortedMap <String,List<Object>> T_sourceInstance=new TreeMap<String,List<Object>>();
	SortedMap <String,List<Object>> T_targetInstance=new TreeMap <String,List<Object>>();
	
	//Si es la primera vez que se añade a esa key crea la lista y si no, lo añade a la lista
	def void addTrace(String sourceElement, Object sourceInstance, String targetElement, Object targetInstance) { 
			//--Destinos a partir del origen
			//String
			var List<String> auxTargetE=T_targetElement.get(sourceElement)
			if (auxTargetE===null)  
				T_targetElement.put(sourceElement, new ArrayList<String>(Arrays.asList(targetElement)))
			else auxTargetE.add(targetElement)
			//objetos
		    var List<Object> auxTargetI=T_targetInstance.get(sourceElement)
		    if (auxTargetI===null)  
				T_targetInstance.put(sourceElement, new ArrayList<Object> (Arrays.asList(targetInstance)))
			else auxTargetI.add(targetInstance)
		    
		    //--Origen a partir del destino (le pasas el targetElement y obtienes el source string u objeto
		    var List<String> auxSourceE=T_sourceElement.get(targetElement)
			if (auxSourceE===null)  
				T_sourceElement.put(targetElement, new ArrayList<String> (Arrays.asList(sourceElement)))
			else auxSourceE.add(sourceElement)
			//objetos
		    var List<Object> auxSourceI=T_sourceInstance.get(targetElement)
		    if (auxSourceI===null)  
				T_sourceInstance.put(targetElement, new ArrayList<Object>(Arrays.asList(sourceInstance)))
			else auxSourceI.add(sourceInstance)
	}
	
	def List<String> getSourceElement(String target) {
		return T_sourceElement.get(target)
	}
	def List<Object> getSourceInstance(String target) {
		return T_sourceInstance.get(target)
	}
	def List<String> getTargetElement(String source) {
		return T_targetElement.get(source)
	}
	def List<Object> getTargetInstance(String source) {
		return T_targetInstance.get(source)
	}
	
	def String getType(Object o) {
		o.class.interfaces.head.name
	}
	
	def SortedMap<String,List<String>> getDirectTraceString() { T_targetElement }
	
	def void printDirectTraceTypes(){
		var SortedMap<String,List<String>> resultado=T_targetElement
		var Set<String> entradas=resultado.keySet
		var iterador=entradas.iterator
		System.out.println
		System.out.println("Traza")
		System.out.println("-----")
		while(iterador.hasNext){
			var String entrada=iterador.next
			var List<String> salida=resultado.get(entrada)
			
			val salidaTipos = this.getTargetInstance(entrada).map[ obj |
				this.getType(obj)
			]
			val entradaTipos = new HashSet<String>
			for (e : salida) {
				for ( obj : this.getSourceInstance(e)) {
					entradaTipos.add(getType(obj))
				}
			}

			var String auxSal=String.join(", ", salida)+" ("+String.join(", ", salidaTipos)+")"
			entrada=entrada+"("+String.join(", ", entradaTipos)+")"
			System.out.println(String::format("%70s",entrada)+" -> "+auxSal)
		}
	}

}