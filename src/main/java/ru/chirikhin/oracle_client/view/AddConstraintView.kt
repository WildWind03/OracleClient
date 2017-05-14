package ru.chirikhin.oracle_client.view

import javafx.collections.ObservableList
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.util.showAlert
import tornadofx.*

class AddConstraintView(columns: ObservableList<Column>,
                        val runnable: AddConstraintRunnable, title: String?) : View(title) {

    abstract class AddConstraintRunnable {
        abstract fun run(name : String, columnName : String)
    }

    override val root = FlowPane()
    var nameTextField : TextField by singleAssign()

    var columnComboBox : ComboBox<Column> by singleAssign()

    private val EXAMPLE_NAME = "NEW_CON"

    init {
        form {
            fieldset {
                field("Name") {
                    nameTextField = textfield {
                        text = EXAMPLE_NAME
                    }
                }

                field("Column") {
                    columnComboBox = combobox {
                        items = columns
                    }
                }
            }

            button("Add constraint") {
                action {
                    if (nameTextField.text.isEmpty()) {
                        showAlert("Can not add new constraint", "Invalid name", "You can not use empty name", Alert.AlertType.ERROR)
                        return@action
                    }

                    if (columnComboBox.selectedItem == null) {
                        showAlert("Can not add new constraint", "Invalid column", "You have to choose column", Alert.AlertType.ERROR)
                        return@action
                    }

                    runnable.run(nameTextField.text, (columnComboBox.selectedItem as Column).name)
                    close()
                }
            }
        }
    }
}