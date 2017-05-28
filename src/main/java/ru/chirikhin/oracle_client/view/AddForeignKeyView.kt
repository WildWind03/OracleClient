package ru.chirikhin.oracle_client.view

import javafx.collections.ObservableList
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import ru.chirikhin.oracle_client.model.Table
import ru.chirikhin.oracle_client.util.showAlert
import tornadofx.*


class AddForeignKeyView(columnSettings: ObservableList<Column>, constraints : ObservableList<Constraint>, databaseRepresentation: DatabaseRepresentation, tablespace: String) : View() {
    override val root = FlowPane()

    private var constraintNameTextField: TextField by singleAssign()
    private var srcColumnComboBox: ComboBox<Column> by singleAssign()
    private var destTableComboBox: ComboBox<Table> by singleAssign()
    private var destColumnComboBox: ComboBox<String> by singleAssign()

    private val DEFAULT_FOREIGN_KEY_TEXT = "DEFAULT_FOREIGN_KEY_NAME"

    init {
        form {
            fieldset {
                field("Name") {
                    constraintNameTextField = textfield {
                        text = DEFAULT_FOREIGN_KEY_TEXT
                    }
                }

                field("Column") {
                    srcColumnComboBox = combobox {
                        items = columnSettings
                    }
                }

                field("Ref table") {
                    destTableComboBox = combobox {
                        items = databaseRepresentation.getTables(tablespace)
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
                action {
                    if (constraintNameTextField.text.isEmpty() || srcColumnComboBox.value == null || destTableComboBox.value == null
                            || destColumnComboBox.value == null) {
                        showAlert("Can not add new foreign key", "Not all fields are filled", "Fill all fields", Alert.AlertType.ERROR)
                        return@action
                    }

                    constraints.add(Constraint.ForeignKey(constraintNameTextField.text, srcColumnComboBox.value.name, destTableComboBox.value.name,
                            destColumnComboBox.value))

                    close()
                }
            }
        }
    }
}