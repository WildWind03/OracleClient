package ru.chirikhin.oracle_client.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


object DatabaseController : IDatabaseController() {
    var connection : Connection? = null

    override fun getTablespaces(): List<String> {

        val tablespaceList : ArrayList<String> = ArrayList()
        val statement = connection?.createStatement()
        val rc : ResultSet? = statement?.executeQuery("select TABLESPACE_NAME from USER_TABLESPACES")

        if (null != rc) {
            while(rc.next()) {
                tablespaceList.add(rc.getString(1))
            }
        }

    return tablespaceList
    }

    override fun getTableNames(tablespace: String): List<String> {
        val tableList : ArrayList<String> = ArrayList()
        val statement = connection?.createStatement()
        val rc : ResultSet? = statement?.executeQuery("select table_name from user_tables where tablespace_name = '$tablespace'")

        if (null != rc) {
            while(rc.next()) {
                tableList.add(rc.getString(1))
            }
        }
        return tableList
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getColumnNames(tablename: String): List<String>? {
        return listOf("hey")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRecords(tablename: String): List<List<String>> {
        return listOf(listOf("hey"))
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connect(ip: String, port: String, username: String, password: String) {
        connection =  DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:orcl",
                username,
                password)



//        var statement = connection?.createStatement()
//
//        var rc : ResultSet? = statement?.executeQuery("select user from dual")
//
//        rc?.next()
//        val s : String? = rc?.getString(1)
//
//        println(s)
//
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}