package com.cgos.side

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlin.system.exitProcess

class SIDELauncher : Application() {
    var commandLine: String = ""
    var inputMode: InputMode = InputMode.TEXT
    var pressedKey: Map<KeyCode, Boolean> = mapOf()
    var keyPressedTime: Map<KeyCode, Long> = mapOf()
    var codes: ArrayList<String> = ArrayList()
    val cursor: Cursor = Cursor()
    var isCapsLocked: Boolean = false
    val charMap: Map<String, Double> = CharSize().charMap

    val keyInputThread: Thread = Thread {
        try {
            while (true) {
                if (keyPressedTime.containsKey(KeyCode.ALT) && System.currentTimeMillis() - keyPressedTime.get(KeyCode.ALT)!! >= 500) {
                    Platform.runLater {
                        changeInputMode(InputMode.COMMAND)
                    }
                } else if (pressedKey.containsKey(KeyCode.ENTER) && inputMode == InputMode.COMMAND) {
                    Platform.runLater {
                        changeInputMode(InputMode.TEXT)
                        commandLine = commandPrompt.text
                        commandPrompt.text = "command: "
                        pressedKey = pressedKey.minus(KeyCode.ENTER)
                        keyPressedTime = keyPressedTime.minus(KeyCode.ENTER)
                        println("Command entered: $commandLine")
                    }
                } else if (keyPressedTime.containsKey(KeyCode.ESCAPE) && System.currentTimeMillis() - keyPressedTime.get(
                        KeyCode.ESCAPE
                    )!! >= 2000
                ) {
                    exitProcess(0)
                } else if (pressedKey.containsKey(KeyCode.CONTROL) && inputMode != InputMode.COMMAND) {
                    if (inputMode != InputMode.CURSOR) {
                        Platform.runLater {
                            changeInputMode(InputMode.CURSOR)
                        }
                    }
                } else if (!pressedKey.containsKey(KeyCode.CONTROL) && inputMode == InputMode.CURSOR) {
                    Platform.runLater {
                        changeInputMode(InputMode.TEXT)
                    }
                } else if (pressedKey.containsKey(KeyCode.CAPS)) {
                    isCapsLocked = !isCapsLocked
                    pressedKey = pressedKey.minus(KeyCode.CAPS)
                    keyPressedTime = keyPressedTime.minus(KeyCode.CAPS)
                } else if (pressedKey.containsKey(KeyCode.ENTER) && inputMode == InputMode.TEXT) {
                    pressedKey = pressedKey.minus(KeyCode.ENTER)
                    keyPressedTime = keyPressedTime.minus(KeyCode.ENTER)
                    inputCode("\n")
                    insertLine()
                } else if (pressedKey.containsKey(KeyCode.BACK_SPACE) && inputMode == InputMode.TEXT) {

                }
                Thread.sleep(10)
            }
        } catch (e: InterruptedException) {
            println("Key input thread interrupted")
        }
    }
    val renderCodeSpaceThread: Thread = Thread {
        try {
            while (true) {
                renderCode(renderedCodeSpace)
                Thread.sleep(20)
            }
        } catch (e: InterruptedException) {
            println("Render code space thread interrupted")
        }
    }


    val titleBar: BorderPane = BorderPane()
    val fileNameLabel: Label = Label("New Code")
    val inputModeLabel: Label = Label("TEXT")
    val mainScreen: BorderPane = BorderPane()
    val codeSpace: Canvas = Canvas()
    val renderedCodeSpace: GraphicsContext = codeSpace.graphicsContext2D
    val rootPanel: BorderPane = BorderPane()
    val commandPrompt: TextField = TextField("command: ")

