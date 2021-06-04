package cn.jeff.game.boxfx.brain

/**
 * 广度优先搜索的节点
 * * [N]是節點類型，
 * * [L]是从一个[N]变为另一个[N]的方法（有向图的边、也称为“弧”）。
 */
abstract class BfsNode<N : BfsNode<N, L>, L : (N) -> N>(
	val distance: Int,
	val fromLink: L?,
	val fromNode: N?,
)
