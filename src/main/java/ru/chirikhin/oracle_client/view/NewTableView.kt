package ru.chirikhin.oracle_client.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.model.Table
import ru.chirikhin.oracle_client.util.createNewTableQueryFromData
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*
import java.util.*

class NewTableView(val databaseRepresentation: DatabaseRepresentation) : View() {

    override val root = VBox()

    private val TABLESPACE_LABEL = "Tablespace"
    private val TABLE_NAME_LABEL = "Name of the table"
    private val EXAMPLE_NAME_OF_TABLE = "NewTable1"

    private val columnSettings = ArrayList<Column>().observable()
    private val constraints = ArrayList<Constraint>().observable()

    private var tablespaceComboBox: ComboBox<String> by singleAssign()
    private var tableNameTextField: TextField by singleAssign()
    private var constraintListView: ListView<Constraint> by singleAssign()

    private val PK_CONSTRAINT_ADD = "PK_CONSTRAINT"
    private var primaryKeyConstraintName = "${EXAMPLE_NAME_OF_TABLE}_$PK_CONSTRAINT_ADD"

    init {
        with(root) {
            title = "Add new table"

            form {
                alignmentProperty().value = Pos.CENTER
                fieldset {
                    labelPosition = Orientation.VERTICAL
                    vbox {
                        field(TABLESPACE_LABEL) {
                            tablespaceComboBox = combobox <String> {
                                items = databaseRepresentation.getTablespaces().observable()
                                if (items.isNotEmpty()) {
                                    selectionModel.select(0)
                                }
                            }
                        }

                        field(TABLE_NAME_LABEL) {
                            tableNameTextField = textfield {
                                text = EXAMPLE_NAME_OF_TABLE
                                textProperty().addListener{
                                    _, _, newValue ->
                                    run {
                                        val oldName = primaryKeyConstraintName
                                        primaryKeyConstraintName = "${newValue}_$PK_CONSTRAINT_ADD"
                                        constraints.forEach {
                                            if (it.name == oldName) {
                                                it.name = primaryKeyConstraintName
                                            }
                                        }

                                        constraintListView.refresh()
                                    }
                                }
                            }
                        }

                        field("Table description") {
                            listview<Column> {
                                items = columnSettings
                                setPrefSize(500.0, 200.0)

                                contextmenu {
                                    item("Remove column").action {
                                        columnSettings.remove(selectedItem)
                                    }
                                }
                            }
                        }

                        button {
                            text = "Add column"

                            action {
                                AddColumnView(columnSettings).openModal(resizable = false)
                            }
                        }

                        field("Constraints") {
                            constraintListView = listview<Constraint> {
                                items = constraints
                                setPrefSize(500.0, 200.0)
                                contextmenu {
                                    item("Remove column").action {
                                        constraints.remove(selectedItem)
                                    }
                                }
                            }
                        }

                        hbox {
                            spacing = 37.0

                            button("Add primary key") {
                                action {
                                    AddConstraintView(columnSettings, object : AddConstraintView.AddConstraintRunnable() {
                                        override fun run(name: String, columnName: String) {
                                            constraints.add(Constraint.PrimaryKey(name, columnName))
                                        }

                                    }, "Add primary key", false, primaryKeyConstraintName).openModal(resizable = false)
                                }
                            }

                            button("Add unique constraint") {
                                action {
                                    AddConstraintView(columnSettings, object : AddConstraintView.AddConstraintRunnable() {
                                        override fun run(name: String, columnName: String) {
                                            constraints.add(Constraint.UniqueConstraint(name, columnName))
                                        }

                                    }, "Add new unique constraint").openModal(resizable = false)
                                }
                            }

                            button("Add foreign key") {
                                action {
                                    val tablespaceName = tablespaceComboBox.selectedItem ?: return@action
                                    AddForeignKeyView(columnSettings, constraints, databaseRepresentation, tablespaceName).openModal(resizable = false)
                                }
                            }
                        }
                    }
                }

                button("Add the table") {
                    action {
                        try {
                            val readyQuery = createNewTableQueryFromData(
                                    tablespaceComboBox.value,
                                    tableNameTextField.text,
                                    columnSettings, constraints
                            )

                            DatabaseController.executeQuery(readyQuery)
                            databaseRepresentation.addTable(tablespaceComboBox.value,
                                    Table(tableNameTextField.text).apply {
                                        setConstraints(constraints)
                                        setColumns(columnSettings)
                                    })

                            close()
                        } catch (e: Exception) {
                            showSQLInternalError(e.message ?: e.toString())
                        }
                    }
                }
            }
        }
    }
}
