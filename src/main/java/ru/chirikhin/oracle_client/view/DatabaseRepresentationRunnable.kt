package ru.chirikhin.oracle_client.view

import ru.chirikhin.oracle_client.model.DatabaseRepresentation


interface DatabaseRepresentationRunnable {
    fun run(databaseRepresentation : DatabaseRepresentation)
}