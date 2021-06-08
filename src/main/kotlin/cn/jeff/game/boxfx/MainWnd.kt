package cn.jeff.game.boxfx

import cn.jeff.game.boxfx.data.RoomRecord
import cn.jeff.game.boxfx.data.gameRecord
import cn.jeff.game.boxfx.event.RoomSuccessEvent
import cn.jeff.game.boxfx.event.TimerEvent
import cn.jeff.utils.Toast
import cn.jeff.utils.inputNumber
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainWnd : View("推箱子智能版") {

	override val root: BorderPane
	private val j: MainWndJ
	private val board: Board
	private val currentRoomNo = SimpleIntegerProperty(0)
	private val bestStepCount = SimpleStringProperty()
	private val bestStepByAI = SimpleStringProperty()

	init {
		primaryStage.isResizable = true

		val loader = FXMLLoader()
		root = loader.load(
			javaClass.getResourceAsStream(
				"/cn/jeff/game/boxfx/MainWnd.fxml"
			)
		)
		j = loader.getController()
		j.k = this

		board = Board()
		root.center = board.root

		loadFirstRoom()

		subscribe<RoomSuccessEvent> { event ->
			onRoomSuccess(event.stepCount)
		}
		subscribe<TimerEvent> {
			board.onTimer()
		}
	}

	private fun loadFirstRoom() {
		loadRoom(gameRecord.lastPlayedRoom?.let {
			RoomManager.rooms[it]
		} ?: RoomManager.rooms.values.first())
	}

	private fun loadRoom(room: RoomManager.Room) {
		val scene = Scene(room)
		board.scene = scene
		currentRoomNo.value = room.roomNo
		gameRecord.lastPlayedRoom = currentRoomNo.value
		gameRecord.save()
		bestStepCount.value = gameRecord.roomRecords[currentRoomNo.value]
			?.bestStepCount?.toString()
		bestStepByAI.value = gameRecord.roomRecords[currentRoomNo.value]
			?.stepCountByAi?.toString()
		bindStatusLabel()
	}

	private fun bindStatusLabel() {
		val stepCount = board.stepCount
		val binding = stringBinding(currentRoomNo, stepCount, bestStepCount, bestStepByAI) {
			"第${currentRoomNo.value}关  当前步数：${stepCount.value} ${
				bestStepCount.value?.let {
					"以往最佳步数：$it"
				} ?: ""
			} ${
				bestStepByAI.value?.let {
					"AI最佳步数：$it"
				} ?: ""
			}"
		}
		j.statusLabel.bind(binding)
	}

	fun prevRoom() {
		val subMap = RoomManager.rooms.headMap(currentRoomNo.value)
		if (subMap.isNotEmpty()) {
			loadRoom(subMap.values.last())
		} else {
			Toast("前面没有了。").show()
		}
	}

	fun selectRoom() {
		inputNumber(currentRoomNo.value, "请输入关卡号码") { roomNo ->
			RoomManager.rooms[roomNo]?.also {
				// 找到这关，就直接加载。
				loadRoom(it)
			} ?: apply {
				// 找不到，首先向后搜。
				RoomManager.rooms.tailMap(roomNo).also { tailMap ->
					// 如果向后搜找到了，问TA要不要改为这关。
					if (tailMap.isNotEmpty()) {
						val otherRoom = tailMap.values.first()
						confirm("找不到第 $roomNo 关，是否改为加载第 ${otherRoom.roomNo} 关？") {
							loadRoom(otherRoom)
						}
					} else {
						// 向后搜找不到，就向前搜。
						RoomManager.rooms.headMap(roomNo).also { headMap ->
							// 如果向前搜找到了，问TA要不要改为这关。
							if (headMap.isNotEmpty()) {
								val otherRoom = headMap.values.last()
								confirm("找不到第 $roomNo 关，是否改为加载第 ${otherRoom.roomNo} 关？") {
									loadRoom(otherRoom)
								}
							} else {
								// 不会运行到这里，不可能后面和前面都没有，除非一关都没有。
								warning("找不到第 $roomNo 关。")
							}
						}
					}
				}
			}
		}
	}

	fun nextRoom() {
		val subMap = RoomManager.rooms.tailMap(currentRoomNo.value + 1)
		if (subMap.isNotEmpty()) {
			loadRoom(subMap.values.first())
		} else {
			Toast("后面没有了。").show()
		}
	}

	fun playAgain() {
		RoomManager.rooms[currentRoomNo.value]?.let {
			loadRoom(it)
		}
	}

	fun autoResolve() {
		val aiWnd = AiWnd(currentRoomNo.value).openWindow()!!
		currentStage?.let {
			aiWnd.x = it.x + it.width
		}
	}

	private fun onRoomSuccess(stepCount: Int) {
		bestStepCount.value = stepCount.toString()
		val roomRecord = gameRecord.roomRecords[currentRoomNo.value] ?: RoomRecord()
		if (roomRecord.bestStepCount ?: Int.MAX_VALUE > stepCount) {
			roomRecord.bestStepCount = stepCount
			gameRecord.roomRecords[currentRoomNo.value] = roomRecord
			gameRecord.save()
		}
	}

}
