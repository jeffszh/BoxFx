package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY

class SolutionFinder(
	width: Int,
	height: Int,
	cells: ArrayXY<Cell>,
	manLocation: LocationXY,
) {

	private val startingCells: EvalCells = EvalCells(width, height, cells)
	private val endingCells: Set<EvalCells>
	private val destLocations: Set<LocationXY> = startingCells.collectDestSet()

	init {
		startingCells.normalize()
		val normalizedEndingCells = startingCells.clone()
		normalizedEndingCells.forAllCells { location, evc ->
			// 去掉原先的箱子
			if (evc[location] == Cell.BOX) {
				evc[location] = Cell.SPACE
			}
			// 在终点位置摆放箱子
			if (location in destLocations) {
				evc[location] = Cell.BOX
			}
		}
		startingCells.expandMan(manLocation)
		val allSpaceLocation = normalizedEndingCells.forAllCells { location, evc ->
			if (evc[location] == Cell.SPACE) {
				location
			} else {
				null
			}
		}.filterNotNull()

		// 找所有可能的结局状态，过程中暂时将搜过的状态的MAN变成MAN_DEST。
		val candidateEndingCells = allSpaceLocation.mapNotNull {
			if (normalizedEndingCells[it] == Cell.SPACE) {
				normalizedEndingCells.expandMan(it)
				val oneOfEnding = normalizedEndingCells.clone()
				normalizedEndingCells.forAllCells { location, evc ->
					if (evc[location] == Cell.MAN) {
						evc[location] = Cell.MAN_DEST
					}
				}
				oneOfEnding
			} else {
				null
			}
		}

		// 找完了之后，把过程中暂时变成MAN_DEST的位置恢复为SPACE。
		candidateEndingCells.forEach {
			it.forAllCells { location, evc ->
				if (evc[location] == Cell.MAN_DEST) {
					evc[location] = Cell.SPACE
				}
			}
		}
		endingCells = candidateEndingCells.toSet()
	}

	/**
	 * 搜集所有的终点。
	 */
	private fun EvalCells.collectDestSet(): Set<LocationXY> =
		forAllCells { location, evc ->
			if (evc[location].isDest()) {
				location
			} else {
				null
			}
		}.filterNotNull().toSet()

	/**
	 * 去掉终点和人位置的信息，归一化。
	 */
	private fun EvalCells.normalize() {
		forAllCells { location, evc ->
			when (evc[location]) {
				Cell.SPACE_DEST -> {
					evc[location] = Cell.SPACE
				}
				Cell.BOX_DEST -> {
					evc[location] = Cell.BOX
				}
				Cell.MAN, Cell.MAN_DEST -> {
					evc[location] = Cell.SPACE
				}
				else -> {
					// do nothing
				}
			}
		}
	}

	/**
	 * 将man的位置扩展为整个可到达的区域，达到归一化效果。
	 */
	private fun EvalCells.expandMan(manLocation: LocationXY) {
		if (get(manLocation) == Cell.SPACE) {
			set(manLocation, Cell.MAN)
			Direction.values().forEach {
				// 简单起见，直接用非尾递归的深度搜索。
				expandMan(manLocation + it)
			}
		}
	}

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

	fun test() {
		println("-----------------")
		println(startingCells)
		println("-----------------")
		destLocations.forEach {
			println(it)
		}
		println("-----------------")
		endingCells.forEach {
			println(it)
		}
		println("-----------------")
	}

}
