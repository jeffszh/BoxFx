package cn.jeff.game.boxfx

import cn.jeff.utils.Toast
import cn.jeff.utils.inputNumber
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
			Toast("前面没有了。").show()
		}
	}

	fun selectStage() {
		inputNumber(currentMapNo, "请输入关卡号码") { mapNo ->
			MapManager.maps[mapNo]?.also {
				// 找到这关，就直接加载。
				loadMap(it)
			} ?: apply {
				// 找不到，首先向后搜。
				MapManager.maps.tailMap(mapNo).also { tailMap ->
					// 如果向后搜找到了，问TA要不要改为这关。
					if (tailMap.isNotEmpty()) {
						val otherMap = tailMap.values.first()
						confirm("找不到第 $mapNo 关，是否改为加载第 ${otherMap.mapNo} 关？") {
							loadMap(otherMap)
						}
					} else {
						// 向后搜找不到，就向前搜。
						MapManager.maps.headMap(mapNo).also { headMap ->
							// 如果向前搜找到了，问TA要不要改为这关。
							if (headMap.isNotEmpty()) {
								val otherMap = headMap.values.last()
								confirm("找不到第 $mapNo 关，是否改为加载第 ${otherMap.mapNo} 关？") {
									loadMap(otherMap)
								}
							} else {
								// 不会运行到这里，不可能后面和前面都没有，除非一关都没有。
								warning("找不到第 $mapNo 关。")
							}
						}
					}
				}
			}
		}
	}

	fun nextStage() {
		val subMap = MapManager.maps.tailMap(currentMapNo + 1)
		if (subMap.isNotEmpty()) {
			loadMap(subMap.values.first())
		} else {
			Toast("后面没有了。").show()
		}
	}

}
