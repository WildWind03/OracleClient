package ru.chirikhin.oracle_client.controller

import javafx.application.Platform
import javafx.scene.control.Alert
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.showAlert
import ru.chirikhin.oracle_client.view.EventLoginSuccess
import ru.chirikhin.oracle_client.view.EventLogin
import ru.chirikhin.oracle_client.view.ProgressIndicatorView
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

                    fire(EventLoginSuccess(databaseRepresentation))
                } catch (e: SQLException) {
                    showAlert("Error", "Can not connect to the database", e.message ?:
                            "No additional information", Alert.AlertType.ERROR)
                    return@subscribe
                }
            }
        }
    }
}

