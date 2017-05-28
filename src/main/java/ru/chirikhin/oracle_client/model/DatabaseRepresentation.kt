package ru.chirikhin.oracle_client.model

import com.sun.javafx.collections.ObservableMapWrapper
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import tornadofx.observable
import java.util.*


class DatabaseRepresentation {
    private val tablespaces : HashMap<String, HashMap<String, Table>?> = HashMap()

    fun addTablespaces(tablespaces: Collection<String>) {
        tablespaces.forEach {
            this.tablespaces.put(it, HashMap<String, Table>())
        }
    }

    fun deleteTable(tablespace: String, nameOfTable : String) {
        val myTablespace = tablespaces[tablespace]
        if (null != myTablespace) {
            myTablespace.remove(nameOfTable)
        } else {
            throw NoSuchTablespaceException()
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

    private fun getTablesMap(tablespace : String) : HashMap<String, Table> {
        return tablespaces[tablespace] ?: throw NoSuchTablespaceException()
    }

    fun getTables(tablespace: String) : ObservableMap<String, Table> {
        return ObservableMapWrapper<String, Table> (getTablesMap(tablespace))
    }

    fun getTable(tablespace : String, nameOfTable : String) : Table {
        return getTablesMap(tablespace)[nameOfTable] ?: throw NoSuchTableException()
    }

    fun getColumnNames(tablespace: String, tableName : String) : List<String> {
        val tables = getTablesMap(tablespace)
        val table = tables[tableName] ?: throw NoSuchTableException()

        return table.getColumnNames()
    }
}