package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.utils.ArrayXY
import cn.jeff.utils.LocationXY

class PathFinder(
		private val cells: ArrayXY<Cell>,
		private val startLocation: LocationXY,
		private val destLocation: LocationXY
) {

	private class LocationNode(
			val locationXY: LocationXY,
			distance: Int,
			fromLink: NodeLink<LocationNode>,
			backLink: NodeLink<LocationNode>)
		: BfsNode<LocationNode>(distance, fromLink, backLink) {

		override fun hashCode(): Int {
			println("计算哈希。")
			return (locationXY.y shl 16) + locationXY.x
		}

		override fun equals(other: Any?): Boolean {
			if (other != null && other is LocationNode) {
				return locationXY.x == other.locationXY.x && locationXY.y == other.locationXY.y
			} else {
				return false
			}
		}
	}

	/**
	 * 正向搜索
	 */
	private class ForwardBfs : BreathFirstSearch<LocationNode>() {
		override fun LocationNode.generateNext(): List<LocationNode> {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}

		override fun LocationNode.checkDone(): Boolean {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
	}

	private val dummyLink = object : BfsNode.NodeLink<LocationNode> {
		override fun link(): LocationNode {
			error("根节点不能访问link！")
		}
	}

	fun search() {
		ForwardBfs().search(LocationNode(
				startLocation, 0,
				dummyLink, dummyLink
		))
	}

}
