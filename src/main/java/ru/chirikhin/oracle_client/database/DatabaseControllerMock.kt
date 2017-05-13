package ru.chirikhin.oracle_client.database

import ru.chirikhin.oracle_client.model.Column


object DatabaseControllerMock : IDatabaseController() {
    override fun getColumns(tableName: String): List<Column>? {
        return null
    }

    override fun getRecords(tableName: String): List<List<String>> {
        when (tableName) {
            "SCHOOL" -> {
                return listOf(listOf("197", "Dovatora street", "Common"), listOf("59", "Dovatora st.", "Common"));
            }
            "TEACHER" -> {
                return listOf(listOf("1", "Catherine Ivanovna", "50"), listOf("2", "Andrew Urevich", "45"))
            }
        }

        return listOf(listOf("ERROR"));
    }

    override fun getTablespaces(): List<String> {
        return listOf("USER", "SYSTEM")
    }

    override fun getTableNames(tablespace: String): List<String>? {
        when (tablespace) {
            "USER" -> return listOf("SCHOOL", "TEACHER")
        }
        return null;
    }

    override fun connect(ip: String, port: String, username: String, password: String) {

    }
}