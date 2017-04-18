package ru.chirikhin.oracle_client.controller

import ru.chirikhin.oracle_client.view.EventLogin
import ru.chirikhin.oracle_client.view.MainView
import tornadofx.Controller

class ViewController : Controller() {
    init {
        subscribe<EventLogin> {
            it.loginView.apply {
                replaceWith(MainView::class)
            }
        }
    }
}

