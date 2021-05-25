package cn.jeff.game.boxfx

import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.*

/**
 * # 棋盘
 *
 * 用于显示游戏画面中央的主体部分，可理解为棋盘。
 */
class Board : View() {

	private val cellImgMap = mapOf(
			Cell.OUTSIDE to
					Image("/img/out_side.png"),
			Cell.WALL to
					Image("/img/wall.png"),
			Cell.SPACE to
					Image("/img/space.png"),
			Cell.SPACE_DEST to
					Image("/img/space_dest.png"),
			Cell.BOX to
					Image("/img/box.png"),
			Cell.BOX_DEST to
					Image("/img/box_dest.png"),
			Cell.MAN to
					Image("/img/man.png"),
			Cell.MAN_DEST to
					Image("/img/man_dest.png")
	)

	private fun Cell.toImage(): Image =
			cellImgMap[this] ?: kotlin.error("不可能")

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
	private val cells = object : ArrayXY<ObjectProperty<Cell>> {
		override fun get(locationXY: LocationXY): ObjectProperty<Cell> =
				internalCellList[locationXY.y][locationXY.x]
	}

	var scene: Scene = Scene(0, 0)
		set(value) {
			field = value
			internalSetScene(value)
		}
	private val width get() = scene.width
	private val height get() = scene.height

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
						imageProperty().bind(cell.objectBinding {
							it?.toImage()
						})
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

	private fun LocationXY.delta(dx: Int, dy: Int) =
			LocationXY(x + dx, y + dy)

	private fun moveOrPush(deltaX: Int, deltaY: Int) {
		// println("移动：$deltaX, $deltaY")
		val location0 = manLocation
		val location1 = location0.delta(deltaX, deltaY)
		val location2 = location1.delta(deltaX, deltaY)

		// 防止超出范围
		if (location2.x !in 0 until width) return
		if (location2.y !in 0 until height) return

		val cell0 = cells[location0].value
		val cell1 = cells[location1].value
		val cell2 = cells[location2].value
		when {
			// 移动
			cell1.isPassable() -> {
				val newCell1 = cell1 + cell0
				val newCell0 = cell0 - cell0
				if (newCell0 != null && newCell1 != null) {
					cells[location0].value = newCell0
					cells[location1].value = newCell1
					manLocation = manLocation.delta(deltaX, deltaY)
				}
			}
			// 推
			cell1.isBox() -> {
				val newCell2 = cell2 + cell1
				val newCell1 = (cell1 - cell1 ?: Cell.OUTSIDE) + cell0
				val newCell0 = cell0 - cell0
				if (newCell0 != null && newCell1 != null && newCell2 != null) {
					cells[location0].value = newCell0
					cells[location1].value = newCell1
					cells[location2].value = newCell2
					manLocation = manLocation.delta(deltaX, deltaY)
				}
			}
		}
	}

}
