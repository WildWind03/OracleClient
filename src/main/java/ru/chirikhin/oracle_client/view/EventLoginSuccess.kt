package ru.chirikhin.oracle_client.view

import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import tornadofx.FXEvent


data class EventLoginSuccess(val databaseRepresentation: DatabaseRepresentation) : FXEvent()