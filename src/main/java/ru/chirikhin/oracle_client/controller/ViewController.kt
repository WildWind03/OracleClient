package ru.chirikhin.oracle_client.controller

import javafx.application.Platform
import javafx.scene.control.Alert
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.util.showAlert
import ru.chirikhin.oracle_client.view.ChangeLayoutEvent
import ru.chirikhin.oracle_client.view.EventLogin
import ru.chirikhin.oracle_client.view.MainView
import ru.chirikhin.oracle_client.view.ProgressIndicatorView
import tornadofx.Controller
import tornadofx.ViewTransition
import tornadofx.seconds
import java.sql.SQLException

class ViewController : Controller() {
    val databaseController = DatabaseController

    init {
        subscribe<EventLogin> {
            it.loginView.apply {
                try {
                    Platform.runLater({
                        replaceWith(ProgressIndicatorView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                    })

                    databaseController.connect(it.ip, it.port, it.username, it.password)

                    fire(ChangeLayoutEvent())
                } catch (e: SQLException) {
                    showAlert("Error", "Can not connect to the database", e.message ?:
                            "No additional information", Alert.AlertType.ERROR)
                    return@subscribe
                }
            }
        }
    }
}

