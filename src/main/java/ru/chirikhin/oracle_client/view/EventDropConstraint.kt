package ru.chirikhin.oracle_client.view

import tornadofx.FXEvent


data class EventDropConstraint(val nameOfTable : String, val nameOfConstraint : String) : FXEvent()