package cn.jeff.game.boxfx

import cn.jeff.game.boxfx.brain.SolutionFinder
import cn.jeff.game.boxfx.data.RoomRecord
import cn.jeff.game.boxfx.data.gameRecord
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.util.*

class AiWnd(private val roomNo: Int) : View("AI自动求解 - 第 $roomNo 关") {

	override val root: BorderPane
	private val j: AiWndJ
	private val board: Board
	private val aiResult: ListProperty<SolutionFinder.EvcLink> = SimpleListProperty()
	private val aiSuccess = aiResult.isNotNull
	private val demoStep = SimpleIntegerProperty(0)
	private var aiTask: Task<*>? = null

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
		board.isInAiWnd = true

		val demoDisabled = aiSuccess.not().or(board.isBusyProperty)
		j.btnReset.disableProperty().bind(demoDisabled)
		j.btnNext.disableProperty().bind(
			demoDisabled.or(
				demoStep.greaterThanOrEqualTo(
					aiResult.sizeProperty()
				)
			)
		)
		j.btnAutoDemo.disableProperty().bind(demoDisabled)
		j.stepLabel.textProperty().bind(demoStep.stringBinding {
			"第${it}步"
		})
	}

	fun abort() {
		aiTask?.cancel()
	}

	override fun onUndock() {
		abort()
	}

	override fun onDock() {
		super.onDock()
		val t1 = Date().time
		j.label3.text = "正在求解……"
		aiTask = runAsync {
			SolutionFinder(
				board.scene.width, board.scene.height,
				board.cells, board.manLocation
			).search(
				{ level, nodeCount ->
					runLater {
						j.label1.text = "正向搜索 - 深度：$level 节点数：$nodeCount"
					}
				},
				{ level, nodeCount ->
					runLater {
						j.label2.text = "反向搜索 - 深度：$level 节点数：$nodeCount"
					}
				}
			)
		} ui { searchResult ->
			println(searchResult)
			println(searchResult.count())
			val t2 = Date().time
			val timeElapsed = t2 - t1
			j.label3.text = "求解完成！最佳解法${searchResult.count()}步，耗时${timeElapsed}毫秒。"
			aiResult.value = searchResult.observable()
			demoStep.value = 0
			j.btnAutoDemo.requestFocus()
			val roomRecord = gameRecord.roomRecords[roomNo] ?: RoomRecord()
			roomRecord.apply {
				stepCountByAi = searchResult.count()
				aiSpendsTime = timeElapsed
			}
			gameRecord.roomRecords[roomNo] = roomRecord
			gameRecord.save()
		}
	}

	fun demoReset() {
		val room = RoomManager.rooms[roomNo]!!
		board.scene = Scene(room)
		demoStep.value = 0
	}

	fun demoNext() {
		val push = aiResult[demoStep.value].pushOrPull
		board.manMoveTo(push.manLocation.x, push.manLocation.y)
		board.isBusyProperty.awaitUntil {
			!it
		}
		board.moveOrPush(push.direction.dx, push.direction.dy)
		demoStep.value++
	}

	fun autoDemo() {
		demoReset()
		while (!j.btnNext.isDisabled) {
			demoNext()
		}
	}

	fun testIt() {
		SolutionFinder(
			board.scene.width, board.scene.height,
			board.cells, board.manLocation
		).test()
	}

}
