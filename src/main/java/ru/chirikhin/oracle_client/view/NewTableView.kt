package ru.chirikhin.oracle_client.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.util.createNewTableQueryFromData
import ru.chirikhin.oracle_client.util.showErrorAlert
import tornadofx.*
import java.util.*

class NewTableView(val databaseRepresentation: DatabaseRepresentation) : View() {
    override val root = VBox()

    private val TABLESPACE = "Tablespace"
    private val TABLE_NAME = "Name of the table"
    private val EXAMPLE_NAME_OF_TABLE = "New table"

    private val columnSettings = ArrayList<Column>().observable()
    private val constraints = ArrayList<Constraint>().observable()

    private var tablespaceComboBox: ComboBox<String> by singleAssign()
    private var tableNameTextField: TextField by singleAssign()

    init {
        with(root) {
            title = "Add new table"
            form {
                alignmentProperty().value = Pos.CENTER
                fieldset {
                    labelPosition = Orientation.VERTICAL
                    vbox {
                        field(TABLESPACE) {
                            tablespaceComboBox = combobox <String> {
                                items = databaseRepresentation.getTablespaces().observable()
                                selectionModel.select(0)
                            }
                        }

                        field(TABLE_NAME) {
                            tableNameTextField = textfield {
                                text = EXAMPLE_NAME_OF_TABLE
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
                            listview<Constraint> {
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

                                    }, "Add primary key").openModal(resizable = false)
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

                            DatabaseController.createTableWithQuery(readyQuery)
                            close()
                        } catch (e : Exception) {
                            showErrorAlert(e.message ?: "No reason")
                        }
                    }
                }
            }

        }
    }
}
