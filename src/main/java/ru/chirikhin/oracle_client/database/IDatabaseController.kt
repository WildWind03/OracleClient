package ru.chirikhin.oracle_client.database

import ru.chirikhin.oracle_client.model.Column
import tornadofx.Controller

abstract class IDatabaseController : Controller() {
    abstract fun getTablespaces() : List<String>?
    abstract fun getTableNames(tablespace : String) : List<String>?
    abstract fun getColumns(tableName: String) : List<Column>?
    abstract fun getRecords(tableName: String) : List<List<String>>?
    abstract fun connect(ip : String, port : String, username : String, password : String)
    abstract fun getConstraints(tables: List<String>): HashMap<String, ArrayList<Constraint>>
}