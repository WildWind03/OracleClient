package ru.chirikhin.oracle_client.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.FlowPane
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.add
import tornadofx.seconds


class ProgressIndicatorView : View() {
    override val root = FlowPane()

    init {
        with(root) {
            alignmentProperty().set(Pos.CENTER)
            add(ProgressIndicator())
        }

        subscribe<ChangeLayoutEvent> {
            Platform.runLater({
                replaceWith(MainView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
            })
        }

    }

}