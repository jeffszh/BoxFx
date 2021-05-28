package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.game.boxfx.brain.BfsNode.Companion.dummyLink
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY

class PathFinder(
		private val width: Int,
		private val height: Int,
		private val cells: ArrayXY<Cell>,
		private val startLocation: LocationXY,
		private val destLocation: LocationXY
) {

	enum class Direction(val dx: Int, val dy: Int) {
		LEFT(-1, 0),
		RIGHT(1, 0),
		UP(0, -1),
		DOWN(0, 1),
	}

	operator fun LocationXY.plus(dir: Direction) =
			LocationXY(x + dir.dx, y + dir.dy)

	private class LocationNode(
			val locationXY: LocationXY,
			distance: Int,
			fromLink: (LocationXY) -> LocationXY,
			backLink: () -> LocationNode)
		: BfsNode<LocationNode, LocationXY>(distance, fromLink, backLink) {

		override fun hashCode(): Int {
			// println("计算哈希。")
			return locationXY.hashCode()
		}

		override fun equals(other: Any?): Boolean =
				other is LocationNode && locationXY == other.locationXY
	}

	/**
	 * 正向搜索
	 */
	private inner class ForwardBfs : BreathFirstSearch<LocationNode, LocationXY>() {
		init {
			name = "正向搜索"
			onNewLevel = {
				println("--------------------------------------------------")
				for (y in 0 until height) {
					for (x in 0 until width) {
						print(
								when (LocationXY(x, y)) {
									in searchingNodes.map {
										it.locationXY
									} -> 'S'
									in searchedNodes.map {
										it.locationXY
									} -> 'D'
									else -> '-'
								}
						)
					}
					println()
				}
				println("--------------------------------------------------")
			}
		}

		override fun LocationNode.generateNext(): List<LocationNode> {
			println("生成下一層節點……")
			val adjacencyLocations = Direction.values().associateWith {
				locationXY + it
			}.filter { (_, v) ->
				v.x in 0 until width && v.y in 0 until height
			}.filter { (_, v) ->
				cells[v].isPassable()
			}
			return adjacencyLocations.map { (k, v) ->
				LocationNode(v, distance + 1, {
					it + k
				}) {
					this
				}
			}
		}

		override fun LocationNode.checkDone() =
				locationXY == destLocation
	}

	fun search() = ForwardBfs().search(root = LocationNode(
			startLocation, 0,
			{ it }, dummyLink
	))

}
