package cn.jeff.game.boxfx

/**
 * # 格子
 * 棋盘中的一格。
 */
enum class Cell {

	OUTSIDE {
		override fun isPassable() = false
		override fun isMan() = false
		override fun isBox() = false
	},
	WALL {
		override fun isPassable() = false
		override fun isMan() = false
		override fun isBox() = false
	},
	SPACE {
		override fun isPassable() = true
		override fun isMan() = false
		override fun isBox() = false
	},
	SPACE_DEST {
		override fun isPassable() = true
		override fun isMan() = false
		override fun isBox() = false
	},
	BOX {
		override fun isPassable() = false
		override fun isMan() = false
		override fun isBox() = true
	},
	BOX_DEST {
		override fun isPassable() = false
		override fun isMan() = false
		override fun isBox() = true
	},
	MAN {
		override fun isPassable() = true
		override fun isMan() = true
		override fun isBox() = false
	},
	MAN_DEST {
		override fun isPassable() = true
		override fun isMan() = true
		override fun isBox() = false
	};

//	val toInt = ordinal

	companion object {
		fun fromInt(ord: Int): Cell = values()[ord]
	}

	abstract fun isPassable(): Boolean
	abstract fun isMan(): Boolean
	abstract fun isBox(): Boolean

}
