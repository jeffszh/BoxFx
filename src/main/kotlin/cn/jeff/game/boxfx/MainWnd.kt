package cn.jeff.game.boxfx

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainWnd : View("推箱子智能版") {

	override val root: BorderPane
	private val j: MainWndJ

	init {
		primaryStage.isResizable = true

		val loader = FXMLLoader()
		root = loader.load(javaClass.getResourceAsStream(
				"/cn/jeff/game/boxfx/MainWnd.fxml"
		))
		j = loader.getController()
		j.k = this
	}

	fun btnClick01() {
		information("很好！")
	}

}
