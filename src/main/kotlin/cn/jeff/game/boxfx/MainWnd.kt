package cn.jeff.game.boxfx

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainWnd : View("推箱子智能版") {

	override val root: BorderPane
	private val j: MainWndJ
	private val board: Board

	init {
		primaryStage.isResizable = true

		val loader = FXMLLoader()
		root = loader.load(javaClass.getResourceAsStream(
				"/cn/jeff/game/boxfx/MainWnd.fxml"
		))
		j = loader.getController()
		j.k = this

		board = Board()
		root.center = board.root
	}

	fun btnClick01() {
		information("很好！")
	}

	fun btnClick02() {
		val gameMap = MapManager.maps[0]
		println("加载第${gameMap.mapNo}关。")
		val scene = Scene(gameMap)
		board.setScene(scene)
	}

}
