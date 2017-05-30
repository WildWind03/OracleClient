package ru.chirikhin.oracle_client.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.util.showAlert
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.textarea


class QueryEditorView(runnable: DatabaseRepresentationRunnable) : View() {
    override val root = VBox(20.0)

    init {
        with(root) {
            paddingProperty().set(Insets(20.0, 20.0, 20.0, 20.0))
            alignmentProperty().value = Pos.CENTER
            val textArea = textarea {

            }

            button("Execute") {
                action {
                    val query = textArea.text
                    if (query.trim().substring(0, 6).toUpperCase() == "SELECT") {
                        fire(EventSelectQuery(query, object : StringArgRunnable {
                            override fun run(arg: String) {
                                showSQLInternalError(arg)
                            }
                        }, object : StringArgRunnable {
                            override fun run(arg: String) {
                                showAlert("Result", "Result", arg, Alert.AlertType.INFORMATION)
                            }
                        }))
                    } else {
                        fire(EventSomeQuery(query, onErrorAction = object : StringArgRunnable {
                            override fun run(arg: String) {
                                showSQLInternalError(arg)
                            }
                        }, onSuccessAction = object : StringArgRunnable {
                            override fun run(arg: String) {
                                showAlert("Result", "Success", "Successfully executed", Alert.AlertType.INFORMATION)
                            }
                        }, runnable = runnable))
                    }

                    close()
                }
            }
        }
    }
}