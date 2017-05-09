package ru.chirikhin.oracle_client.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


object DatabaseController : IDatabaseController() {
    var connection : Connection? = null

    override fun getTablespaces(): List<String> {
        val tablespaceList : ArrayList<String> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()
        val rc : ResultSet = statement.executeQuery("select TABLESPACE_NAME from USER_TABLESPACES")

        while(rc.next()) {
            tablespaceList.add(rc.getString(1))
        }

    return tablespaceList
    }

    override fun getTableNames(tablespace: String): List<String> {
        val tableList : ArrayList<String> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()
        val rc : ResultSet = statement.executeQuery("select table_name from user_tables where tablespace_name = '$tablespace'")

        while(rc.next()) {
            tableList.add(rc.getString(1))
        }

        return tableList
    }

    override fun getColumnNames(tablename: String): List<String>? {
        val columnList : ArrayList<String> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()
        val rc : ResultSet = statement.executeQuery("SELECT table_name, column_name, data_type, data_length" +
                " FROM USER_TAB_COLUMNS" +
                " WHERE table_name = '$tablename'")

        while(rc.next()) {
            columnList.add(rc.getString(2))
        }

        return columnList
    }

    override fun getRecords(tablename: String): List<List<String>> {
        val recordsList : ArrayList<ArrayList<String>> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()

        val rc = statement.executeQuery("SELECT * FROM  $tablename")
        val columnCount = rc.metaData.columnCount

        while(rc.next()) {
            val record : ArrayList<String> = ArrayList()
            (1..columnCount).mapTo(record) {
                rc.getString(it) ?: "null"
            }

            recordsList.add(record)
        }

        return recordsList
    }

    override fun connect(ip: String, port: String, username: String, password: String) {
        connection =  DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:orcl",
                username,
                password)
    }

}