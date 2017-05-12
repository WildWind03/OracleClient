package ru.chirikhin.oracle_client.model

sealed class Type {
    data class NUMBER (val precision : Int, val scale : Int) : Type()
    data class VARCHAR2 (val length : Int) : Type()
    class DATE : Type()
}