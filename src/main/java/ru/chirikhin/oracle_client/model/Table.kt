package ru.chirikhin.oracle_client.model

import javafx.collections.ObservableList
import ru.chirikhin.oracle_client.database.Constraint


class Table(nameOfTable: String) {
    private var rows : ArrayList<ArrayList<String>>? = ArrayList()
    private val columns: HashMap<String, Column> = HashMap()
    private var constraints : HashMap<String, Constraint> = HashMap()

    var name : String = nameOfTable

    fun addColumn(name : String, column: Column) {
        columns.put(name, column)
    }

    fun getColumns() : HashMap<String, Column> {
        return columns
    }

    fun getColumn(name : String) : Column {
        return columns[name] ?: throw NoSuchColumnException()
    }

    fun deleteColumn(name : String) {
        columns.remove(name)
    }

    fun addConstraint(name : String, constraint: Constraint) {
        constraints.put(name, constraint)
    }

    fun removeConstraint(name : String) {
        constraints.remove(name)
    }

    fun setConstraints(constraints : HashMap<String, Constraint>) {
        this.constraints = constraints
    }

    fun setColumns(columns : List<Column>?) {
        this.columns.clear()
        columns?.forEach {
            addColumn(it.name, it)
        }
    }

    fun getColumnNames() : List<String> {
        val keys = columns.keys
        val keysList = keys.toList()
        return keysList
    }

    fun setRows(rows : ArrayList<ArrayList<String>>?) {
        this.rows = rows
    }

    fun clearConstraints() {
        constraints.clear()
    }

    override fun toString(): String {
        return name
    }

    fun setConstraints(constraints: ObservableList<Constraint>) {
        constraints.forEach {
            this.constraints.put(it.name, it)
        }
    }
}
