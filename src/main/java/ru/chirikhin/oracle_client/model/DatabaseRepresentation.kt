package ru.chirikhin.oracle_client.model


class DatabaseRepresentation {
    val tablespaces : HashMap<String, HashMap<String, Table?>?> = HashMap()

    fun addTablespaces(tablespaces: Collection<String>) {
        tablespaces.forEach {
            this.tablespaces.put(it, null);
        }
    }

    fun addTables(tablespace: String, tables: Collection<String>) {
        tables.forEach {
            tablespaces[tablespace]?.put(it, Table())
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

    private fun getTablespaces() : List<String> {
        return tablespaces.keys.toTypedArray().asList()
    }

    private fun getTables(tablespace : String) : HashMap<String, Table?> {
        return tablespaces[tablespace] ?: throw NoSuchTablespaceException()
    }

    fun getTable(tablespace : String, nameOfTable : String) : Table {
        return getTables(tablespace).get(nameOfTable) ?: throw NoSuchTableException()
    }
}