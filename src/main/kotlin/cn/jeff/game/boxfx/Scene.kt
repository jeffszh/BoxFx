package cn.jeff.game.boxfx

/**
 * # 场景
 * 游戏中，棋盘显示的内容，称为场景。
 * 名称跟[javafx.scene.Scene]相同，但由于[tornadofx.App]很少用到[javafx.scene.Scene]，就不改名了。
 *
 * @property width 宽度
 * @property height 高度
 */
class Scene(width: Int, height: Int) {

	var width = width
		private set
	var height = height
		private set

	var cells = List(height) { List(width) { Cell.OUTSIDE } }
		private set

	constructor(room: RoomManager.Room) : this(room.width, room.height) {
		loadInitialScene(room)
	}

	private fun loadInitialScene(room: RoomManager.Room) {
		width = room.width
		height = room.height
		cells = room.lines.map { line ->
			line.map { char ->
				Cell.fromInt("-+ .#@^".indexOf(char))
			}
		}
	}

}
