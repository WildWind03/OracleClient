package ru.chirikhin.oracle_client.database

import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.Type
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


object DatabaseController : IDatabaseController() {
    var connection : Connection? = null

    override fun getTablespaces(): List<String> {
        val tablespaceList : ArrayList<String> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()
        statement.executeQuery("select TABLESPACE_NAME from USER_TABLESPACES").use {
            while(it.next()) {
                tablespaceList.add(it.getString(1))
            }
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

    override fun getColumns(tableName: String): List<Column>? {
        val columnList : ArrayList<Column> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()
        statement.executeQuery("SELECT column_name, data_type, data_precision, data_scale," +
                " nullable, char_length" +
                " FROM USER_TAB_COLUMNS" +
                " WHERE table_name = '$tableName'").use {

            while (it.next()) {
                columnList.add(Column(it.getString(5) != "Y", it.getString(2), it.getString(1)))

            }
        }

        return columnList
    }

    private fun getUniqueConstraints(tableName: String) : ArrayList<Constraint.UniqueConstraint> {
        val uniqueConstraint = ArrayList<Constraint.UniqueConstraint>()

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        statement.executeQuery("SELECT constraint_name, column_name FROM " +
                "((SELECT constraint_name as cn, constraint_type from user_constraints) inner join user_cons_columns ON cn = CONSTRAINT_NAME)" +
                " WHERE table_name = '$tableName' AND constraint_type = 'U'").use {
            while(it.next()) {
                uniqueConstraint.add(Constraint.UniqueConstraint(it.getString(1),
                        it.getString(2)))
            }
        }

        return uniqueConstraint
    }

    private fun getForeignKeys(tables : List<String>) : HashMap<String, ArrayList<Constraint>> {
        val constraints = HashMap<String, ArrayList<Constraint>>()

        if (tables.isEmpty()) {
            return constraints
        }

        tables.forEach {
            constraints.put(it, ArrayList())
        }

        val queryBuilder = StringBuilder()
        queryBuilder.append("select src_cc.owner as src_scheme, src_cc.table_name as src_table," +
                " src_cc.column_name as src_column, dest_cc.owner as dest_scheme, dest_cc.table_name as dest_table," +
        " dest_cc.column_name as dest_column, c.constraint_name" +
                " from all_constraints c inner join all_cons_columns dest_cc " +
                "on c . r_constraint_name = dest_cc.constraint_name and c . r_owner = dest_cc.owner" +
                " inner join all_cons_columns src_cc on c . constraint_name = src_cc.constraint_name and c . owner = src_cc . owner" +
                " where c . constraint_type = 'R' AND (")

        for (i in 0..tables.size - 1) {
            if (i < tables.size - 1) {
                queryBuilder.append("dest_cc.table_name = '${tables[i]}' OR ")
            } else {
                queryBuilder.append("dest_cc.table_name = '${tables[i]}')")
            }
        }

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        statement.executeQuery(queryBuilder.toString()).use {
            while (it.next()) {
                val table = it.getString(2)
                constraints[table]?.add(Constraint.ForeignKey(it.getString(7),
                        it.getString(3), it.getString(5), it.getString(6)))

            }
        }

        return constraints
    }

    private fun getPrimaryKeys(tables : List<String>) : HashMap<String, ArrayList<Constraint>> {
        val constraints = HashMap<String, ArrayList<Constraint>>()

        if (tables.isEmpty()) {
            return constraints
        }
        tables.forEach {
            constraints.put(it, ArrayList())
        }

        val queryBuilder = StringBuilder()

        queryBuilder.append("SELECT constraint_name, column_name, table_name FROM " +
                "((SELECT constraint_name as cn, constraint_type from user_constraints) inner join user_cons_columns ON cn = CONSTRAINT_NAME)" +
                " WHERE constraint_type = 'P' AND (")

        for (i in 0..tables.size - 1) {
            if (i < tables.size - 1) {
                queryBuilder.append("table_name = '${tables[i]}' OR ")
            } else {
                queryBuilder.append("table_name = '${tables[i]}')")
            }
        }

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        statement.executeQuery(queryBuilder.toString()).use {
            while (it.next()) {
                val table = it.getString(3)
                constraints[table]?.add(Constraint.PrimaryKey(it.getString(1), it.getString(2)))
            }
        }

        return constraints
    }

    private fun getUniqueConstraints(tables : List<String>) : HashMap<String, ArrayList<Constraint>> {
        val constraints = HashMap<String, ArrayList<Constraint>>()

        if (tables.isEmpty()) {
            return constraints
        }

        tables.forEach {
            constraints.put(it, ArrayList())
        }

        val queryBuilder = StringBuilder()

        queryBuilder.append("SELECT constraint_name, column_name, table_name FROM " +
                "((SELECT constraint_name as cn, constraint_type from user_constraints) inner join user_cons_columns ON cn = CONSTRAINT_NAME)" +
                " WHERE constraint_type = 'U' AND (")

        for (i in 0..tables.size - 1) {
            if (i < tables.size - 1) {
                queryBuilder.append("table_name = '${tables[i]}' OR ")
            } else {
                queryBuilder.append("table_name = '${tables[i]}')")
            }
        }

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        statement.executeQuery(queryBuilder.toString()).use {
            while (it.next()) {
                val table = it.getString(3)
                constraints[table]?.add(Constraint.UniqueConstraint(it.getString(1), it.getString(2)))
            }
        }

        return constraints
    }

    private fun getForeignKeys(tableName: String) : ArrayList<Constraint.ForeignKey> {
        val foreignKeys = ArrayList<Constraint.ForeignKey>()

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        statement.executeQuery("select src_cc.owner as src_scheme, src_cc.table_name as src_table," +
                " src_cc.column_name as src_column, dest_cc.owner as dest_scheme, dest_cc.table_name as dest_table," +
                " dest_cc.column_name as dest_column, c.constraint_name" +
                " from all_constraints c inner join all_cons_columns dest_cc " +
                "on c . r_constraint_name = dest_cc.constraint_name and c . r_owner = dest_cc.owner" +
                " inner join all_cons_columns src_cc on c . constraint_name = src_cc.constraint_name and c . owner = src_cc . owner" +
                " where c . constraint_type = 'R' and dest_cc.table_name = \"$tableName\"").use {
            while(it.next()) {
                foreignKeys.add(Constraint.ForeignKey(it.getString(7),
                        it.getString(3), it.getString(5), it.getString(6)))
            }
        }

        return foreignKeys;
    }

    private fun getPrimaryKeys(tableName: String) : ArrayList<Constraint.PrimaryKey> {
        val primaryKeys = ArrayList<Constraint.PrimaryKey>()

        val statement = connection?.createStatement() ?: throw NoConnectionException()

        val rc = statement.executeQuery("SELECT constraint_name, column_name FROM " +
                "((SELECT constraint_name as cn, constraint_type from user_constraints) inner join user_cons_columns ON cn = CONSTRAINT_NAME)" +
                " WHERE table_name = \"$tableName\" AND constraint_type = \"P\"")

        while(rc.next()) {
            primaryKeys.add(Constraint.PrimaryKey(rc.getString(1), rc.getString(2)))
        }

        return primaryKeys

    }

    fun getConstraints(tables : List<String>) : HashMap<String, ArrayList<Constraint>> {
        val constraints = HashMap<String, ArrayList<Constraint>>()
        constraints.putAll(getForeignKeys(tables))
        constraints.putAll(getPrimaryKeys(tables))
        constraints.putAll(getUniqueConstraints(tables))

        return constraints
    }

    fun getConstraints(tableName: String) : ArrayList<Constraint> {
        val constraints : ArrayList<Constraint> = ArrayList()

        constraints.addAll(getForeignKeys(tableName))
        constraints.addAll(getPrimaryKeys(tableName))
        constraints.addAll(getUniqueConstraints(tableName))

        return constraints
    }

    override fun getRecords(tableName: String): List<List<String>> {
        val recordsList : ArrayList<ArrayList<String>> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()

        val rc = statement.executeQuery("SELECT * FROM  \"$tableName\"")

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

    fun createTableWithQuery(query : String) {
        val statement = connection?.createStatement() ?: throw NoConnectionException()

        statement.execute(query)
    }

}