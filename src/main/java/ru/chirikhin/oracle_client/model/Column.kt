package ru.chirikhin.oracle_client.model


data class Column(var isNotNull : Boolean,
                  var type : String,
                  var name : String) {
    override fun toString(): String {
        return "$name (Type = $type, IsNotNull = $isNotNull)"
    }
}