    override fun start(stage: Stage) {
        codes.add("")
        titleBar.style =
            "-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1px;"

//        closeButton.setOnAction { closeButtonAction(stage) }
//        closeButton.style =
//            "-fx-background-color: #ff0000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #ff0000; -fx-border-width: 1px; -fx-alignment: center;"
//        titleBar.left = closeButton

        fileNameLabel.style =
            "-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1px; -fx-alignment: center;"
        titleBar.center = fileNameLabel

        inputModeLabel.style =
            "-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1px; -fx-alignment: center;"
        titleBar.right = inputModeLabel

        mainScreen.style =
            "-fx-background-color: #323232; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-border-color: #646464; -fx-border-width: 1px;"
        rootPanel.center = mainScreen

        codeSpace.width = 800.0
        codeSpace.height = 600.0
        codeSpace.widthProperty().bind(rootPanel.widthProperty())
        codeSpace.heightProperty().bind(rootPanel.heightProperty())
//        codeSpace.requestFocus()
        mainScreen.center = codeSpace

        commandPrompt.isVisible = false
        rootPanel.bottom = commandPrompt

//        rootPanel.setOnKeyPressed { event ->
//
//        }
//        rootPanel.setOnKeyReleased { event ->
//            pressedKey = pressedKey.minus(event.code)
//            keyPressedTime = keyPressedTime.minus(event.code)
//            println("Key released: ${event.code}")
//        }
        stage.setOnHiding {
            pressedKey = mapOf()
            keyPressedTime = mapOf()
        }
        stage.setOnCloseRequest {
            exitProcess(0)
        }
        stage.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            if (!pressedKey.containsKey(event.code) && isSpecialKey(event.code) && inputMode == InputMode.TEXT) {
                pressedKey = pressedKey.plus(event.code to true)
                keyPressedTime = keyPressedTime.plus(event.code to System.currentTimeMillis())
                println("Key pressed: ${event.code}")
            }
//            else if (isEnglishLetter(event.code) && inputMode == InputMode.TEXT) {
//                inputCode(event.text)
//                println(codes[0])
//            } else if (event.code == KeyCode.SPACE && inputMode == InputMode.TEXT) {
//                inputCode(" ")
//            }
            else if (event.code == KeyCode.BACK_SPACE && inputMode == InputMode.TEXT) {
                if (cursor.curX > 0) {
                    codes[cursor.curY.toInt()] = codes[cursor.curY.toInt()].substring(
                        0,
                        cursor.curX.toInt() - 1
                    ) + codes[cursor.curY.toInt()].substring(cursor.curX.toInt())
                    cursor.curX--
                } else if (cursor.curX == 0.toLong() && cursor.curY > 0) {
                    val tempString =codes[cursor.curY.toInt()]
                    codes.removeAt(cursor.curY.toInt())
                    cursor.curY--
                    cursor.curX = codes[cursor.curY.toInt()].length.toLong()
                    codes[cursor.curY.toInt()] += tempString
                }
                cursor.setBlinkingStatus(true)
            }
        }
        stage.addEventHandler(KeyEvent.KEY_RELEASED) { event ->
            pressedKey = pressedKey.minus(event.code)
            keyPressedTime = keyPressedTime.minus(event.code)
            println("Key released: ${event.code}")
        }
        stage.addEventHandler(KeyEvent.KEY_TYPED) { event ->
            val inputChar: String = event.character
            if (inputChar.length != 1 || inputChar.matches("\\p{Cntrl}".toRegex())) {
                println("Special key typed: $inputChar ${event.code == KeyCode.UNDEFINED}")
                event.consume()
                return@addEventHandler
            }
//            println("Key typed: ${event.code}")
//            inputCode(event.text)
            if (inputChar != null && inputChar.length == 1) {
                inputCode(inputChar)
            }
        }


        rootPanel.top = titleBar

        val scene = Scene(rootPanel, 800.0, 600.0)

        stage.title = "SIDE"
        stage.isFullScreen = true
        stage.scene = scene
        stage.show()

