package ru.chirikhin.oracle_client.view

import javafx.geometry.Orientation
import javafx.scene.layout.FlowPane
import ru.chirikhin.oracle_client.model.Column
import tornadofx.*
import java.util.ArrayList


class AddColumnView(columnSettings: ArrayList<Column>) : View() {
    override val root = FlowPane()

    private val EXAMPLE_TYPE = "NUMBER"
    private val EXAMPLE_NAME = "ANOTHER_COLUMN"

    private val nullableCheckBox = checkbox {  }
    private val typeTextField = textfield {
        text = EXAMPLE_TYPE
    }
    private val nameTextFiled = textfield {
        text = EXAMPLE_NAME
    }

    init {
        with(root) {
            form {
                fieldset() {
//                    field("Name") {
//                        nameTextFiled
//                    }

                    field("Type") {
                        typeTextField
                    }

                    field("Nullable") {
                        nullableCheckBox
                    }
                }

                button("Add new column") {
                    columnSettings.add(Column(!nullableCheckBox.isSelected, typeTextField.text, nameTextFiled.text))
                }

            }
        }
    }
}