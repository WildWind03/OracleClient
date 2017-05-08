package ru.chirikhin.oracle_client.controller

import javafx.scene.control.Alert
import ru.chirikhin.oracle_client.database.DatabaseController
import ru.chirikhin.oracle_client.view.EventLogin
import ru.chirikhin.oracle_client.view.LoginView
import ru.chirikhin.oracle_client.view.MainView
import tornadofx.Controller
import java.sql.SQLException

class ViewController : Controller() {
    init {
        subscribe<EventLogin>  {
            it.loginView.apply {
                try {
                    DatabaseController.connect(it.ip, it.port, it.username, it.password)
                } catch (e : SQLException) {
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Error"
                        headerText = "Can not connect to the database"
                        contentText = e.message
                        showAndWait()
                        return@subscribe;
                    }
                }

                replaceWith(MainView::class)
            }
        }
    }
}

