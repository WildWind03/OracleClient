package ru.chirikhin.oracle_client.database


object DatabaseControllerMock : IDatabaseController() {
    override fun getColumnNames(tablename: String): List<String>? {
        if (tablename == "SCHOOL") {
            return listOf("NUMBER", "ADDRESS", "TYPE")
        }

        if (tablename == "TEACHER") {
            return listOf("ID", "NAME", "AGE")
        }

        return null
    }

    override fun getRecords(tablename: String): List<List<String>> {
        when (tablename) {
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