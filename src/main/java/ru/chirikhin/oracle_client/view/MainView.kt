package ru.chirikhin.oracle_client.view

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.model.NoSuchTablespaceException
import ru.chirikhin.oracle_client.util.showSQLInternalError
import sun.plugin2.jvm.RemoteJVMLauncher
import tornadofx.*
import tornadofx.Stylesheet.Companion.cell
import tornadofx.Stylesheet.Companion.tab
import java.sql.SQLException


class MainView : View() {
    override val root = BorderPane()

    private var tableView: TableView<List<String>>? = null
    private val databaseRepresentation : DatabaseRepresentation = DatabaseRepresentation()

    private val MAIN_VIEW_TITLE = "Oracle Client"
    private val TABLESPACES = "Tablespaces"
    private val ADD_NEW_ITEM = "Add new table"

    private val databaseController = DatabaseController

    private var tablespaceItem : TreeItem<String> by singleAssign()

    init {
        title = MAIN_VIEW_TITLE

        with(primaryStage) {
            isMaximized = true
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
                selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                    val treeItem = newValue

                    try {
                        if (newValue.isLeaf && newValue.parent.value != TABLESPACES && newValue.value != TABLESPACES) {
                            val types = databaseRepresentation.getColumnNames(newValue.parent.value,
                                    treeItem.value)

                            if (types.isNotEmpty()) {
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



                try {
                    val tablespaces = databaseController.getTablespaces()
                    databaseRepresentation.addTablespaces(tablespaces)

                    for (tablespace in tablespaces) {
                        val tablespaceItem = TreeItem(tablespace)
                        root.children.add(tablespaceItem)

                        val tables = databaseController.getTableNames(tablespace)
                        databaseRepresentation.addTables(tablespace, tables)

                        val constraints = databaseController.getConstraints(tables)

                        tables.forEach {
                            val tableConstraints = constraints[it]
                            val constraintMap = HashMap<String, Constraint>()
                            tableConstraints?.forEach { constraintMap.put(it.name, it) }
                            databaseRepresentation.getTable(tablespace, it).setConstraints(constraintMap)

                            val columns = databaseController.getColumns(it)
                            databaseRepresentation.getTable(tablespace, it).setColumns(columns)
                        }

                        tablespaceItem.children.apply {
                            tables.forEach {
                                add(TreeItem(it))
                            }
                        }
                    }
                } catch (e : SQLException) {
                    showSQLInternalError(e.localizedMessage)
                }
            }
        }
    }

}