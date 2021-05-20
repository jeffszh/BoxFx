package cn.jeff.game.boxfx

/**
 * # 场景
 * 游戏中，棋盘显示的内容，称为场景。
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

	constructor(gameMap: MapManager.GameMap) : this(gameMap.width, gameMap.height) {
		loadFromMap(gameMap)
	}

	private fun loadFromMap(gameMap: MapManager.GameMap) {
		width = gameMap.width
		height = gameMap.height
		cells = gameMap.lines.map { line ->
			line.map { char ->
				Cell.fromInt("-+ .#@^".indexOf(char))
			}
		}
	}

}
