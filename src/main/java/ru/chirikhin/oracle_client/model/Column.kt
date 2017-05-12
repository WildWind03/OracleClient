package ru.chirikhin.oracle_client.model


data class Column(var isNotNull : Boolean,
                  var isUnique : Boolean,
                  var type : Type)