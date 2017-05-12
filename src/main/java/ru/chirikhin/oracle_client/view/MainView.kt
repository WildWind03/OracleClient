package ru.chirikhin.oracle_client.view

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.showErrorAlert
import tornadofx.*
import java.sql.SQLException


class MainView : View() {
    override val root = BorderPane()

    var tableView: TableView<List<String>>? = null
    val databaseRepresentation : DatabaseRepresentation = DatabaseRepresentation()

    private val MAIN_VIEW_TITLE = "Oracle Client"
    private val TABLESPACES = "Tablespaces"
    private val ADD_NEW_ITEM = "Add new table"

    private val databaseController = DatabaseController

    init {
        title = MAIN_VIEW_TITLE
        with(primaryStage) {
            isMaximized = true
        }

        with(root) {
            left = treeview<String> {
                selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    val treeItem = newValue

                    try {
                        if (newValue.isLeaf) {
                            val types = databaseController.getColumnNames(treeItem.value)

                            if (null != types && types.isNotEmpty()) {
                                tableView = TableView<List<String>>().apply {
                                    items = databaseController.getRecords(treeItem.value).observable()

                                    for (k in 0..types.size - 1) {
                                        column<List<String>, String>(types[k]) {
                                            ReadOnlyObjectWrapper(it.value[k])
                                        }
                                    }
                                }
                            } else {
                                tableView = null
                            }
                        }
                    } catch (e : SQLException) {
                        showErrorAlert(e.localizedMessage)
                    }

                    center = tableView
                }

                root = TreeItem(TABLESPACES)

                try {
                    val tablespaces = databaseController.getTablespaces()
                    databaseRepresentation.addTablespaces(tablespaces)

                    for (tablespace in tablespaces) {
                        val tablespaceItem = TreeItem(tablespace)
                        root.children.add(tablespaceItem)

                        val tables = databaseController.getTableNames(tablespace)
                        databaseRepresentation.addTables(tablespace, tables)

                        tables.forEach {
                            val tableConstraints = databaseController.getConstraints(tablespace, it)
                            val constraintMap = HashMap<String, Constraint>()
                            tableConstraints.forEach { constraintMap.put(it.name, it) }
                            databaseRepresentation.getTable(tablespace, it).setConstraints(constraintMap)
                        }

                        tablespaceItem.children.apply {
                            tables.forEach {
                                add(TreeItem(it))
                            }
                        }
                    }
                } catch (e : SQLException) {
                    showErrorAlert(e.localizedMessage)
                }

                contextmenu {
                    menuitem(ADD_NEW_ITEM) {
                        NewTableView(selectedValue).openModal()
                    }
                }
            }
        }
    }

}