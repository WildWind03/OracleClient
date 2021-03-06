package ru.chirikhin.oracle_client.view

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderPane
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*
import java.sql.SQLException


class MainView : Fragment() {
    override val root = BorderPane()

    private var tableView: TableView<List<String>>? = null
    val databaseRepresentation: DatabaseRepresentation by params
    private var oldArg1: ObservableValue<out TreeItem<String>>? = null
    private var oldArg2: TreeItem<String>? = null
    private var oldArg3: TreeItem<String>? = null

    private val MAIN_VIEW_TITLE = "Oracle Client"
    private val TABLESPACES = "Tablespaces"
    private val ADD_NEW_ITEM = "Add new table"

    private var tablespaceItem: TreeItem<String> by singleAssign()

    private val OPEN_QUERY_EDITOR = "Open query editor"

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
                    item(OPEN_QUERY_EDITOR) {
                        setOnAction {
                            QueryEditorView(object : DatabaseRepresentationRunnable {
                                override fun run(databaseRepresentation: DatabaseRepresentation) {
                                    replaceWith(find<MainView>
                                    (mapOf(MainView::databaseRepresentation to databaseRepresentation)),
                                            ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                                }
                            }).openModal(resizable = false)
                        }
                    }
                }
            }

            val selectAction = { _: ObservableValue<out TreeItem<String>>, _: TreeItem<String>?, newValue: TreeItem<String> ->
                val treeItem = newValue

                try {
                    if (newValue.isLeaf && newValue.parent.value != TABLESPACES && newValue.value != TABLESPACES) {
                        val columnNames = databaseRepresentation.getColumnNames(newValue.parent.value,
                                treeItem.value)

                        if (columnNames.isNotEmpty()) {
                            val tableViewItems = databaseRepresentation.getRecords(treeItem.value).observable()
                            tableView = tableview(tableViewItems) {
                                isEditable = true

                                var oldValues: List<String>? = null

                                for (k in 0..columnNames.size - 1) {
                                    column<List<String>, String?>(columnNames[k]) {
                                        SimpleObjectProperty(it.value[k])
                                    }.useTextField().apply {
                                        setOnEditStart {
                                            oldValues = it.rowValue
                                        }
                                        setOnEditCommit {
                                            val newValue = it.newValue
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
                                                tableViewItems.remove(selectedRow)
                                            } catch (e: Exception) {
                                                showSQLInternalError(e.toString())
                                            }
                                        }
                                    }

                                    item("Insert row") {
                                        action {
                                            val columns = databaseRepresentation.getTable(newValue.parent.value, newValue.value).getColumns()
                                            InsertRowView(columns.values, newValue.value, tableViewItems).openModal(resizable = false)
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
                } catch (e: SQLException) {
                    showSQLInternalError(e.localizedMessage)
                    tableView = null
                }

                center = tableView
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

                    item("Alter table") {
                        action {
                            val selectedItem = selectionModel.selectedItem
                            if (selectedItem.isLeaf && selectedItem.parent.value != TABLESPACES) {
                                AlterTableView(databaseRepresentation.getTable(selectedItem.parent.value,
                                        selectedItem.value), databaseRepresentation, Runnable {
                                    if (oldArg1 != null && oldArg3 != null) {
                                        selectAction.invoke(oldArg1!!, oldArg2, oldArg3!!)
                                    }
                                }, selectedItem.parent.value).openModal()

                            }
                        }
                    }
                }

                selectionModel.selectedItemProperty().addListener { arg1, arg2, arg3 ->
                    oldArg1 = arg1
                    oldArg2 = arg2
                    oldArg3 = arg3
                    selectAction.invoke(arg1, TreeItem(), arg3)
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