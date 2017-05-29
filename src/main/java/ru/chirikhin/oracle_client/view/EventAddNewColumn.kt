package ru.chirikhin.oracle_client.view

import ru.chirikhin.oracle_client.model.Column
import tornadofx.FXEvent


data class EventAddNewColumn(val nameOfTable : String, val column : Column) : FXEvent()