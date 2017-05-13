package ru.chirikhin.oracle_client.model

import ru.chirikhin.oracle_client.database.Constraint


class Table {
    private var rows : ArrayList<ArrayList<String>>? = ArrayList()
    private val columns: HashMap<String, Column> = HashMap()
    private var constraints : HashMap<String, Constraint> = HashMap()

    fun addColumn(name : String, column: Column) {
        columns.put(name, column)
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
        columns?.forEach {
            addColumn(it.name, it)
        }
    }

    fun getColumnNames() : List<String> {
        return columns.keys.toList()
    }

    fun setRows(rows : ArrayList<ArrayList<String>>?) {
        this.rows = rows
    }

    fun clearConstraints() {
        constraints.clear()
    }
}
