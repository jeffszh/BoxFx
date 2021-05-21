package cn.jeff.utils

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*

class Toast(msg: String) : View("提示框") {

	override val root = label(msg) {
		style = "-fx-background: rgba(56,56,56,0.7);-fx-border-radius: 25;-fx-background-radius: 25"//label透明,圆角
		textFill = Color.rgb(225, 255, 226)
		prefHeight = 50.0
		paddingAll = 15.0
		alignment = Pos.CENTER
		font = Font(20.0)
	}

	fun show(timeInMs: Int = 1000) {
		openWindow(StageStyle.TRANSPARENT)
		root.scene.fill = null
		runLater(Duration.millis(timeInMs.toDouble())) {
			close()
		}
	}

}
