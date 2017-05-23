package ru.chirikhin.oracle_client.main

import ru.chirikhin.oracle_client.controller.ViewController
import ru.chirikhin.oracle_client.view.LoginView
import tornadofx.App

class Main : App() {
    override val primaryView = LoginView::class

    init {
        ViewController()
    }
}