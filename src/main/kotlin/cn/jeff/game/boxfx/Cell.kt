package cn.jeff.game.boxfx

/**
 * 这个类代表棋盘中的一格。
 */
enum class Cell {

	OUTSIDE {
		override fun isPassable() = false
	},
	WALL {
		override fun isPassable() = false
	},
	SPACE {
		override fun isPassable() = true
	},
	SPACE_DEST {
		override fun isPassable() = true
	},
	BOX {
		override fun isPassable() = false
	},
	BOX_DEST {
		override fun isPassable() = false
	},
	MAN {
		override fun isPassable() = true
	},
	MAN_DEST {
		override fun isPassable() = true
	};

//	val toInt = ordinal

	companion object {
		fun fromInt(ord: Int): Cell = values()[ord]
	}

	abstract fun isPassable(): Boolean

}
