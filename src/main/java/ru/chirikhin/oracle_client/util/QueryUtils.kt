package ru.chirikhin.oracle_client.util

import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.model.Column

class CreateQueryException(reason : String) : Exception(reason)

fun createNewTableQueryFromData(tablespace : String, tableName : String, columns : List<Column>,
                                constraints : List<Constraint>) : String {
    if (columns.isEmpty()) {
        throw CreateQueryException("A table must contain one column at least")
    }

    val query : StringBuilder = StringBuilder()

    query.append("CREATE TABLE \"$tableName\" (")

    for (i in 0..columns.size - 1) {
        query.append("\"${columns[i].name}\" ${columns[i].type} ")
        if (columns[i].isNotNull) {
            query.append("NOT NULL")
        }

        query.append(",")
    }


    constraints.filter {it is Constraint.PrimaryKey}.run {
        if (size == 0) {
            return@run
        }
        val primaryKeyConstraintBuilder : StringBuilder = StringBuilder()
        primaryKeyConstraintBuilder.append("CONSTRAINT \"${this[0].name}\" PRIMARY KEY (")
        forEach {
            primaryKeyConstraintBuilder.append("\"${(it as Constraint.PrimaryKey).columnName}\",")
        }

        primaryKeyConstraintBuilder.replace(primaryKeyConstraintBuilder.length - 1, primaryKeyConstraintBuilder.length, "")
        primaryKeyConstraintBuilder.append("),")
        println(primaryKeyConstraintBuilder.toString())
        query.append(primaryKeyConstraintBuilder.toString())
    }

    constraints.forEach {
        when(it) {
            is Constraint.ForeignKey -> {
                query.append("CONSTRAINT \"${it.name}\" FOREIGN KEY (\"${it.srcColumn}\")" +
                        " REFERENCES \"${it.destTable}\"(\"${it.destColumn}\"),")
            }

            is Constraint.UniqueConstraint -> {
                query.append("CONSTRAINT \"${it.name}\" UNIQUE (\"${it.columnName}\"),")
            }
        }
    }

    query.append(") TABLESPACE $tablespace")

    val lastCommaIndex = query.lastIndexOf(",")

    if (-1 != lastCommaIndex) {
        query.replace(lastCommaIndex, lastCommaIndex + 1, "")
    }

    val readyQuery = query.toString()

    return readyQuery
}