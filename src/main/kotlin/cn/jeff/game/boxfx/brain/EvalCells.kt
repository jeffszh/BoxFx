package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY

/**
 * # Evaluable Cells
 *
 * 原先的cells的类型是[ArrayXY]，只是个很简单的接口类，
 * 不方便进行各种运算，因此引入这个类，方便进行各种操作。
 */
class EvalCells(
	private val width: Int,
	private val height: Int,
	cells: ArrayXY<Cell>
) : ArrayXY<Cell> {
	private val internalCellList: Array<Array<Cell>> = (0 until height).map { y ->
		(0 until width).map { x ->
			cells[LocationXY(x, y)]
		}.toTypedArray()
	}.toTypedArray()

	override fun get(locationXY: LocationXY): Cell =
		internalCellList[locationXY.y][locationXY.x]

	override operator fun set(locationXY: LocationXY, value: Cell) {
		internalCellList[locationXY.y][locationXY.x] = value
	}

	private constructor (other: EvalCells) : this(other.width, other.height, other)

	fun clone() = EvalCells(this)

	fun <T> forAllCells(op: (location: LocationXY, evc: EvalCells) -> T): List<T> =
		(0 until height).flatMap { y ->
			(0 until width).map { x ->
				op(LocationXY(x, y), this)
			}
		}

	fun toPackedString() = forAllCells { location, evc ->
		evc[location].toChar()
	}.toCharArray().concatToString()

	override fun toString() =
		toPackedString().let { packedString ->
			(0 until height).map {
				packedString.substring(it * width, it * width + width)
			}
		}.joinToString("\n")

	override fun hashCode() =
		toPackedString().hashCode()

	override fun equals(other: Any?) = other is EvalCells &&
			toPackedString() == other.toPackedString()

	private companion object {
		private const val cellChars = "-+ .#@^"
		private fun Cell.toChar() = cellChars[ordinal]
		// private fun Char.toCell() = Cell.fromInt(cellChars.indexOf(this))
	}
}
