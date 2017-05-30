package ru.chirikhin.oracle_client.database

import ru.chirikhin.oracle_client.model.Column
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

    override fun getColumns(tableName: String): List<Column> {
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

    override fun getConstraints(tables : List<String>) : HashMap<String, ArrayList<Constraint>> {
        val constraints = HashMap<String, ArrayList<Constraint>>()

        val foreignKeys = getForeignKeys(tables)
        val primaryKeys = getPrimaryKeys(tables)
        val uniqueKeys = getUniqueConstraints(tables)

        tables.forEach {
            if (null == constraints[it]) {
                constraints[it] = ArrayList()
            }

            constraints[it]?.addAll(foreignKeys[it] ?: ArrayList())
            constraints[it]?.addAll(primaryKeys[it] ?: ArrayList())
            constraints[it]?.addAll(uniqueKeys[it] ?: ArrayList())
        }

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

    fun executeQuery(query : String) {
        val statement = connection?.createStatement() ?: throw NoConnectionException()
        statement.execute(query)
    }

    fun deleteTable(nameOfTable: String) {
        executeQuery("DROP TABLE \"$nameOfTable\"")
    }

    fun deleteRow(nameOfTable: String, columnNames: List<String>, columnValues: List<String>) {
        val deleteRowQueryBuilder = StringBuilder()
        deleteRowQueryBuilder.append("DELETE FROM \"$nameOfTable\" WHERE ")

        for (i in 0..columnNames.size - 1) {
            deleteRowQueryBuilder.append("\"${columnNames[i]}\" = '${columnValues[i]}'")
            if (i <= columnNames.size - 2) {
                deleteRowQueryBuilder.append(" AND ")
            }
        }

        executeQuery(deleteRowQueryBuilder.toString())
    }

    fun updateRow(oldRowValue: List<String>, columnNames: List<String>, columnName: String, newValue: String, nameOfTable: String) {
        val updateRowQueryBuilder = StringBuilder()
        updateRowQueryBuilder.append("UPDATE \"$nameOfTable\" SET $columnName = '$newValue' WHERE ")

        for (i in 0..oldRowValue.size - 1) {
            updateRowQueryBuilder.append("${columnNames[i]} = '${oldRowValue[i]}'")

            if (i < oldRowValue.size - 1) {
                updateRowQueryBuilder.append(" AND ")
            }
        }

        executeQuery(updateRowQueryBuilder.toString())
    }

    fun insertRow(nameOfTable: String, rows: Collection<MyColumn>) {
        val insertRowsQueryBuilder = StringBuilder()
        insertRowsQueryBuilder.append("INSERT INTO \"$nameOfTable\" (")

        for ((index, value) in rows.withIndex()) {
            insertRowsQueryBuilder.append("\"${value.columnName}\"")

            if (index < rows.size - 1) {
                insertRowsQueryBuilder.append(",")
            }
        }

        insertRowsQueryBuilder.append(") VALUES (")

        for ((index, value) in rows.withIndex()) {
            insertRowsQueryBuilder.append("'${value.columnValue}'")

            if (index < rows.size - 1) {
                insertRowsQueryBuilder.append(",")
            }
        }

        insertRowsQueryBuilder.append(")")

        executeQuery(insertRowsQueryBuilder.toString())
    }

    fun deleteColumn(nameOfTable: String, columnName: String) {
        val query = "ALTER TABLE \"$nameOfTable\" DROP COLUMN \"$columnName\""
        executeQuery(query)
    }

    fun dropConstraint(nameOfTable: String, constraintName: String) {
        executeQuery("ALTER TABLE \"$nameOfTable\" DROP CONSTRAINT \"$constraintName\"")
    }

    fun addConstraint(nameOfTable: String, constraint: Constraint) {
        when(constraint) {
            is Constraint.PrimaryKey -> {
                val primaryKeys = ArrayList(getPrimaryKeys(listOf(nameOfTable))[nameOfTable])

                if (primaryKeys.isNotEmpty()) {
                    dropConstraint(nameOfTable, constraint.name)
                }

                primaryKeys.add(constraint)
                val queryBuilder = StringBuilder()

                queryBuilder.append("ALTER TABLE \"$nameOfTable\" ADD CONSTRAINT \"${constraint.name}\" PRIMARY KEY (")

                for (i in 0..primaryKeys.size - 1) {
                    val primaryKey : Constraint.PrimaryKey = primaryKeys[i] as Constraint.PrimaryKey
                    queryBuilder.append("\"${primaryKey.columnName}\"")

                    if (i < primaryKeys.size - 1) {
                        queryBuilder.append(",")
                    }
                }

                queryBuilder.append(")")

                executeQuery(queryBuilder.toString())
            }

            is Constraint.UniqueConstraint -> {
                executeQuery("ALTER TABLE \"$nameOfTable\" ADD CONSTRAINT \"${constraint.name}\" UNIQUE (\"${constraint.columnName}\")")
            }

            is Constraint.ForeignKey -> {
                executeQuery("ALTER TABLE \"$nameOfTable\" ADD CONSTRAINT \"${constraint.name}\"" +
                        " FOREIGN KEY (\"${constraint.srcColumn}\") REFERENCES \"${constraint.destTable}\" (\"${constraint.destColumn}\")")
            }
        }
    }

    fun addNewColumn(nameOfTable: String, column: Column) {
        val query = StringBuilder("ALTER TABLE \"$nameOfTable\" ADD \"${column.name}\" ${column.type} ")
        if (column.isNotNull) {
            query.append("NOT NULL")
        }
        executeQuery(query.toString())
    }

    fun executeSelectQuery(query: String) : ArrayList<ArrayList<String>> {
        val recordsList : ArrayList<ArrayList<String>> = ArrayList()
        val statement = connection?.createStatement() ?: throw NoConnectionException()

        val rc = statement.executeQuery(query)

        val columnCount = rc.metaData.columnCount

        val names = ArrayList<String>()
        (1..columnCount).mapTo(names) {
            rc.metaData.getColumnName(it)
        }
        recordsList.add(names)

        while(rc.next()) {
            val record : ArrayList<String> = ArrayList()
            (1..columnCount).mapTo(record) {
                rc.getString(it) ?: "null"
            }

            recordsList.add(record)
        }

        return recordsList
    }

}