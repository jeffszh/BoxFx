package cn.jeff.utils

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*

/*
object Toast {

	private val label = Label().apply {
		style = "-fx-background: rgba(56,56,56,0.7);-fx-border-radius: 25;-fx-background-radius: 25"//label透明,圆角
		textFill = Color.rgb(225, 255, 226)
		prefHeight = 50.0
		paddingAll = 15.0
		alignment = Pos.CENTER
		font = Font(20.0)
	}
	private val stage = Stage().apply {
		scene = Scene(label)
		scene.fill = null
		initStyle(StageStyle.TRANSPARENT)
		isAlwaysOnTop = true
	}
	private var task: FXTimerTask? = null

	fun show(msg: String, timeInMs: Int = 1000) {
		task?.cancel()
		task = runLater(Duration.millis(timeInMs.toDouble())) {
			stage.close()
		}
		label.text = msg
		stage.show()
	}

}
*/

class Toast(msg: String) : View("提示框") {

	override val root = label(msg) {
		style = "-fx-background: rgba(56,56,56,0.7);-fx-border-radius: 25;-fx-background-radius: 25"//label透明,圆角
		textFill = Color.rgb(225, 255, 226)
		prefHeight = 50.0
		paddingAll = 15.0
		alignment = Pos.CENTER
		font = Font(20.0)
	}

	init {
//		currentStage!!.apply {
//			isAlwaysOnTop = true
//		}
	}

	fun show(timeInMs: Int = 1000) {
		openWindow(StageStyle.TRANSPARENT)
		root.scene.fill = null
		runLater(Duration.millis(timeInMs.toDouble())) {
			close()
		}
	}

}
