package ru.chirikhin.oracle_client.view


data class ColumnProperties(val name : String, val notNull : Boolean,
                            val isPrimaryKey : Boolean,
                            val isUnique : Boolean,
                            val foreignKeys : List<String>)