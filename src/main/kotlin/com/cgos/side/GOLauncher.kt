package com.cgos.go

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class GOLauncher : Application() {
    var commandLine: String = ""
    var inputMode: InputMode = InputMode.COMMAND

    val titleBar: BorderPane = BorderPane()
    val closeButton: Button = Button("Close")
    val fileNameLabel: Label = Label("File Name")
    val inputModeLabel: Label = Label("Command Mode")
    val rootPanel: BorderPane = BorderPane()

    override fun start(stage: Stage) {
        titleBar.style =
            "-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1px;"

        closeButton.setOnAction { closeButtonAction(stage) }
        closeButton.style =
            "-fx-background-color: #ff0000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #ff0000; -fx-border-width: 1px; -fx-alignment: center;"
        titleBar.left = closeButton

        fileNameLabel.style =
            "-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1px; -fx-alignment: center;"
        titleBar.center = fileNameLabel

        inputModeLabel.style =
            "-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1px; -fx-alignment: center;"
        titleBar.right = inputModeLabel

        val mainScreen = VBox()
        mainScreen.style =
            "-fx-background-color: #646464; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #646464; -fx-border-width: 1px;"

        rootPanel.setOnKeyPressed { event ->
            when (event.code) {
                KeyCode.ESCAPE -> {
                    changeInputMode(InputMode.COMMAND)
                }

                KeyCode.ENTER -> {
                    changeInputMode(InputMode.TEXT)
                }

                else -> {
                    println("Key pressed: ${event.code}")
                }
            }
        }


        rootPanel.center = mainScreen
        rootPanel.top = titleBar

        val scene = Scene(rootPanel, 800.0, 600.0)

        stage.title = "GO IDE"
        stage.isFullScreen = true
        stage.scene = scene
        stage.show()
    }

    private fun closeButtonAction(stage: Stage) {
        println("Close button clicked")
        stage.close()
    }

    fun changeInputMode(inputMode: InputMode) {
        this.inputMode = inputMode
        inputModeLabel.text = inputMode.toString()
    }
}

fun main() {
    Application.launch(GOLauncher::class.java)
}