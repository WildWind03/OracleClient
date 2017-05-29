package ru.chirikhin.oracle_client.view

import javafx.collections.ObservableList
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.util.showAlert
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*

class AddColumnInAlterView(columnSettings: ObservableList<Column>, nameOfTable : String) : View() {
    final override val root = FlowPane()

    private val EXAMPLE_TYPE = "NUMBER"
    private val EXAMPLE_NAME = "Column1"

    private var nullableCheckBox : CheckBox by singleAssign()
    private var typeTextField : TextField by singleAssign()
    private var nameTextFiled : TextField by singleAssign()

    init {
        with(root) {
            title = "Add new column"
            form {
                fieldset {
                    field("Name") {
                        nameTextFiled = textfield {
                            text = EXAMPLE_NAME
                        }
                    }

                    field("Type") {
                        typeTextField = textfield {
                            text = EXAMPLE_TYPE
                        }
                    }

                    field("Nullable") {
                        nullableCheckBox = checkbox {  }
                    }
                }

                button("Add new column") {
                    action {
                        try {
                            val column = Column(!nullableCheckBox.isSelected, typeTextField.text, nameTextFiled.text)
                            fire(EventAddNewColumn(nameOfTable, column))
                            columnSettings.add(column)
                        } catch (e : Exception) {
                            showSQLInternalError(e.toString())
                        }
                        close()
                    }
                }

            }
        }
    }
}