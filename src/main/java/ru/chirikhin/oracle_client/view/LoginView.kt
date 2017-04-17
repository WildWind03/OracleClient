package ru.chirikhin.oracle_client.view

import javafx.scene.layout.FlowPane
import tornadofx.*

class LoginView : View() {
    private val APP_NAME = "Oracle Client"

    override val root = FlowPane()

    private val SIGN_IN = "Sign in"
    private val USERNAME = "Username"
    private val PASSWORD = "Password"
    private val IP = "IP"
    private val PORT = "Port"
    private val HEIGHT = 280;
    private val WIDTH = 340;

    init {
        title = APP_NAME
        with(primaryStage) {
            maxHeight = HEIGHT.toDouble();
            minHeight = HEIGHT.toDouble();
            maxWidth = WIDTH.toDouble();
            maxWidth = WIDTH.toDouble();
        }

        with (root) {
            form {
                fieldset {
                    field(IP) {
                        textfield { }
                    }

                    field(PORT) {
                        textfield { }
                    }

                    field(USERNAME) {
                        textfield { }
                    }

                    field(PASSWORD) {
                        passwordfield { }
                    }
                }

                button(SIGN_IN) { }
            }
        }
    }

    fun signIn() {

    }
}