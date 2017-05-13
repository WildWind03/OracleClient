package ru.chirikhin.oracle_client.view

import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.model.Column
import tornadofx.*
import java.util.ArrayList


class AddColumnView(columnSettings: ObservableList<Column>) : View() {
    override val root = FlowPane()

    private val EXAMPLE_TYPE = "NUMBER"
    private val EXAMPLE_NAME = "ANOTHER_COLUMN"

    private var nullableCheckBox : CheckBox? = null
    private var typeTextField : TextField? = null
    private var nameTextFiled : TextField? = null

    init {
        with(root) {
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
                        columnSettings.add(Column(!nullableCheckBox!!.isSelected, typeTextField!!.text, nameTextFiled!!.text))
                        close()
                    }
                }

            }
        }
    }
}