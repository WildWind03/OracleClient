package ru.chirikhin.oracle_client.view

import tornadofx.FXEvent


data class EventDeleteColumn(val nameOfTable: String, val nameOfColumn: String) : FXEvent()