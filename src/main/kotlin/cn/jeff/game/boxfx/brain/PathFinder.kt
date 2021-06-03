package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.game.boxfx.brain.BfsNode.Companion.dummyLink
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/**
 * # 路径寻找器
 * 用于在棋盘中寻找移动路径。
 *
 * @property width 棋盘的宽度
 * @property height 棋盘的高度
 * @property cells 棋盘格
 * @property startLocation 起点
 * @property destLocation 终点
 */
class PathFinder(
	private val width: Int,
	private val height: Int,
	private val cells: ArrayXY<Cell>,
	private val startLocation: LocationXY,
	private val destLocation: LocationXY
) {

	operator fun LocationXY.plus(dir: Direction) =
		dir(this)

	private class LocationNode(
		val locationXY: LocationXY,
		distance: Int,
		fromLink: Direction,
		backLink: () -> LocationNode
	) : BfsNode<LocationNode, LocationXY, Direction>(distance, fromLink, backLink) {

		override fun hashCode(): Int {
			// println("计算哈希。")
			return locationXY.hashCode()
		}

		override fun equals(other: Any?): Boolean =
			other is LocationNode && locationXY == other.locationXY
	}

	private fun calcAdjacencyLocations(
		locationXY: LocationXY
	) = Direction.meaningfulValues.associateWith {
		locationXY + it
	}.filter { (_, v) ->
		v.x in 0 until width && v.y in 0 until height
	}.filter { (_, v) ->
		cells[v].isPassable()
	}

	private val forwardSearch: BreathFirstSearch<LocationNode, LocationXY, Direction> = object :
		BreathFirstSearch<LocationNode, LocationXY, Direction>() {
		init {
			name = "正向搜索"
			onNewLevel = { level, nodeCount ->
				if (nodeCount >
					backwardSearch.searchingNodes.count() +
					backwardSearch.searchedNodes.count()
				) {
					println("$name 暂停搜索第 $level 层。")
					yield()
				}
			}
		}

		override fun LocationNode.generateNext() = calcAdjacencyLocations(locationXY)
			.map { (direction, newLocation) ->
				LocationNode(newLocation, distance + 1, direction) {
					this
				}
			}

		override fun LocationNode.checkDone(): Boolean {
			if (locationXY == matchPoint) {
				return true
			}
			if (backwardSearch.searchingNodes.contains(this)) {
				matchPoint = locationXY
				return true
			}
			return false
		}
	}

	private val backwardSearch: BreathFirstSearch<LocationNode, LocationXY, Direction> = object :
		BreathFirstSearch<LocationNode, LocationXY, Direction>() {
		init {
			name = "反向搜索"
			onNewLevel = { level, nodeCount ->
				if (nodeCount >
					forwardSearch.searchingNodes.count() +
					forwardSearch.searchedNodes.count()
				) {
					println("$name 暂停搜索第 $level 层。")
					yield()
				}
			}
		}

		override fun LocationNode.generateNext() = calcAdjacencyLocations(locationXY)
			.map { (direction, newLocation) ->
				LocationNode(newLocation, distance + 1, direction) {
					this
				}
			}

		override fun LocationNode.checkDone(): Boolean {
			if (locationXY == matchPoint) {
				return true
			}
			if (forwardSearch.searchingNodes.contains(this)) {
				matchPoint = locationXY
				return true
			}
			return false
		}
	}

	private var matchPoint: LocationXY? = null

	/**
	 * # 寻找路径
	 * 寻找从[startLocation]到[destLocation]的最短路径。
	 * @return 若成功，返回移动方法的列表；若失败，返回空列表。
	 */
	fun search() = runBlocking {
		val forwardSearchResult = async {
			forwardSearch.search(
				root = LocationNode(
					startLocation, 0,
					Direction.DUMMY, dummyLink
				)
			)
		}
		val backwardSearchResult = async {
			backwardSearch.search(
				root = LocationNode(
					destLocation, 0,
					Direction.DUMMY, dummyLink
				)
			)
		}
		val result = awaitAll(forwardSearchResult, backwardSearchResult)
		return@runBlocking result[0] + result[1].reversed().map {
			it.inverseOperation
		}
	}

}
