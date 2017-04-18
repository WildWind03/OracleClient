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

    private val TABLES = "Tables"

    init {
        val tables = databaseController.getTableNames("Any");

        with(root) {
            left = treeview<String> {
                selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
                    val treeItem = newValue
                    tableView = TableView<List<String>>().apply {
                        val types = databaseController.getColumnNames(treeItem.value)
                        items = databaseController.getRecords(treeItem.value).observable()

                        types.forEach(::println)

                        for (k in 0..types.size - 1) {
                            column<List<String>, String>(types[k]) {
                                ReadOnlyObjectWrapper(it.value[k])
                            }
                        }
                    }

                    center = tableView
                }
                root = TreeItem(TABLES)

                root.children.apply {
                    tables.forEach {
                        add(TreeItem(it))
                    }
                }
            }



//            tableView = TableView<List<String>>().apply {
//                isEditable = false
//                items = databaseController.getRecords(tables[0]).observable()
//
//                val types = databaseController.getColumnNames(tables[0])
//                for (k in 0..types.size - 1) {
//                    column<List<String>, String>(types[k]) {
//                        ReadOnlyObjectWrapper(it.value[k])
//                    }
//                }
//            }
//
//            center = tableView
        }
    }

}