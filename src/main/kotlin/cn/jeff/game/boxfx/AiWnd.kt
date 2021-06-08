package cn.jeff.game.boxfx

import cn.jeff.game.boxfx.brain.SolutionFinder
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class AiWnd(private val roomNo: Int) : View("AI自动求解 - 第 $roomNo 关") {

	override val root: BorderPane
	private val j: AiWndJ
	private val board: Board
	private val aiResult: ListProperty<SolutionFinder.EvcLink> = SimpleListProperty()
	private val aiSuccess = aiResult.isNotNull
	private val demoStep = SimpleIntegerProperty(0)

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
		j.stepLabel.textProperty().bind(demoStep.stringBinding {
			"第${it}步"
		})
	}

	fun abort() {
	}

	override fun onDock() {
		super.onDock()
		j.label3.text = "正在求解……"
		runAsync {
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
			j.label3.text = "求解完成！最佳解法需${searchResult.count()}步。"
			aiResult.value = searchResult.observable()
			demoStep.value = 0
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

	fun testIt() {
		SolutionFinder(
			board.scene.width, board.scene.height,
			board.cells, board.manLocation
		).test()
	}

}
