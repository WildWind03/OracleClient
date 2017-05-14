package ru.chirikhin.oracle_client.model


data class Column(var isNotNull : Boolean,
                  var type : String,
                  var name : String) {
    override fun toString(): String {
        if (isNotNull) {
            return "$name (TYPE: $type, NOT NULL)"
        }

        return "$name (TYPE: $type)"
    }
}