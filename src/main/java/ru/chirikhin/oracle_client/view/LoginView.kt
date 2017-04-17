package ru.chirikhin.oracle_client.view

import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import tornadofx.*
import java.util.regex.Pattern


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

    private val signInButton = Button(SIGN_IN)
    private val ipTextField = TextField()
    private val portTextField = TextField()
    private val usernameTextField = TextField()
    private val passwordTextField = PasswordField()

    private var isIpValid = false
    private var isPortValid = false
    private var isUsernameValid = false
    private var isPasswordValid = false


    init {
        title = APP_NAME
        with(primaryStage) {
            maxHeight = HEIGHT.toDouble();
            minHeight = HEIGHT.toDouble();
            maxWidth = WIDTH.toDouble();
            maxWidth = WIDTH.toDouble();
        }

        val ipPattern = Pattern.compile(getIpRegex())
        val portPattern = Pattern.compile(getPortRegex())
        with(root) {
            form {
                fieldset {
                    field(IP) {
                        add(ipTextField);

                        ipTextField.apply {
                            textProperty().addListener { observable, oldValue, newValue ->
                                val ipMather = ipPattern.matcher(newValue)
                                isIpValid = ipMather.matches()
                                updateSignInEnabledState()
                            }
                        }
                    }

                    field(PORT) {
                        add(portTextField)

                        portTextField.apply {
                            textProperty().addListener { observable, oldValue, newValue ->
                                val portMather = portPattern.matcher(newValue)
                                isPortValid = portMather.matches()
                                updateSignInEnabledState()
                            }
                        }
                    }

                    field(USERNAME) {
                        add(usernameTextField)

                        usernameTextField.apply {
                            textProperty().addListener { observable, oldValue, newValue ->
                                isUsernameValid = !newValue.isEmpty()
                                updateSignInEnabledState()
                            }
                        }
                    }

                    field(PASSWORD) {
                        add(passwordTextField.apply {
                            textProperty().addListener { observable, oldValue, newValue ->
                                isPasswordValid = !newValue.isEmpty()
                                updateSignInEnabledState()
                            }
                        })
                    }
                }

                signInButton.disableProperty().set(true)
                add(signInButton);
            }

        }
    }

    private fun updateSignInEnabledState() {
        signInButton.disableProperty().set(!(isIpValid && isPasswordValid && isPortValid && isUsernameValid))
    }
}