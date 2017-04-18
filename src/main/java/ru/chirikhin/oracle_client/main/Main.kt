package ru.chirikhin.oracle_client.main

import ru.chirikhin.oracle_client.view.LoginView
import ru.chirikhin.oracle_client.controller.ViewController
import tornadofx.App

class Main : App() {
    override val primaryView = LoginView::class

    init {
        val viewController = ViewController()
    }
}