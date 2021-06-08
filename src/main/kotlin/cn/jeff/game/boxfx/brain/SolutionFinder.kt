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

	private fun EvalCells.performPush(push: BoxOperation.Push) {
		val manLocation = push.manLocation
		val boxLocation = manLocation + push.pushDirection
		val destLocation = boxLocation + push.pushDirection
		this[manLocation] = Cell.SPACE
		this[boxLocation] = Cell.MAN
		this[destLocation] = Cell.BOX
	}

	private fun EvalCells.performPull(pull: BoxOperation.Pull) {
		val manLocation = pull.manLocation
		val boxLocation = manLocation - pull.pullDirection
		val newManLocation = manLocation + pull.pullDirection
		this[boxLocation] = Cell.SPACE
		this[manLocation] = Cell.BOX
		this[newManLocation] = Cell.MAN
	}

	private fun interface CanReappearCells {
		fun reappearCells(): EvalCells
		fun calcHash() = reappearCells().toPackedString().hashCode()
		fun cellsEquals(other: Any?) =
			other is CanReappearCells &&
					reappearCells().toPackedString() ==
					other.reappearCells().toPackedString()
	}

	private inner class ForwardLink(val push: BoxOperation.Push) : (ForwardNode) -> ForwardNode {
		override fun invoke(p1: ForwardNode) = ForwardNode(
			p1.distance + 1,
			this,
			p1
		)
	}

	private inner class ForwardNode(
		distance: Int,
		fromLink: ForwardLink?,
		fromNode: ForwardNode?
	) : BfsNode<ForwardNode, ForwardLink>(distance, fromLink, fromNode), CanReappearCells {

		/**
		 * # 重现Cells
		 * 根据推的路径，重现Cells的状态。
		 * @return 本节点对应的Cells。
		 */
		override fun reappearCells(): EvalCells {
			val fromLinkList = mutableListOf<ForwardLink>()
			var n1 = this
			while (n1.fromNode != null) {
				fromLinkList.add(n1.fromLink!!)
				n1 = n1.fromNode!!
			}
			val pushList = fromLinkList.map {
				it.push
			}.reversed()
			return startingCells.clone().also { evc ->
				pushList.forEach { push ->
					evc.performPush(push)
				}
				pushList.lastOrNull()?.let {
					evc.normalize()
					evc.expandMan(it.manLocation + it.pushDirection)
				}
			}
		}

		private val hashCode by lazy { calcHash() }
		override fun hashCode() = hashCode
		override fun equals(other: Any?) = cellsEquals(other)

	}

	private inner class ForwardSearch : BreathFirstSearch<ForwardNode, ForwardLink>() {
		override fun ForwardNode.generateNext(): List<ForwardNode> {
			val evc = reappearCells()
			// 找所有箱子
			val boxLocationList = evc.forAllCells { location, evc1 ->
				if (evc1[location].isBox()) {
					location
				} else {
					null
				}
			}.filterNotNull()
			// 然后找旁边有MAN的，并且推的方向可以通过的，生成推列表。
			val pushList = boxLocationList.flatMap { boxLocation ->
				Direction.values().mapNotNull { direction ->
					if (evc[boxLocation - direction] == Cell.MAN &&
						evc[boxLocation + direction].isPassable()
					) {
						BoxOperation.Push(boxLocation - direction, direction)
					} else {
						null
					}
				}
			}
			// 根据推列表生成下级节点
			return pushList.map { push ->
				ForwardNode(distance + 1, ForwardLink(push), this)
			}
		}

		override fun ForwardNode.checkDone(): Boolean {
			if (this == matchPoint) {
				return true
			}
			if (this in (backwardSearch.searchingNodes as HashSet<CanReappearCells>)) {
				matchPoint = this
				return true
			}
			return false
		}

	}

	private val forwardSearch = ForwardSearch()

	private inner class BackwardLink(val pull: BoxOperation.Pull) : (BackwardNode) -> BackwardNode {
		override fun invoke(p1: BackwardNode) = BackwardNode(
			p1.distance + 1,
			this,
			p1
		)
	}

	private open inner class BackwardNode(
		distance: Int,
		fromLink: BackwardLink?,
		fromNode: BackwardNode?
	) : BfsNode<BackwardNode, BackwardLink>(distance, fromLink, fromNode), CanReappearCells {

		override fun reappearCells(): EvalCells {
			val fromLinkList = mutableListOf<BackwardLink>()
			var n1 = this
			while (n1.fromNode != null) {
				fromLinkList.add(n1.fromLink!!)
				n1 = n1.fromNode!!
			}
			val pullList = fromLinkList.map {
				it.pull
			}.reversed()
			// 运行到这里，n1一定是其中一个根节点了。
			return n1.reappearCells().clone().also { evc ->
				pullList.forEach { pull ->
					evc.performPull(pull)
				}
				pullList.lastOrNull()?.let {
					evc.normalize()
					evc.expandMan(it.manLocation + it.pullDirection)
				}
			}
		}

		private val hashCode by lazy { calcHash() }
		override fun hashCode() = hashCode
		override fun equals(other: Any?) = cellsEquals(other)

	}

	private inner class BackwardRootNode(val cells: EvalCells) :
		BackwardNode(0, null, null) {
		override fun reappearCells() = cells
	}

	private class BackwardSearch : BreathFirstSearch<BackwardNode, BackwardLink>() {
		override fun BackwardNode.generateNext(): List<BackwardNode> {
			TODO("Not yet implemented")
		}

		override fun BackwardNode.checkDone(): Boolean {
			TODO("Not yet implemented")
		}
	}

	private var matchPoint: CanReappearCells? = null

	private val backwardSearch = BackwardSearch()

}
