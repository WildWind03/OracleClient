package ru.chirikhin.oracle_client.database


sealed class Constraint(val name : String) {
    class PrimaryKey(conName : String, val columnName : String) : Constraint(conName) {
        override fun toString(): String {
            return "$name (type: PRIMARY KEY, column: $columnName)"
        }
    }

    class ForeignKey(conName: String, val srcScheme : String, val srcTable : String, val srcColumn : String, val destTable : String,
                     val destColumn : String, val destScheme : String)
        : Constraint(conName) {
        override fun toString(): String {
            return "$name (type : FOREIGN KEY, src column: $srcColumn, ref_column: $destTable.$destColumn"
        }
    }

    class UniqueConstraint(conName : String, val columnName : String) : Constraint(conName) {
        override fun toString(): String {
            return "$name (type: UNIQUE, column: $columnName)"
        }
    }
}