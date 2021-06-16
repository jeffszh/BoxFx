package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

class SolutionFinder(
	width: Int,
	height: Int,
	cells: ArrayXY<Cell>,
	manLocation: LocationXY,
) {

	private val startingCells: EvalCells = EvalCells(width, height, cells)
	private val endingCells: Set<EvalCells>
	private val destLocations: Set<LocationXY> = startingCells.collectDestSet()
	private val deadArea: Set<LocationXY>

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

		// 找所有死角
		val deadCorners = startingCells.forAllCells { location, evc ->
			if (location in destLocations || evc[location] in setOf(Cell.WALL, Cell.OUTSIDE)) {
				null
			} else {
				location
			}
		}.filterNotNull().filter { location ->
			Direction.values().any { dir ->
				EightTrigrams.check(location, dir) {
					heaven {
						startingCells[it] == Cell.WALL
					}
					water {
						startingCells[it] == Cell.WALL
					}
				}
			}
		}

		// 找所有死边
		// 先横向找
		val horizontalDeadBorders = (0 until height).flatMap { y ->
			val deadCornersInLine = deadCorners.filter { it.y == y }.sortedBy { it.x }
			if (deadCornersInLine.count() >= 2) {
				(0 until deadCornersInLine.count() - 1).flatMap { i ->
					val p1 = deadCornersInLine[i]
					val p2 = deadCornersInLine[i + 1]
					val straightInSight = (p1.x..p2.x).all { x ->
						LocationXY(x, y) !in destLocations &&
								startingCells[LocationXY(x, y)] != Cell.WALL
					}
					val upSideWallAllWall = (p1.x..p2.x).all { x ->
						startingCells[LocationXY(x, y - 1)] == Cell.WALL
					}
					val downSideWallAllWall = (p1.x..p2.x).all { x ->
						startingCells[LocationXY(x, y + 1)] == Cell.WALL
					}
					if (straightInSight && (upSideWallAllWall || downSideWallAllWall)) {
						(p1.x..p2.x).map { x ->
							LocationXY(x, y)
						}
					} else {
						emptyList()
					}
				}
			} else {
				emptyList()
			}
		}
		// 再纵向找
		val verticalDeadBorders = (0 until width).flatMap { x ->
			val deadCornersInColumn = deadCorners.filter { it.x == x }.sortedBy { it.y }
			if (deadCornersInColumn.count() >= 2) {
				(0 until deadCornersInColumn.count() - 1).flatMap { i ->
					val p1 = deadCornersInColumn[i]
					val p2 = deadCornersInColumn[i + 1]
					val straightInSight = (p1.y..p2.y).all { y ->
						LocationXY(x, y) !in destLocations &&
								startingCells[LocationXY(x, y)] != Cell.WALL
					}
					val leftSideAllWall = (p1.y..p2.y).all { y ->
						startingCells[LocationXY(x - 1, y)] == Cell.WALL
					}
					val rightSideAllWall = (p1.y..p2.y).all { y ->
						startingCells[LocationXY(x + 1, y)] == Cell.WALL
					}
					if (straightInSight && (leftSideAllWall || rightSideAllWall)) {
						(p1.y..p2.y).map { y ->
							LocationXY(x, y)
						}
					} else {
						emptyList()
					}
				}
			} else {
				emptyList()
			}
		}

		deadArea = (deadCorners + horizontalDeadBorders + verticalDeadBorders).toSet()

		for (y in 0 until height) {
			for (x in 0 until width) {
				val loc = LocationXY(x, y)
				print(
					if (loc in deadArea) {
						"X"
					} else {
						"-+ .#@^"[startingCells[loc].ordinal]
					}
				)
			}
			println()
		}
		println("死点数量：${deadArea.count()}")
	}

	companion object {
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

	class EvcLink(val pushOrPull: BoxOperation) : (EvcNode) -> EvcNode {
		override fun invoke(p1: EvcNode) = EvcNode(
			p1.distance + 1,
			this,
			p1
		)
	}

	open class EvcNode(
		distance: Int,
		fromLink: EvcLink?,
		fromNode: EvcNode?
	) : BfsNode<EvcNode, EvcLink>(distance, fromLink, fromNode) {
		private val hashCode by lazy { calcHash() }
		private fun calcHash() = reappearCells().toPackedString().hashCode()
		override fun hashCode() = hashCode
		override fun equals(other: Any?) = other is EvcNode &&
				reappearCells().toPackedString() ==
				other.reappearCells().toPackedString()

		open fun reappearCells(): EvalCells {
			val fromLinkList = mutableListOf<EvcLink>()
			var n1 = this
			while (n1.fromNode != null) {
				fromLinkList.add(n1.fromLink!!)
				n1 = n1.fromNode!!
			}
			val pushOrPullList = fromLinkList.map {
				it.pushOrPull
			}.reversed()
			// 运行到这里，n1一定是其中一个根节点了。
			return n1.reappearCells().clone().also { evc ->
				pushOrPullList.forEach { pushOrPull ->
					pushOrPull(evc)
				}
				pushOrPullList.lastOrNull()?.let {
					evc.normalize()
					evc.expandMan(it.manLocation + it.direction)
				}
			}
		}
	}

	private inner class ForwardSearch : BreathFirstSearch<EvcNode, EvcLink>() {
		override fun EvcNode.generateNext(): List<EvcNode> {
			if (Thread.currentThread().isInterrupted) {
				return emptyList()
			}
			val evc = reappearCells()
			if (isDead(evc, fromLink)) {
				return emptyList()
			}
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
						evc[boxLocation + direction].isPassable() &&
						boxLocation + direction !in deadArea
					) {
						BoxOperation.Push(boxLocation - direction, direction)
					} else {
						null
					}
				}
			}
			// 根据推列表生成下级节点
			return pushList.map { push ->
				EvcNode(distance + 1, EvcLink(push), this)
			}
		}

		private fun isDead(evc: EvalCells, fromLink: EvcLink?): Boolean {
			if (fromLink == null) {
				return false
			}
			val push = fromLink.pushOrPull as BoxOperation.Push
			val center = push.manLocation + push.direction + push.direction
			return /*EightTrigrams.checkEitherOr(center, push.direction) {
				// 判斷推入墻角的情況
				heaven { location ->
					center !in destLocations && evc[location] == Cell.WALL
				}
				water { location ->
					evc[location] == Cell.WALL
				}
			} || */EightTrigrams.checkEitherOr(center, push.direction) {
				// 判斷聚四的情況
				var hasUnresolvedBox = false
				heaven { location ->
					if (center !in destLocations) hasUnresolvedBox = true
					when (evc[location]) {
						Cell.BOX -> {
							if (location !in destLocations) hasUnresolvedBox = true
							true
						}
						Cell.WALL -> true
						else -> false
					}
				}
				wind { location ->
					when (evc[location]) {
						Cell.BOX -> {
							if (location !in destLocations) hasUnresolvedBox = true
							true
						}
						Cell.WALL -> true
						else -> false
					}
				}
				water { location ->
					when (evc[location]) {
						Cell.BOX -> {
							if (location !in destLocations) hasUnresolvedBox = true
							true
						}
						Cell.WALL -> true
						else -> false
					} && hasUnresolvedBox
				}
			} || EightTrigrams.checkEitherOr(center, push.direction) {
				// 判断折四之一：横竖横
				heaven { location ->
					(center !in destLocations || location !in destLocations) &&
							evc[location] == Cell.BOX
				}
				swamp { location ->
					evc[location] == Cell.WALL
				}
				water { location ->
					evc[location] == Cell.WALL
				}
			} || EightTrigrams.checkEitherOr(center, push.direction) {
				// 判断折四之二：竖横竖
				water { location ->
					(center !in destLocations || location !in destLocations) &&
							evc[location] == Cell.BOX
				}
				heaven { location ->
					evc[location] == Cell.WALL
				}
				mountain { location ->
					evc[location] == Cell.WALL
				}
			}
		}

		/**
		 * 原先的代码（已注释掉）有bug，如果[matchPoint]已经非空（另一个方向的搜索找到），
		 * 但跟本节点不相同，那么，在第二个if里面可能又会找到另一个[matchPoint]，
		 * 于是，正反两条路径就错开了，得不出正确的结果。
		 * 修正：当[matchPoint]已经存在，不再进行第二个判断。
		 */
		override fun EvcNode.checkDone(): Boolean {
			// 以下是有bug的代码
//			if (this == matchPoint) {
//				return true
//			}
//			if (this in backwardSearch.searchingNodes) {
//				matchPoint = this
//				return true
//			}
//			return false
			// 以下是修正后的代码
			if (matchPoint != null) {
				return this == matchPoint
			}
			if (this in backwardSearch.searchingNodes) {
				matchPoint = this
				return true
			}
			return false
		}

		init {
			name = "推 --- "
			onNewLevel = { level, nodeCount ->
				if (level > 0) {
					forwardCost = nodeCount / level
					if (forwardCost > backwardCost) {
						println("$name 暂停搜索第 $level 层。")
						yield()
					}
				}
			}
		}
	}

	private val forwardSearch = ForwardSearch()
	private var forwardCost = 0

	/**
	 * # 根节点
	 * 其他节点是从根节点通过计算一系列推或拉的动作，重现出cells，
	 * 而根节点必须独立提供[cells]的初始状态。
	 * @property cells 初始cells状态，对于正向搜索，是起点状态；对于反向搜索是其中一个终点状态。
	 */
	private class RootNode(private val cells: EvalCells) :
		EvcNode(0, null, null) {
		override fun reappearCells() = cells
	}

	private inner class BackwardSearch : BreathFirstSearch<EvcNode, EvcLink>() {
		override fun EvcNode.generateNext(): List<EvcNode> {
			if (Thread.currentThread().isInterrupted) {
				return emptyList()
			}
			val evc = reappearCells()
			// 找所有箱子
			val boxLocationList = evc.forAllCells { location, evc1 ->
				if (evc1[location].isBox()) {
					location
				} else {
					null
				}
			}.filterNotNull()
			// 然后找旁边有MAN的，并且拉的方向可以通过的，生成拉列表。
			val pullList = boxLocationList.flatMap { boxLocation ->
				Direction.values().mapNotNull { direction ->
					if (evc[boxLocation + direction] == Cell.MAN &&
						evc[boxLocation + direction + direction].isPassable()
					) {
						BoxOperation.Pull(boxLocation + direction, direction)
					} else {
						null
					}
				}
			}
			// 根据拉列表生成下级节点
			return pullList.map { pull ->
				EvcNode(distance + 1, EvcLink(pull), this)
			}
		}

		override fun EvcNode.checkDone(): Boolean {
			if (this == matchPoint) {
				return true
			}
			// 若matchPoint已经存在，不要进行后续判断。
			if (matchPoint != null) {
				return false
			}
			if (this in forwardSearch.searchingNodes) {
				matchPoint = this
				return true
			}
			return false
		}

		init {
			name = "拉 --- "
			onNewLevel = { level, nodeCount ->
				if (level > 0) {
					backwardCost = nodeCount / level
					if (backwardCost > forwardCost) {
						println("$name 暂停搜索第 $level 层。")
						yield()
					}
				}
			}
		}
	}

	private val backwardSearch = BackwardSearch()
	private var backwardCost = 0

	private var matchPoint: EvcNode? = null

	fun search(
		reportForwardSearch: (level: Int, nodeCount: Int) -> Unit,
		reportBackwardSearch: (level: Int, nodeCount: Int) -> Unit,
	) = runBlocking {
		val oldForwardOnNewLevel = forwardSearch.onNewLevel
		forwardSearch.onNewLevel = { level, nodeCount ->
			reportForwardSearch(level, nodeCount)
			oldForwardOnNewLevel(level, nodeCount)
		}
		val oldBackwardOnNewLevel = backwardSearch.onNewLevel
		backwardSearch.onNewLevel = { level, nodeCount ->
			reportBackwardSearch(level, nodeCount)
			oldBackwardOnNewLevel(level, nodeCount)
		}
		val forwardSearchResult = async {
			forwardSearch.search(
				RootNode(startingCells)
			)
		}
		val backwardSearchResult = async {
			backwardSearch.search(
				* endingCells.map {
					RootNode(it)
				}.toTypedArray()
			)
		}
		val result = awaitAll(forwardSearchResult, backwardSearchResult)
		return@runBlocking result[0] + result[1].reversed().map {
			EvcLink((it.pushOrPull as BoxOperation.Pull).inverseOperation())
		}
	}

}
