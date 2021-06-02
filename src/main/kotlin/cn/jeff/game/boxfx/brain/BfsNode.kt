package cn.jeff.game.boxfx.brain

/**
 * 广度优先搜索的节点
 * * [N]是節點類型，
 * * [P]是Payload類型，
 * * [L]是从一个[P]变为另一个[P]的方法（有向图的边、也称为“弧”）。
 */
abstract class BfsNode<N : BfsNode<N, P, L>, P, L : (P) -> P>(
	val distance: Int,
	val fromLink: L,
	val backLink: () -> N
) {
	companion object {
		val dummyLink = {
			error("無效link！")
		}
	}
}
