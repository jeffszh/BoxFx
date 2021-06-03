package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY

class SolutionFinder(
	private val width: Int,
	private val height: Int,
	cells: ArrayXY<Cell>,
	private val manLocation: LocationXY,
) {
	private val internalCellList: Array<Array<Cell>> = (0 until height).map { y ->
		(0 until width).map { x ->
			cells[LocationXY(x, y)]
		}.toTypedArray()
	}.toTypedArray()
	private val cells = object : ArrayXY<Cell> {
		override fun get(locationXY: LocationXY): Cell =
			internalCellList[locationXY.y][locationXY.x]

		override operator fun set(locationXY: LocationXY, value: Cell) {
			internalCellList[locationXY.y][locationXY.x] = value
		}
	}

	/**
	 * # Evaluable Cells
	 *
	 * 原先的[cells]的类型是[ArrayXY]，只是个很简单的接口类，
	 * 不方便进行各种运算，因此引入这个类，方便进行各种操作。
	 */
	class EvalCells : ArrayXY<Cell> {
		override fun get(locationXY: LocationXY): Cell {
			TODO("Not yet implemented")
		}

		override fun set(locationXY: LocationXY, value: Cell) {
			TODO("Not yet implemented")
		}
	}

}
