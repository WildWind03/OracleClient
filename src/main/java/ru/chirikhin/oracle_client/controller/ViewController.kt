package ru.chirikhin.oracle_client.controller

import javafx.application.Platform
import javafx.scene.control.Alert
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.util.showAlert
import ru.chirikhin.oracle_client.view.EventLogin
import ru.chirikhin.oracle_client.view.MainView
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
                    databaseController.connect(it.ip, it.port, it.username, it.password)

                    Platform.runLater({
                        replaceWith(MainView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
                    })
                } catch (e: SQLException) {
                    showAlert("Error", "Can not connect to the database", e.message ?:
                            "No additional information", Alert.AlertType.ERROR)
                    return@subscribe
                }
            }
        }
    }
}

