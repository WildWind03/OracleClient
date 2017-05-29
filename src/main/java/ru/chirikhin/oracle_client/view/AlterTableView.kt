package ru.chirikhin.oracle_client.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.model.Table
import tornadofx.*


class AlterTableView(val table: Table, val databaseRepresentation: DatabaseRepresentation) : View() {
    override val root = VBox()

    private val TABLE_NAME_LABEL = "Name of the table"
    private val EXAMPLE_NAME_OF_TABLE = "NewTable1"
    private val PK_CONSTRAINT_ADD = "PK_CONSTRAINT"

    private var primaryKeyConstraintName = "${EXAMPLE_NAME_OF_TABLE}_$PK_CONSTRAINT_ADD"

    private var constraintListView: ListView<Constraint> by singleAssign()
    private var tablespaceComboBox: ComboBox<String> by singleAssign()

    init {
        val columns = table.getColumns().values.toList().observable()
        val constraints = table.getConstraints().values.toList().observable()


        with(root) {
            title = "Alter the table"

            form {
                alignmentProperty().value = Pos.CENTER
                fieldset {
                    labelPosition = Orientation.VERTICAL
                    vbox {
                        field(TABLE_NAME_LABEL) {
                            label(table.name)
                        }

                        field("Table description") {
                            listview<Column> {
                                items = columns
                                setPrefSize(500.0, 200.0)

                                contextmenu {
                                    item("Remove column").action {
                                        columns.remove(selectedItem)
                                    }
                                }
                            }
                        }

                        button {
                            text = "Add column"

                            action {
                                AddColumnView(columns).openModal(resizable = false)
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
                                    AddConstraintView(columns, object : AddConstraintView.AddConstraintRunnable() {
                                        override fun run(name: String, columnName: String) {
                                            constraints.add(Constraint.PrimaryKey(name, columnName))
                                        }

                                    }, "Add primary key", false, primaryKeyConstraintName).openModal(resizable = false)
                                }
                            }

                            button("Add unique constraint") {
                                action {
                                    AddConstraintView(columns, object : AddConstraintView.AddConstraintRunnable() {
                                        override fun run(name: String, columnName: String) {
                                            constraints.add(Constraint.UniqueConstraint(name, columnName))
                                        }

                                    }, "Add new unique constraint").openModal(resizable = false)
                                }
                            }

                            button("Add foreign key") {
                                action {
                                    val tablespaceName = tablespaceComboBox.selectedItem ?: return@action
                                    AddForeignKeyView(columns, constraints, databaseRepresentation, tablespaceName).openModal(resizable = false)
                                }
                            }
                        }
                    }
                }

                button("Finish altering") {
                    action {
                        close()
                    }
                }
            }
        }

    }
}