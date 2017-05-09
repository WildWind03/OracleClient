package ru.chirikhin.oracle_client.model


class Table {
    private var rows : ArrayList<ArrayList<String>>? = null
    private val rowMetaInformation : HashMap<String, Column> = HashMap()

    fun addColumn(name : String, column: Column) {
        rowMetaInformation.put(name, column)
    }

    fun getColumn(name : String) : Column {
        return rowMetaInformation[name] ?: throw NoSuchColumnException()
    }

    fun deleteColumn(name : String) {
        rowMetaInformation.remove(name)
    }
}
