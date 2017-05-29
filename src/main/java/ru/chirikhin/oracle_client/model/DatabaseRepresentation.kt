package ru.chirikhin.oracle_client.model

import com.sun.javafx.collections.ObservableMapWrapper
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import tornadofx.observable


abstract class DatabaseRepresentation {
    private val tablespaces : ObservableMap<String, ObservableList<Table>>
            = ObservableMapWrapper<String, ObservableList<Table>> (HashMap())

    fun addTablespaces(tablespaces: Collection<String>) {
        tablespaces.forEach {
            this.tablespaces.put(it, ArrayList<Table>().observable())
        }
    }

    abstract fun getRecords(tableName: String): List<List<String>>

    abstract fun deleteRow(nameOfTable: String, columnNames: List<String>, columnValues: List<String>)

    abstract fun updateRow(oldRowValue: List<String>?, columnNames: List<String>, columnName: String,
                           newValue: String, nameOfTable: String)

    fun deleteTable(tablespace: String, nameOfTable : String) {
        val tablesOfCurrentTablespace = tablespaces[tablespace]

        if (null != tablesOfCurrentTablespace) {
            tablesOfCurrentTablespace.removeIf {
                it.name == nameOfTable
            }
        } else {
            throw NoSuchTablespaceException()
        }

        deleteTableInDatabase(nameOfTable)
    }

    abstract fun deleteTableInDatabase(nameOfTable: String)

    fun addTable(tablespace: String, table : Table) {
        val myTablespace = tablespaces[tablespace]
        if (null != myTablespace) {
            myTablespace.add(table)
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

   fun getTables(tablespace : String) : ObservableList<Table> {
        return tablespaces[tablespace] ?: throw NoSuchTablespaceException()
    }

    fun getTable(tablespace : String, nameOfTable : String) : Table {
        val tables = getTables(tablespace)

        tables.forEach {
            if (it.name == nameOfTable) {
                return it
            }
        }

        throw NoSuchTableException()
    }

    fun getColumnNames(tablespace: String, tableName : String) : List<String> {
        return  getTable(tablespace, tableName).getColumnNames()
    }
}