package com.example.passwordchecker

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage

class PasswordCheckerApp : Application() {

    override fun start(stage: Stage) {
        val title = Label("Password Strength Checker").apply {
            font = Font.font("System", 22.0)
        }

        val passwordField = PasswordField().apply { promptText = "Enter password..." }
        val visibleField = TextField().apply {
            promptText = "Enter password..."
            isVisible = false
            isManaged = false
        }

        // keep in sync
        passwordField.textProperty().addListener { _, _, v -> if (visibleField.text != v) visibleField.text = v }
        visibleField.textProperty().addListener { _, _, v -> if (passwordField.text != v) passwordField.text = v }

        val showCheck = CheckBox("Show").apply {
            setOnAction {
                val show = isSelected
                visibleField.isVisible = show
                visibleField.isManaged = show
                passwordField.isVisible = !show
                passwordField.isManaged = !show
            }
        }

        val inputRow = HBox(10.0, passwordField, visibleField, showCheck).apply {
            alignment = Pos.CENTER_LEFT
            HBox.setHgrow(passwordField, Priority.ALWAYS)
            HBox.setHgrow(visibleField, Priority.ALWAYS)
        }

        val strengthBar = ProgressBar(0.0).apply { prefWidth = 520.0 }
        val strengthLabel = Label("Strength: Very Weak (0/100)").apply { font = Font.font("System", 13.0) }

        val rulesBox = VBox(6.0)

        val suggestionsArea = TextArea().apply {
            isEditable = false
            isWrapText = true
            prefRowCount = 5
        }

        fun refresh(pw: String) {
            val result = StrengthEvaluator.evaluate(pw)
            strengthBar.progress = result.score / 100.0
            strengthLabel.text = "Strength: ${result.label} (${result.score}/100)"

            rulesBox.children.clear()
            result.rules.forEach { rr ->
                rulesBox.children.add(Label("${if (rr.passed) "✅" else "❌"} ${rr.name}"))
            }

            suggestionsArea.text = result.suggestions.joinToString(separator = "\n• ", prefix = "• ")
        }

        // generator controls
        val lengthLabel = Label("Length: 16")
        val lengthSlider = Slider(8.0, 32.0, 16.0).apply {
            isShowTickLabels = true
            isShowTickMarks = true
            majorTickUnit = 8.0
            blockIncrement = 1.0
        }
        lengthSlider.valueProperty().addListener { _, _, v -> lengthLabel.text = "Length: ${v.toInt()}" }

        val upperCheck = CheckBox("Uppercase").apply { isSelected = true }
        val lowerCheck = CheckBox("Lowercase").apply { isSelected = true }
        val digitCheck = CheckBox("Numbers").apply { isSelected = true }
        val specialCheck = CheckBox("Special").apply { isSelected = true }

        val generateBtn = Button("Generate Strong Password").apply {
            setOnAction {
                val pw = PasswordGenerator.generate(
                    PasswordGenerator.Options(
                        length = lengthSlider.value.toInt(),
                        includeUpper = upperCheck.isSelected,
                        includeLower = lowerCheck.isSelected,
                        includeDigits = digitCheck.isSelected,
                        includeSpecial = specialCheck.isSelected
                    )
                )
                passwordField.text = pw
                refresh(pw)
            }
        }

        val copyBtn = Button("Copy").apply {
            setOnAction {
                val pw = if (showCheck.isSelected) visibleField.text else passwordField.text
                if (pw.isNullOrBlank()) return@setOnAction

                val clipboard = Clipboard.getSystemClipboard()
                val content = ClipboardContent()
                content.putString(pw)
                clipboard.setContent(content)
            }
        }

        val clearBtn = Button("Clear").apply {
            setOnAction {
                passwordField.clear()
                visibleField.clear()
                refresh("")
            }
        }

        val genRow1 = HBox(10.0, lengthLabel, lengthSlider).apply {
            alignment = Pos.CENTER_LEFT
            HBox.setHgrow(lengthSlider, Priority.ALWAYS)
        }

        val genRow2 = HBox(12.0, upperCheck, lowerCheck, digitCheck, specialCheck).apply {
            alignment = Pos.CENTER_LEFT
        }

        val actions = HBox(10.0, generateBtn, copyBtn, clearBtn).apply {
            alignment = Pos.CENTER_LEFT
        }

        // live update
        passwordField.textProperty().addListener { _, _, v -> refresh(v) }
        visibleField.textProperty().addListener { _, _, v -> refresh(v) }

        refresh("")

        val root = VBox(14.0).apply {
            padding = Insets(20.0)
            children.addAll(
                title,
                Label("Password:"),
                inputRow,
                strengthBar,
                strengthLabel,
                Separator(),
                Label("Requirements:"),
                rulesBox,
                Separator(),
                Label("Suggestions:"),
                suggestionsArea,
                Separator(),
                genRow1,
                genRow2,
                actions
            )
        }

        stage.title = "PasswordChecker"
        stage.scene = Scene(root, 740.0, 720.0)
        stage.show()
    }
}

fun main() {
    Application.launch(PasswordCheckerApp::class.java)
}
