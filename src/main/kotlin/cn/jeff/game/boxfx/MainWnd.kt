package cn.jeff.game.boxfx

import cn.jeff.utils.Toast
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainWnd : View("推箱子智能版") {

	override val root: BorderPane
	private val j: MainWndJ
	private val board: Board
	private var currentMapNo = 0

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

		loadFirstStage()
	}

	private fun loadFirstStage() {
		loadMap(MapManager.maps.values.first())
	}

	private fun loadMap(gameMap: MapManager.GameMap) {
		val scene = Scene(gameMap)
		board.setScene(scene)
		currentMapNo = gameMap.mapNo
	}

	fun prevStage() {
		val subMap = MapManager.maps.headMap(currentMapNo)
		if (subMap.isNotEmpty()) {
			loadMap(subMap.values.last())
		} else {
			//Toast.show("前面没有了。")
			Toast("前面没有了。").show()
		}
	}

	fun selectStage() {}

	fun nextStage() {
		val subMap = MapManager.maps.tailMap(currentMapNo + 1)
		if (subMap.isNotEmpty()) {
			loadMap(subMap.values.first())
		} else {
			//Toast.show("后面没有了。")
			Toast("后面没有了。").show()
		}
	}

}
