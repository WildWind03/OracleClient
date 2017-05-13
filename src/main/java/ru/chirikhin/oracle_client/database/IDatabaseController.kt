package ru.chirikhin.oracle_client.database

import tornadofx.Controller

abstract class IDatabaseController : Controller() {
    abstract fun getTablespaces() : List<String>?
    abstract fun getTableNames(tablespace : String) : List<String>?
    abstract fun getColumnNames(tableName: String) : List<String>?
    abstract fun getRecords(tableName: String) : List<List<String>>?
    abstract fun connect(ip : String, port : String, username : String, password : String)
}