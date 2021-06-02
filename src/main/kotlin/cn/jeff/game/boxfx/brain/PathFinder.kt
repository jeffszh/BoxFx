package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.game.boxfx.brain.BfsNode.Companion.dummyLink
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

class PathFinder(
	private val width: Int,
	private val height: Int,
	private val cells: ArrayXY<Cell>,
	private val startLocation: LocationXY,
	private val destLocation: LocationXY
) {

	enum class Direction(val dx: Int, val dy: Int) : (LocationXY) -> LocationXY {
		LEFT(-1, 0) {
			override val inverseOperation get() = RIGHT
		},
		RIGHT(1, 0) {
			override val inverseOperation get() = LEFT
		},
		UP(0, -1) {
			override val inverseOperation get() = DOWN
		},
		DOWN(0, 1) {
			override val inverseOperation get() = UP
		},
		;

		override fun invoke(p1: LocationXY) =
			LocationXY(p1.x + dx, p1.y + dy)

		abstract val inverseOperation: Direction
	}

	operator fun LocationXY.plus(dir: Direction) =
		dir(this)

	private class LocationNode(
		val locationXY: LocationXY,
		distance: Int,
		fromLink: (LocationXY) -> LocationXY,
		backLink: () -> LocationNode
	) : BfsNode<LocationNode, LocationXY>(distance, fromLink, backLink) {

		override fun hashCode(): Int {
			// println("计算哈希。")
			return locationXY.hashCode()
		}

		override fun equals(other: Any?): Boolean =
			other is LocationNode && locationXY == other.locationXY
	}

	private fun calcAdjacencyLocations(
		locationXY: LocationXY
	) = Direction.values().associateWith {
		locationXY + it
	}.filter { (_, v) ->
		v.x in 0 until width && v.y in 0 until height
	}.filter { (_, v) ->
		cells[v].isPassable()
	}

	private val forwardSearch: BreathFirstSearch<LocationNode, LocationXY> = object :
		BreathFirstSearch<LocationNode, LocationXY>() {
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

	private val backwardSearch: BreathFirstSearch<LocationNode, LocationXY> = object :
		BreathFirstSearch<LocationNode, LocationXY>() {
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

	fun search() = runBlocking {
		val forwardSearchResult = async {
			forwardSearch.search(
				root = LocationNode(
					startLocation, 0,
					{ it }, dummyLink
				)
			)
		}
		val backwardSearchResult = async {
			backwardSearch.search(
				root = LocationNode(
					destLocation, 0,
					{ it }, dummyLink
				)
			)
		}
		val result = awaitAll(forwardSearchResult, backwardSearchResult)
		return@runBlocking result[0] + result[1].reversed().map {
			if (it is Direction) {
				it.inverseOperation
			} else {
				error("内部类型出错！")
			}
		}
	}

}
