package ru.chirikhin.oracle_client.view

import ru.chirikhin.oracle_client.database.Constraint
import tornadofx.FXEvent


data class EventAddConstraint(val nameOfTable : String, val constraint: Constraint) : FXEvent()