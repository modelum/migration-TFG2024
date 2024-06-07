package es.um.dsdm.relational2relationaltext

import relationalschema.RelationalSchema
import java.io.File
import java.io.FileWriter
import relationalschema.Table
import relationalschema.Column

class Relational2RelationalText {

	val static String slash = '/'

	def relational2RelationalText(RelationalSchema relationalSchema, String outputFileUri) {
		new File(outputFileUri.substring(0, outputFileUri.lastIndexOf(slash) + 1)).mkdirs

		var file = new File(outputFileUri)
		file.createNewFile

		val FileWriter fileWriter = new FileWriter(file)
		relationalSchema.tables.forEach [ t |
			fileWriter.append(table2text(t))
		]

		fileWriter.close
	}

	def private table2text(Table table) '''
		CREATE TABLE «table.name»
			«columns(table)»
			«primaryKeys(table)»
			«foreignKeys(table)»
	'''

	/*def private columns(Table table) '''
		«FOR c : table.columns SEPARATOR ','»
			«c.name» «c.datatype»(«c.size»)«if (!c.nullable) ' NOT NULL' else ''»«if (c.defaultvalue != 'NULL') ' DEFAULT ' + c.defaultvalue else ''»
		«ENDFOR»
	'''*/
	
	private def String columns(Table table) '''
		«FOR c : table.columns SEPARATOR ','»
			«c.name» «formatDatatype(c)»«if (!c.nullable) ' NOT NULL' else ''»«if (c.defaultvalue != 'NULL') ' DEFAULT ' + c.defaultvalue else ''»
		«ENDFOR»
	'''

	private def String primaryKeys(Table table) '''
		«IF table.keys.filter[isPK].size > 0»
			«FOR k : table.keys.filter[isPK] SEPARATOR ','»
				PRIMARY KEY («k.columns.map[c | c.name].join(', ')»)
			«ENDFOR»
		«ENDIF»
	'''

	private def String foreignKeys(Table table) '''
		«IF table.fks.size > 0»
			«FOR fkey : table.fks SEPARATOR ','»
				CONSTRAINT «fkey.constraintname» FOREIGN KEY («fkey.columns.map[c | c.name].join(', ')») REFERENCES «fkey.refsTo.owner.name» («fkey.refsTo.columns.map[c | c.name].join(', ')») ON DELETE «fkey.onDelete» ON UPDATE «fkey.onUpdate»
			«ENDFOR»
		«ENDIF»
	'''
	
	private def String formatDatatype(Column column) {
		switch column.datatype {
			case "VARCHAR":
				return column.datatype + '(' + column.size + ')'
			default:
				return column.datatype
		}
	}
}
