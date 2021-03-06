package ru.chirikhin.oracle_client.controller

import javafx.application.Platform
import javafx.scene.control.Alert
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.showAlert
import ru.chirikhin.oracle_client.view.*
import tornadofx.Controller
import tornadofx.ViewTransition
import tornadofx.observable
import tornadofx.seconds
import java.sql.SQLException

class ViewController : Controller() {
    val databaseController = DatabaseController

    init {
        subscribe<EventLogin> {
            it.loginView.apply {
                try {
                    Platform.runLater({
                        replaceWith(ProgressIndicatorView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                    })

                    databaseController.connect(it.ip, it.port, it.username, it.password)


                    fire(EventLoginSuccess(loadDatabaseRepresentation()))
                } catch (e: SQLException) {
                    showAlert("Error", "Can not connect to the database", e.message ?:
                            "No additional information", Alert.AlertType.ERROR)
                    return@subscribe
                }
            }
        }

        subscribe<EventInsertRow> {
            databaseController.insertRow(it.nameOfTable, it.columns)
        }

        subscribe<EventDeleteColumn> {
            databaseController.deleteColumn(it.nameOfTable, it.nameOfColumn)
        }

        subscribe<EventDropConstraint> {
            databaseController.dropConstraint(it.nameOfTable, it.nameOfConstraint)
        }

        subscribe<EventAddConstraint> {
            databaseController.addConstraint(it.nameOfTable, it.constraint)
        }

        subscribe<EventAddNewColumn> {
            databaseController.addNewColumn(it.nameOfTable, it.column)
        }

        subscribe<EventSomeQuery> {
            try {
                databaseController.executeQuery(it.query)
                it.onSuccessAction.run("Successfully executed")
                it.runnable.run(loadDatabaseRepresentation())
            } catch (e : Exception) {
                it.onErrorAction.run(e.toString())
            }
        }

        subscribe<EventSelectQuery> {
            try {
                val values = databaseController.executeSelectQuery(it.query)
                val valuesBuilder = StringBuilder()

                values.forEach {
                    valuesBuilder.append(it.toString() + "\n")
                }
                it.onSuccessAction.run(valuesBuilder.toString())
            } catch (e : Exception) {
                it.onErrorAction.run(e.toString())
            }
        }
    }

    private fun loadDatabaseRepresentation() : DatabaseRepresentation {
        val databaseRepresentation = object : DatabaseRepresentation() {

            override fun updateRow(oldRowValue: List<String>?, columnNames: List<String>, columnName: String, newValue: String, nameOfTable: String) {
                if (null == oldRowValue) {
                    return
                }
                databaseController.updateRow(oldRowValue, columnNames, columnName, newValue, nameOfTable)
            }

            override fun deleteRow(nameOfTable: String, columnNames: List<String>, columnValues: List<String>) {
                databaseController.deleteRow(nameOfTable, columnNames, columnValues)
            }

            override fun deleteTableInDatabase(nameOfTable: String) {
                databaseController.deleteTable(nameOfTable)
            }

            override fun getRecords(tableName: String): List<List<String>> {
                return databaseController.getRecords(tableName)
            }

        }

        val tablespaces = databaseController.getTablespaces()
        databaseRepresentation.addTablespaces(tablespaces)

        tablespaces.forEach {
            val tables = databaseController.getTableNames(it)
            databaseRepresentation.addTables(it, tables)
            val constraints = databaseController.getConstraints(tables)
            for ((key, value) in constraints) {
                databaseRepresentation.getTable(it, key).setConstraints(value.observable())
            }
            for (table in tables) {
                val columns = databaseController.getColumns(table)
                databaseRepresentation.getTable(it, table).setColumns(columns)
            }
        }

        return databaseRepresentation
    }
}

