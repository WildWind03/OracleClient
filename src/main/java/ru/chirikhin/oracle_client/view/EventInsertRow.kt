package ru.chirikhin.oracle_client.view

import ru.chirikhin.oracle_client.database.MyColumn
import tornadofx.FXEvent


data class EventInsertRow (val nameOfTable : String, val columns : Collection<MyColumn>) : FXEvent()