package cn.jeff.game.boxfx

/**
 * # 格子
 * 棋盘中的一格。
 */
enum class Cell(private val flags: Int) {

	OUTSIDE(0b10001) {
		override operator fun plus(otherCell: Cell): Cell? = null
	},
	WALL(0b0001) {
		override operator fun plus(otherCell: Cell): Cell? = null
	},
	SPACE(0b0000),
	SPACE_DEST(0b1000),
	BOX(0b0101),
	BOX_DEST(0b1101),
	MAN(0b0010),
	MAN_DEST(0b1010);

//	val toInt = ordinal

	companion object {
		const val BLOCKED = 0b0001
		const val IS_MAN = 0b0010
		const val IS_BOX = 0b0100
		const val IS_DEST = 0b1000

		private val flagsMap = values().associateBy { it.flags }
		fun fromFlags(flags: Int) = flagsMap[flags]

		fun fromInt(ord: Int): Cell = values()[ord]
	}

	fun isPassable() = (flags and BLOCKED) == 0
	fun isMan() = (flags and IS_MAN) != 0
	fun isBox() = (flags and IS_BOX) != 0
	fun isDest() = (flags and IS_DEST) != 0

	/**
	 * 加法的意思是[otherCell]进入当前[Cell]，变成另一个[Cell]。
	 * 若成功，返回进入后的结果，否则返回空。
	 *
	 * @param otherCell 要进入的[Cell]
	 * @return 若操作是合法的（可以推进来），返回进来后的结果，否则为空。
	 */
	open operator fun plus(otherCell: Cell): Cell? = when {
		isPassable() ->
			fromFlags((flags and 0x08) or (otherCell.flags and 0x07))
		else -> null
	}

	/**
	 * 减法的意思是从当前[Cell]取走[otherCell]，变成另一个[Cell]。
	 *
	 * @param otherCell 要移走的[Cell]
	 * @return 若操作不合法，返回空。
	 */
	operator fun minus(otherCell: Cell): Cell? = when {
		(0 != flags and 0x06) and (flags and 0x07 == otherCell.flags and 0x07) ->
			fromFlags(flags and 0x08)
		else -> null
	}

}
