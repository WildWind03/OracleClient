package ru.chirikhin.oracle_client.view

import javafx.collections.ObservableList
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.model.Table
import tornadofx.*


class AddForeignKeyView(columnSettings: ObservableList<Column>, databaseRepresentation: DatabaseRepresentation, tablespace: String) : View() {
    override val root = FlowPane()

    private var constraintNameTextField: TextField by singleAssign()
    private var srcColumnComboBox: ComboBox<Column> by singleAssign()
    private var destTableComboBox: ComboBox<Table> by singleAssign()
    private var destColumnComboBox: ComboBox<String> by singleAssign()

    private val CONSTRAINT_DEFAULT_NAME = "FOREIGN KEY"

    init {
        form {
            fieldset {
                field("Name") {
                    constraintNameTextField = textfield {
                        text = CONSTRAINT_DEFAULT_NAME
                    }
                }

                field("Column") {
                    srcColumnComboBox = combobox {
                        items = columnSettings
                    }
                }

                field("Ref table") {
                    destTableComboBox = combobox {
                        items = databaseRepresentation.getTables(tablespace).values.toList().observable()
                        setOnAction {
                            val refTableName = selectedItem ?: return@setOnAction
                            destColumnComboBox.items = databaseRepresentation.getColumnNames(tablespace, refTableName.name).observable()
                        }
                    }
                }

                field("Ref column") {
                    destColumnComboBox = combobox {
                        val refTableName = destTableComboBox.selectedItem
                        if (null != refTableName) {
                            items = databaseRepresentation.getColumnNames(tablespace, refTableName.name).observable()
                        }
                    }
                }
            }

            button("Add new foreign key") {

            }
        }
    }
}