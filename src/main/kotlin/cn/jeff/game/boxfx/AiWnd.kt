package cn.jeff.game.boxfx

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class AiWnd(roomNo: Int) : View("AI自动求解 - 第 $roomNo 关") {

	override val root: BorderPane
	private val j: AiWndJ
	private val board: Board

	init {
		primaryStage.isResizable = true

		val loader = FXMLLoader()
		root = loader.load(
			javaClass.getResourceAsStream(
				"/cn/jeff/game/boxfx/AiWnd.fxml"
			)
		)
		j = loader.getController()
		j.k = this

		board = Board()
		root.center = board.root

		val room = RoomManager.rooms[roomNo]!!
		board.scene = Scene(room)
		board.isSuccess = true
	}

	fun abort() {
	}

}