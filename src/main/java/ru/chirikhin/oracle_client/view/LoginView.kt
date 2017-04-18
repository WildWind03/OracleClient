package ru.chirikhin.oracle_client.view

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import ru.chirikhin.oracle_client.util.getIpRegex
import ru.chirikhin.oracle_client.util.getPortRegex
import tornadofx.*
import java.util.regex.Pattern


class LoginView : View() {
    private val APP_NAME = "Oracle Client: Authorization"

    override val root = FlowPane()

    private val IP_DEFAULT = "127.0.0.1"
    private val PORT_DEFAULT = "12000"
    private val USERNAME_DEFAULT = "SYSTEM"
    private val PASSWORD_DEFAULT = "123456"

    private val SIGN_IN = "Sign in"
    private val USERNAME = "Username"
    private val PASSWORD = "Password"
    private val IP = "IP"
    private val PORT = "Port"
    private val MIN_HEIGHT = 280;
    private val MIN_WIDTH = 340;

    private val signInButton = Button(SIGN_IN)
    private val ipTextField = TextField(IP_DEFAULT)
    private val portTextField = TextField(PORT_DEFAULT)
    private val usernameTextField = TextField(USERNAME_DEFAULT)
    private val passwordTextField = PasswordField()

    private var isIpValid = true
    private var isPortValid = true
    private var isUsernameValid = true
    private var isPasswordValid = true


    init {
        passwordTextField.text = PASSWORD_DEFAULT
        title = APP_NAME
        with(primaryStage) {
            minWidth = MIN_WIDTH.toDouble()
            minHeight = MIN_HEIGHT.toDouble()
        }

        val ipPattern = Pattern.compile(getIpRegex())
        val portPattern = Pattern.compile(getPortRegex())

        with(root) {
            alignmentProperty().value = Pos.CENTER
             form {
                alignmentProperty().value = Pos.CENTER
                fieldset {
                    field(IP) {
                        add(ipTextField)

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

                signInButton.apply {
                    setOnAction { onSignInButtonClicked() }
                }

                add(signInButton)
            }
        }
    }

    private fun updateSignInEnabledState() {
        signInButton.disableProperty().set(!(isIpValid && isPasswordValid && isPortValid && isUsernameValid))
    }

    private fun onSignInButtonClicked() {
        fire(EventLogin(ipTextField.text, portTextField.text, usernameTextField.text, passwordTextField.text, this))
    }
}