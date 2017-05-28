package ru.chirikhin.oracle_client.view

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*
import java.sql.SQLException
import java.util.*
import javax.swing.tree.TreeNode


class MainView : View() {
    override val root = BorderPane()

    private var tableView: TableView<List<String>>? = null
    val databaseRepresentation : DatabaseRepresentation by params

    private val MAIN_VIEW_TITLE = "Oracle Client"
    private val TABLESPACES = "Tablespaces"
    private val ADD_NEW_ITEM = "Add new table"

    private var tablespaceItem : TreeItem<String> by singleAssign()

    init {
        title = MAIN_VIEW_TITLE

        with(primaryStage) {
            isMaximized = true
            isResizable = true
        }

        with(root) {
            top = menubar {
                menu("Table") {
                    item(ADD_NEW_ITEM) {
                        setOnAction {
                            NewTableView(databaseRepresentation, object : NewTableView.StringRunnable() {
                                override fun run(tablespaceName: String, tableName: String){
                                    tablespaceItem.children.apply {
                                        forEach {
                                            if (it.value == tablespaceName) {
                                                it.children.add(TreeItem(tableName))
                                                return@forEach
                                            }
                                        }
                                    }
                                }
                            }).openModal(resizable = false)
                        }
                    }

                    item("Alter") {

                    }
                }
            }

            left = treeview<String> {
                contextmenu {
                    item("Drop table") {
                        action {
                            val selectedItem = selectionModel.selectedItem
                            if (selectedItem.isLeaf && selectedItem.parent.value != TABLESPACES) {
                                println("delete ${selectedItem.value}")
                            }
                        }
                    }
                }

                selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    val treeItem = newValue

                    try {
                        if (newValue.isLeaf && newValue.parent.value != TABLESPACES && newValue.value != TABLESPACES) {
                            val types = databaseRepresentation.getColumnNames(newValue.parent.value,
                                    treeItem.value)

                            if (types.isNotEmpty()) {
                                tableView = TableView<List<String>>().apply {
                                    items = databaseRepresentation.getRecords(treeItem.value).observable()

                                    for (k in 0..types.size - 1) {
                                        column<List<String>, String>(types[k]) {
                                            ReadOnlyObjectWrapper(it.value[k])
                                        }
                                    }
                                }
                            } else {
                                tableView = null
                            }
                        } else {
                            tableView = null
                        }
                    } catch (e : SQLException) {
                        showSQLInternalError(e.localizedMessage)
                        tableView = null
                    }

                    center = tableView
                }

                tablespaceItem = TreeItem(TABLESPACES)
                root = tablespaceItem

                root.children.bind(databaseRepresentation.getTablespaces().observable(), {
                    TreeItem(it).apply {
                        children.bind(databaseRepresentation.getTables(it).observable(), {
                            TreeItem(it.name)
                        })
                    }
                })
            }
        }
    }

}