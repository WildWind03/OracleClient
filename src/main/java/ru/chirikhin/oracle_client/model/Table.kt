package ru.chirikhin.oracle_client.model

import javafx.collections.ObservableList
import ru.chirikhin.oracle_client.database.Constraint


class Table(nameOfTable: String) {
    private var rows : ArrayList<ArrayList<String>>? = ArrayList()
    private val columns: HashMap<String, Column> = HashMap()
    private var constraints : HashSet<Constraint> = HashSet()

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

    fun removeConstraint(name : String) {
        constraints.removeIf {
            it.name == name
        }
    }

    fun getConstraints() : HashSet<Constraint> {
        return constraints
    }

    fun setColumns(columns : List<Column>?) {
        this.columns.clear()
        columns?.forEach {
            addColumn(it.name, it)
        }
    }

    fun addConstraint(constraint: Constraint) {
        constraints.add(constraint)
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
            this.constraints.add(it)
        }
    }
}
