package ru.chirikhin.oracle_client.database


sealed class Constraint(val name : String) {
    class PrimaryKey(conName : String, val columnName : String) : Constraint(conName) {
        override fun toText(): String {
            return "Constraint: $name, Type: Primary Key, Column Name : $columnName"
        }
    }

    class ForeignKey(conName: String, val srcScheme : String, val srcTable : String, val srcColumn : String, val destTable : String,
                     val destColumn : String, val destScheme : String)
        : Constraint(conName) {
        override fun toText(): String {
            return "Constraint: $name, Type : Foreign Key, Source Column : $srcColumn, Destination Table: $destTable, " +
                    "Destination Column: $destColumn"
        }
    }

    class UniqueConstraint(conName : String, val columnName : String) : Constraint(conName) {
        override fun toText(): String {
            return "Constraint: $name, Type: Unique, Column Name : $columnName"
        }
    }

    abstract fun toText() : String
}