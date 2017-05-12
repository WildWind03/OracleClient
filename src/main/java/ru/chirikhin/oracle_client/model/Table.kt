package ru.chirikhin.oracle_client.model

import ru.chirikhin.oracle_client.database.Constraint


class Table {
    private var rows : ArrayList<ArrayList<String>>? = null
    private val rowMetaInformation : HashMap<String, Column> = HashMap()
    private var constraints : HashMap<String, Constraint> = HashMap()

    fun addColumn(name : String, column: Column) {
        rowMetaInformation.put(name, column)
    }

    fun getColumn(name : String) : Column {
        return rowMetaInformation[name] ?: throw NoSuchColumnException()
    }

    fun deleteColumn(name : String) {
        rowMetaInformation.remove(name)
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

    fun clearConstraints() {
        constraints.clear()
    }
}
