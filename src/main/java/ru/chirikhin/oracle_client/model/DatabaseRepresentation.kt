package ru.chirikhin.oracle_client.model

import tornadofx.observable
import java.util.*


class DatabaseRepresentation {
    private val tablespaces : HashMap<String, HashMap<String, Table>?> = HashMap()

    fun addTablespaces(tablespaces: Collection<String>) {
        tablespaces.forEach {
            this.tablespaces.put(it, HashMap<String, Table>())
        }
    }

    fun addTable(tablespace: String, table : Table) {
        val myTablespace = tablespaces[tablespace]
        if (null != myTablespace) {
            myTablespace.put(table.name, table)
        } else {
            throw NoSuchTablespaceException()
        }
    }

    fun addTables(tablespace: String, tables: Collection<String>) {
        tables.forEach {
            addTable(tablespace, Table(it))
        }
    }

    fun addColumn(tablespace: String, nameOfTable: String, columnName : String, column: Column) {
        val table : Table = getTable(tablespace, nameOfTable)
        table.addColumn(columnName, column)
    }

    fun getColumn(tablespace: String, nameOfTable: String, columnName: String) : Column {
        return getTable(tablespace, nameOfTable).getColumn(columnName)
    }

    fun deleteColumn(tablespace: String, nameOfTable: String, columnName: String) {
        getTable(tablespace, nameOfTable).deleteColumn(columnName)
    }

    fun getTablespaces() : List<String> {
        return tablespaces.keys.toTypedArray().asList()
    }

    fun getTables(tablespace : String) : HashMap<String, Table> {
        return tablespaces[tablespace] ?: throw NoSuchTablespaceException()
    }

    fun getTable(tablespace : String, nameOfTable : String) : Table {
        return getTables(tablespace).get(nameOfTable) ?: throw NoSuchTableException()
    }

    fun getColumnNames(tablespace: String, tableName : String) : List<String> {
        val tables = getTables(tablespace)
        val table = tables.get(tableName)
        if (null == table) {
            throw NoSuchTableException()
        }

        return table.getColumnNames()
        //return getTables(tablespace)[tableName]?.getColumnNames() ?: throw NoSuchTableException()
    }
}