package cn.jeff.game.boxfx

import javafx.scene.image.Image
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

	override val root = gridpane()

	fun setScene(scene: Scene) {
		root.clear()
		root.row {}
		println("加载scene，宽度=${scene.width}，高度=${scene.height}")
		scene.cells.forEach { cellList ->
			root.row {
				cellList.forEach { cell ->
					imageview(cell.toImage())
				}
			}
		}
		root.row {}
	}

}
