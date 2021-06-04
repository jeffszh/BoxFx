package cn.jeff.game.boxfx

import cn.jeff.game.boxfx.brain.PathFinder
import cn.jeff.game.boxfx.event.MoveOrPushEvent
import cn.jeff.game.boxfx.event.RoomSuccessEvent
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY
import cn.jeff.utils.Toast
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import tornadofx.*

/**
 * # 棋盘
 *
 * 用于显示游戏画面中央的主体部分，可理解为棋盘。
 */
class Board : View() {

	companion object {
		private val outsideImage = Image("/img/out_side.png")
		private val wallImage = Image("/img/wall.png")
		private val spaceImage = Image("/img/space.png")
		private val spaceDestImage = Image("/img/space_dest.png")
		private val boxImage = Image("/img/box.png")
		private val boxDestImage = Image("/img/box_dest.png")
		private val manImage = Image("/img/man.png")
		private val manImageR = Image("/img/man_r.png")
		private val manDestImage = Image("/img/man_dest.png")
		private val manDestImageR = Image("/img/man_dest_r.png")

		private val cellImgMap = mapOf(
			Cell.OUTSIDE to
					listOf(outsideImage),
			Cell.WALL to
					listOf(wallImage),
			Cell.SPACE to
					listOf(spaceImage),
			Cell.SPACE_DEST to
					listOf(spaceDestImage),
			Cell.BOX to
					listOf(boxImage),
			Cell.BOX_DEST to
					listOf(boxDestImage),
			Cell.MAN to
					listOf(manImage, manImageR),
			Cell.MAN_DEST to
					listOf(manDestImage, manDestImageR)
		)

		private fun Cell.toImage(): List<Image> =
			cellImgMap[this] ?: kotlin.error("不可能")
	}

	override val root = gridpane {
		alignment = Pos.CENTER
		isFocusTraversable = true
		onKeyPressed = EventHandler {
			processKey(it)
		}
	}

	private var manLocation = LocationXY(0, 0)
	private var internalCellList =
		listOf(listOf<ObjectProperty<Cell>>())
	private val cells = object : ArrayXY<Cell> {
		override fun get(locationXY: LocationXY): Cell =
			internalCellList[locationXY.y][locationXY.x].value

		override operator fun set(locationXY: LocationXY, value: Cell) {
			internalCellList[locationXY.y][locationXY.x].value = value
		}
	}

	var scene: Scene = Scene(0, 0)
		set(value) {
			field = value
			internalSetScene(value)
			stepCount.value = 0
			isSuccess = false
		}
	private val width get() = scene.width
	private val height get() = scene.height
	val stepCount = SimpleIntegerProperty(0)
	var isSuccess = false

	private fun internalSetScene(scene: Scene) {
		root.clear()
		println("加载scene，宽度=${scene.width}，高度=${scene.height}")
		internalCellList = scene.cells.mapIndexed { y, cellList ->
			val obCellList = cellList.map {
				SimpleObjectProperty(it)
			}
			root.row {
				obCellList.forEachIndexed { x, cell ->
					imageview {
						imageProperty().bind(cell.objectBinding(timeTick) {
							it?.toImage()?.let { imgLst ->
								if (imgLst.count() > 1) {
									imgLst[timeTick.value and 0x01]
								} else {
									imgLst.first()
								}
							}
						})
						setOnMouseClicked {
							if (it.button == MouseButton.PRIMARY) {
								onCellClick(x, y)
							}
						}
					}

					// 顺便找到人的位置
					if (cell.value.isMan()) {
						manLocation = LocationXY(x, y)
					}
				}
			}
			obCellList
		}
	}

	private fun processKey(k: KeyEvent) {
		// println(k)
		if (isSuccess) return
		when (k.code) {
			KeyCode.UP -> moveOrPush(0, -1)
			KeyCode.DOWN -> moveOrPush(0, 1)
			KeyCode.LEFT -> moveOrPush(-1, 0)
			KeyCode.RIGHT -> moveOrPush(1, 0)
			else -> {
				// do nothing
			}
		}
	}

	private fun onCellClick(x: Int, y: Int) {
		if (isSuccess) return
		if (manLocation.x == x && manLocation.y == y) {
			return
		}
		// Toast("点击：$x, $y").show()
		val searchResult = PathFinder(width, height, cells, manLocation, LocationXY(x, y)).search()
		if (searchResult.isEmpty()) {
			Toast("去不了那里！").show()
		} else {
			var location = manLocation
			searchResult.forEach {
				location += it.direction
				println(location)
			}
			println("共 ${searchResult.count()} 步。")
			runAsync {
				searchResult.forEach {
					Thread.sleep(50)
					fire(MoveOrPushEvent(it.direction.dx, it.direction.dy))
				}
			}
		}
	}

	fun moveOrPush(deltaX: Int, deltaY: Int) {
		// println("移动：$deltaX, $deltaY")
		val location0 = manLocation
		val location1 = location0.delta(deltaX, deltaY)
		val location2 = location1.delta(deltaX, deltaY)

		// 防止超出范围
		if (location2.x !in 0 until width) return
		if (location2.y !in 0 until height) return

		val cell0 = cells[location0]
		val cell1 = cells[location1]
		val cell2 = cells[location2]
		when {
			// 移动
			cell1.isPassable() -> {
				val newCell1 = cell1 + cell0
				val newCell0 = cell0 - cell0
				if (newCell0 != null && newCell1 != null) {
					cells[location0] = newCell0
					cells[location1] = newCell1
					manLocation = manLocation.delta(deltaX, deltaY)
				}
			}
			// 推
			cell1.isBox() -> {
				val newCell2 = cell2 + cell1
				val newCell1 = (cell1 - cell1 ?: Cell.OUTSIDE) + cell0
				val newCell0 = cell0 - cell0
				if (newCell0 != null && newCell1 != null && newCell2 != null) {
					cells[location0] = newCell0
					cells[location1] = newCell1
					cells[location2] = newCell2
					manLocation = manLocation.delta(deltaX, deltaY)
					stepCount.value++
					checkSuccess()
				}
			}
		}
	}

	/**
	 * 检查本关是否已成功。
	 */
	private fun checkSuccess() {
		val unresolvedBoxes = internalCellList.sumBy {
			it.count { cell ->
				cell.value == Cell.BOX
			}
		}
		if (unresolvedBoxes == 0) {
			isSuccess = true
			fire(RoomSuccessEvent(stepCount.value))
			Toast("恭喜！你已完成本关！").show(2000)
		}
	}

	/**
	 * 每半秒增长1的可观察变量，用于产生一些效果。
	 */
	private val timeTick = SimpleIntegerProperty(0)

	/**
	 * 定时器，用于使人图标动起来。
	 */
	fun onTimer() {
		// println("定时器！")

		// 用下面的方法不可行，因为已绑定的属性是不可以赋值的，只好引入更复杂的绑定来实现效果了。
//		root.children.forEach {
//			if (it is ImageView) {
//				when (it.image) {
//					manImage -> manImageR
//					manImageR -> manImage
//					manDestImage -> manDestImageR
//					manDestImageR -> manDestImage
//					else -> null
//				}?.also { mirrorImage ->
//					it.image = mirrorImage
//				}
//			}
//		}

		// 让timeTick属性每半秒增长一次，通过绑定就可以实现效果了。
		if (!isSuccess) {
			timeTick.value++
		}
	}

}
