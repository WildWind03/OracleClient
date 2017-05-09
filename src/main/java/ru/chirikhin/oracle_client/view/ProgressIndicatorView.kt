package ru.chirikhin.oracle_client.view

import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.FlowPane
import tornadofx.View
import tornadofx.add


class ProgressIndicatorView : View() {
    override val root = FlowPane()

    init {
        root.add(ProgressIndicator())
    }

}