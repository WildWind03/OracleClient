package ru.chirikhin.oracle_client.view

import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.property.*
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*
import java.sql.SQLException
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.control.cell.TextFieldTreeTableCell
import kotlin.properties.ObservableProperty


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
                            NewTableView(databaseRepresentation).openModal(resizable = false)
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
                                databaseRepresentation.deleteTable(selectedItem.parent.value, selectedItem.value)
                            }
                        }
                    }
                }

                selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    val treeItem = newValue

                    try {
                        if (newValue.isLeaf && newValue.parent.value != TABLESPACES && newValue.value != TABLESPACES) {
                            val columnNames = databaseRepresentation.getColumnNames(newValue.parent.value,
                                    treeItem.value)

                            if (columnNames.isNotEmpty()) {
                                val tableViewItems = databaseRepresentation.getRecords(treeItem.value).observable()
                                tableView = tableview(tableViewItems) {
                                    isEditable = true

                                    var oldValues : List<String>? = null

                                    for (k in 0..columnNames.size - 1) {
                                        column<List<String>, String?>(columnNames[k]) {
                                            SimpleObjectProperty(it.value[k])
                                        }.useTextField().apply {
                                            setOnEditStart {
                                                oldValues = it.rowValue
                                            }
                                            setOnEditCommit {
                                                val newValue = it.newValue
                                                val oldValue = it.oldValue
                                                val columnNumber = it.tablePosition.column

                                                if (null == newValue) {
                                                    return@setOnEditCommit
                                                }

                                                databaseRepresentation.updateRow(oldValues, columnNames, columnNames[columnNumber], newValue, treeItem.value)
                                            }
                                        }
                                    }

                                    contextmenu {
                                        item("Delete row") {
                                            action {
                                                val selectedRow = selectedItem ?: return@action

                                                try {
                                                    databaseRepresentation.deleteRow(newValue.value, columnNames, selectedRow)
                                                } catch (e : Exception) {
                                                    showSQLInternalError(e.toString())
                                                }
                                                tableViewItems.remove(selectedRow)

                                            }
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
                        children.bind(databaseRepresentation.getTables(it), {
                            TreeItem(it.name)
                        })
                    }
                })
            }
        }
    }

}