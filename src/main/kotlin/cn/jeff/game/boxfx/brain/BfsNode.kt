package cn.jeff.game.boxfx.brain

/**
 * 广度优先搜索的节点
 */
abstract class BfsNode<T>(
		val distance: Int,
		val fromLink: NodeLink<T>,
		val backLink: NodeLink<T>
) {
	interface NodeLink<T> {
		fun link(): T
	}
}
