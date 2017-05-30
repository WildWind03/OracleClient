package ru.chirikhin.oracle_client.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.model.Table
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*


class AlterTableView(val table: Table, val databaseRepresentation: DatabaseRepresentation,
                     val afterCloseRunnable : Runnable, val tablespace : String) : View() {
    override val root = VBox()

    private val TABLE_NAME_LABEL = "Name of the table"
    private val EXAMPLE_NAME_OF_TABLE = "NewTable1"
    private val PK_CONSTRAINT_ADD = "PK_CONSTRAINT"

    private var primaryKeyConstraintName = "${EXAMPLE_NAME_OF_TABLE}_$PK_CONSTRAINT_ADD"

    private var constraintListView: ListView<Constraint> by singleAssign()

    init {
        val columns = ArrayList(table.getColumns().values).observable()
        val constraints = ArrayList(table.getConstraints()).observable()

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
                                        try {
                                            fire(EventDeleteColumn(table.name, selectedItem?.name ?: throw IllegalArgumentException("Can not delete column")))
                                            table.deleteColumn(selectedItem?.name ?: throw IllegalArgumentException())
                                            columns.remove(selectedItem)

                                        } catch (e : Exception) {
                                            showSQLInternalError(e.toString())
                                        }
                                    }
                                }
                            }
                        }

                        button {
                            text = "Add column"

                            action {
                                AddColumnInAlterView(columns, table).openModal(resizable = false)
                            }
                        }

                        field("Constraints") {
                            constraintListView = listview<Constraint> {
                                items = constraints
                                setPrefSize(500.0, 200.0)
                                contextmenu {
                                    item("Remove constraint").action {
                                        try {
                                            fire(EventDropConstraint(table.name, selectedItem?.name ?: throw IllegalArgumentException("Can not drop constraint")))
                                            table.removeConstraint(selectedItem?.name ?: throw IllegalArgumentException("Can not drop constraint"))
                                            val currentSelectedItem = selectedItem?.name
                                            constraints.removeIf {
                                                it.name == currentSelectedItem
                                            }
                                        } catch (e : Exception) {
                                            showSQLInternalError(e.toString())
                                        }
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
                                            val primaryKey = Constraint.PrimaryKey(name, columnName)
                                            fire(EventAddConstraint(table.name, primaryKey))
                                            constraints.add(primaryKey)
                                        }
                                    }, "Add primary key", false, primaryKeyConstraintName).openModal(resizable = false)
                                }
                            }

                            button("Add unique constraint") {
                                action {
                                    AddConstraintView(columns, object : AddConstraintView.AddConstraintRunnable() {
                                        override fun run(name: String, columnName: String) {
                                            val uniqueConstraint = Constraint.UniqueConstraint(name, columnName)
                                            fire(EventAddConstraint(table.name, uniqueConstraint))
                                            constraints.add(uniqueConstraint)
                                        }
                                    }, "Add new unique constraint").openModal(resizable = false)
                                }
                            }

                            button("Add foreign key") {
                                action {
                                    AddForeignKeyInAlterView(columns, constraints, databaseRepresentation, tablespace, table.name).openModal(resizable = false)
                                }
                            }
                        }
                    }
                }

                button("Finish altering") {
                    action {
                        afterCloseRunnable.run()
                        close()
                    }
                }
            }
        }

    }
}