        initThreads()
    }

    fun changeInputMode(inputMode: InputMode) {
        this.inputMode = inputMode
        inputModeLabel.text = inputMode.toString()
        when (inputMode) {
            InputMode.COMMAND -> {
                commandPrompt.isVisible = true
                commandPrompt.requestFocus()
                cursor.isBlinking = false
                cursor.isVisible = true
            }

            InputMode.TEXT -> {
                commandPrompt.isVisible = false
                cursor.isBlinking = true
                cursor.isVisible = true
            }

            InputMode.CURSOR -> {
                commandPrompt.isVisible = false
            }
        }
    }

    fun initThreads() {
        keyInputThread.start()
        renderCodeSpaceThread.start()
        cursor.setBlinkingStatus(true)
    }

    private fun isEnglishLetter(keyCode: KeyCode): Boolean {
        return keyCode in KeyCode.A..KeyCode.Z
    }

    private fun isSpecialKey(keyCode: KeyCode): Boolean {
        return keyCode == KeyCode.ENTER || keyCode == KeyCode.SHIFT
                || keyCode == KeyCode.CONTROL || keyCode == KeyCode.ALT
                || keyCode == KeyCode.CAPS || keyCode == KeyCode.ESCAPE
    }

    private fun renderCode(renderer: GraphicsContext) {
        val maxLineNumber = codes.size
        val maxLineNumberDigit = maxLineNumber.toString().length
        val lineNumberSpace = maxLineNumberDigit * 10.0
        renderer.clearRect(0.0, 0.0, renderer.canvas.width, renderer.canvas.height)
        // highlight the current line
        renderer.fill = Color(0.3, 0.3, 0.3, 1.0)
        val width: Double = renderer.canvas.width
        renderer.fillRect(0.0, cursor.curY * 25.0, width, 25.0)

        //size up the font
        renderer.font = javafx.scene.text.Font.font(20.0)
        for (i in 0 until codes.size) {
            // line number
            renderer.fill = Color(0.5, 0.5, 0.5, 1.0)
//            renderer.fillText((i + 1).toString(), 0.0, 20.0 + i * 25)
            // draw line number match the max line number digit
            val lineNumber = (i + 1).toString()
            val lineNumberWidth = lineNumber.length * 10.0
            renderer.fillText(lineNumber, lineNumberSpace - lineNumberWidth, 20.0 + i * 25)

            renderer.fill = Color(0.9, 0.9, 0.9, 1.0)
            var usedWidth: Double = 0.0
            for (j in 0 until codes[i].length) {
                renderer.fillText(codes[i][j].toString(), 20.0 + usedWidth, 20.0 + i * 25)
                if (charMap.containsKey(codes[i][j].toString())) {
                    usedWidth += (charMap[codes[i][j].toString()]!! + 1.0)
//                renderer.fillText(codes[i][j].toString(), 20.0 + j * 15, 20.0 + i * 25)
                } else {
                    usedWidth += 10.0
                }
            }
//            renderer.fillText(codes[i], 20.0, 20.0 + i * 25)
        }

        // render the cursor
        if (cursor.isVisible) {
            renderer.fill = Color(0.9, 0.9, 0.9, 1.0)
            var usedWidth: Double = 0.0
            for (i in 0 until cursor.curX) {
                if (charMap.containsKey(codes[cursor.curY.toInt()][i.toInt()].toString())) {
                    usedWidth += (charMap[codes[cursor.curY.toInt()][i.toInt()].toString()]!! + 1.0)
                } else {
                    usedWidth += 10.0
                }
            }
//            renderer.fillRect(20.0 + cursor.curX * 15, 2.5+ cursor.curY * 25.0, 2.0, 20.0)
            renderer.fillRect(20.0 + usedWidth, 2.5 + cursor.curY * 25.0, 2.0, 20.0)
        }
    }

    private fun inputCode(code: String) {
        cursor.setBlinkingStatus(true)
        if (pressedKey.containsKey(KeyCode.SHIFT) && !isCapsLocked) {
            codes[cursor.curY.toInt()] += code.toUpperCase()
        } else if (pressedKey.containsKey(KeyCode.SHIFT) && isCapsLocked) {
            codes[cursor.curY.toInt()] += code.toLowerCase()
        } else if (!pressedKey.containsKey(KeyCode.SHIFT) && isCapsLocked) {
            codes[cursor.curY.toInt()] += code.toUpperCase()
        } else {
            codes[cursor.curY.toInt()] += code.toLowerCase()
        }
        cursor.curX++
    }

    private fun insertLine() {
        val originalLineNumber = cursor.curY
        cursor.curY++
        cursor.curX = 0
        codes.add(originalLineNumber.toInt() + 1, "")

    }
}

fun main() {
    Application.launch(SIDELauncher::class.java)
}