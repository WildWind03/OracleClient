package ru.chirikhin.oracle_client.view

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import ru.chirikhin.oracle_client.database.DatabaseControllerMock
import tornadofx.*

class MainView : View() {
    override val root = BorderPane()
    var tableView: TableView<List<String>>? = null
    val databaseController = DatabaseControllerMock()

    private val MAIN_VIEW_TITLE = "Oracle Client"
    private val TABLESPACES = "Tablespaces"

    init {
        title = MAIN_VIEW_TITLE
        with(primaryStage) {
            isMaximized = true
        }

        with(root) {
            left = treeview<String> {
                selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
                    val treeItem = newValue
                    val types = databaseController.getColumnNames(treeItem.value)

                    if (null != types) {
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

                    center = tableView
                }

                root = TreeItem(TABLESPACES)
                val tablespaces = databaseController.getTablespaces()

                for (tablespace in tablespaces) {
                    val tablespaceItem = TreeItem(tablespace)
                    root.children.add(tablespaceItem)

                    val tables = databaseController.getTableNames(tablespace)
                    tablespaceItem.children.apply {
                        tables?.forEach {
                            add(TreeItem(it))
                        }
                    }
                }
            }
        }
    }

}