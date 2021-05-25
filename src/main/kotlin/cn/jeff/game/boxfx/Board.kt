package cn.jeff.game.boxfx

import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
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

	private val imageViews = mutableListOf<MutableList<ImageView>>()

	private var manLocation = LocationXY(0, 0)
	private val cells = object : ArrayXY<Cell> {
		override fun get(locationXY: LocationXY): Cell =
				scene.cells[locationXY.y][locationXY.x]
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
		imageViews.clear()
		println("加载scene，宽度=${scene.width}，高度=${scene.height}")
		scene.cells.forEachIndexed { y, cellList ->
			root.row {
				val lineOfImageViews = mutableListOf<ImageView>()
				cellList.forEachIndexed { x, cell ->
					lineOfImageViews.add(imageview(cell.toImage()))

					// 顺便找到人的位置
					if (cell.isMan()) {
						manLocation = LocationXY(x, y)
					}
				}
				imageViews.add(lineOfImageViews)
			}
		}
	}

	private fun processKey(k: KeyEvent) {
		// println(k)
		when (k.code) {
			KeyCode.UP -> move(0, -1)
			KeyCode.DOWN -> move(0, 1)
			KeyCode.LEFT -> move(-1, 0)
			KeyCode.RIGHT -> move(1, 0)
			else -> {
				// do nothing
			}
		}
	}

	private fun move(deltaX: Int, deltaY: Int) {
		// println("移动：$deltaX, $deltaY")
		val location0 = manLocation
		val location1 = LocationXY(location0.x + deltaX, location0.y + deltaY)
		if (location1.x !in 0 until width) return
		if (location1.y !in 0 until height) return
		val cell0 = cells[location0]
		val cell1 = cells[location1]
		if (cell1.isPassable()) {
		}
	}

}
