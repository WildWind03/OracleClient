package ru.chirikhin.oracle_client.util

import javafx.application.Platform
import javafx.scene.control.Alert

fun showAlert(title : String, header : String, content : String, type : Alert.AlertType) {
    Platform.runLater({
        Alert(type).apply {
            this.title = title
            this.headerText = header
            this.contentText = content
            showAndWait()
        }
    })
}

fun showErrorAlert(content: String) {
    showAlert("Error", "SQL internal error", content, Alert.AlertType.ERROR)
}