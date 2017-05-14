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
    private val USERNAME_DEFAULT = "C##Wind"
    private val PASSWORD_DEFAULT = "Pumpkin123"

    private val SIGN_IN = "Sign in"
    private val USERNAME = "Username"
    private val PASSWORD = "Password"
    private val IP = "IP"
    private val PORT = "Port"
    private val MIN_HEIGHT = 280;
    private val MIN_WIDTH = 340;

    private var signInButton : Button by singleAssign()
    private var ipTextField : TextField by singleAssign()
    private var portTextField : TextField by singleAssign()
    private var usernameTextField : TextField by singleAssign()
    private var passwordTextField : PasswordField by singleAssign()

    private var isIpValid = true
    private var isPortValid = true
    private var isUsernameValid = true
    private var isPasswordValid = true


    init {
        title = APP_NAME

        with(primaryStage) {
            minWidth = MIN_WIDTH.toDouble()
            minHeight = MIN_HEIGHT.toDouble()
            resizableProperty().set(false)
        }

        val ipPattern = Pattern.compile(getIpRegex())
        val portPattern = Pattern.compile(getPortRegex())

        with(root) {
            alignmentProperty().value = Pos.CENTER
             form {
                alignmentProperty().value = Pos.CENTER
                fieldset {
                    field(IP) {
                        ipTextField = textfield (IP_DEFAULT) {
                            textProperty().addListener { _, _, newValue ->
                                val ipMather = ipPattern.matcher(newValue)
                                isIpValid = ipMather.matches()
                                updateSignInEnabledState()
                            }
                        }
                    }

                    field(PORT) {
                        portTextField = textfield (PORT_DEFAULT) {
                            textProperty().addListener { _, _, newValue ->
                                val portMather = portPattern.matcher(newValue)
                                isPortValid = portMather.matches()
                                updateSignInEnabledState()
                            }
                        }
                    }

                    field(USERNAME) {

                        usernameTextField = textfield (USERNAME_DEFAULT) {
                            textProperty().addListener { _, _, newValue ->
                                isUsernameValid = !newValue.isEmpty()
                                updateSignInEnabledState()
                            }
                        }
                    }

                    field(PASSWORD) {
                        passwordTextField = passwordfield(PASSWORD_DEFAULT) {
                            textProperty().addListener { _, _, newValue ->
                                isPasswordValid = !newValue.isEmpty()
                                updateSignInEnabledState()
                            }
                        }
                    }
                }

                signInButton = button(SIGN_IN) {
                    setOnAction { onSignInButtonClicked() }
                }
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