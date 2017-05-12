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

    private fun getForeignKeys(tablespace: String, tablename: String) : ArrayList<Constraint.ForeignKey> {
        val foreignKeys = ArrayList<Constraint.ForeignKey>()

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        val rc = statement.executeQuery("select src_cc.table_name as src_table, src_cc.column_name as src_column, dest_cc.table_name as dest_table, dest_cc.column_name as dest_column, c.constraint_name from all_constraints c inner join all_cons_columns dest_cc on c . r_constraint_name = dest_cc.constraint_name and c . r_owner = dest_cc.owner inner join all_cons_columns src_cc on c . constraint_name = src_cc.constraint_name and c . owner = src_cc . owner where c . constraint_type = 'R' and dest_cc.table_name = 'BUS'")

        while(rc.next()) {
            foreignKeys.add(Constraint.ForeignKey(rc.getString(5), rc.getString(1), rc.getString(2), rc.getString(3), rc.getString(4)))
        }

        return foreignKeys;
    }

    private fun getPrimaryKeys(tablespace: String, tablename: String) : ArrayList<Constraint.PrimaryKey> {
        val primaryKeys = ArrayList<Constraint.PrimaryKey>()

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        val rc = statement.executeQuery("SELECT constraint_name, column_name FROM ((SELECT constraint_name as cn, constraint_type from user_constraints) inner join user_cons_columns ON cn = CONSTRAINT_NAME) WHERE table_name = 'BUS' AND constraint_type = 'P'")

        while(rc.next()) {
            primaryKeys.add(Constraint.PrimaryKey(rc.getString(1), rc.getString(2)))
        }

        return primaryKeys

    }

    fun getConstraints(tablespace: String, tablename: String) : ArrayList<Constraint> {
        val constraints : ArrayList<Constraint> = ArrayList()

        constraints.addAll(getForeignKeys(tablespace, tablename))
        constraints.addAll(getPrimaryKeys(tablespace, tablename))

        return constraints
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