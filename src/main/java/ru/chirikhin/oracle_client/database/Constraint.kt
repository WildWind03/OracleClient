package ru.chirikhin.oracle_client.database


sealed class Constraint(val name : String) {
    class PrimaryKey(conName : String, val columnName : String) : Constraint(conName)
    class ForeignKey(conName: String, val srcTable : String, val srcColumn : String, val destTable : String, var destColumn : String)
        : Constraint(conName)
}