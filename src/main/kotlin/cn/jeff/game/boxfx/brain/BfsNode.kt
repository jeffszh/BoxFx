package cn.jeff.game.boxfx.brain

/**
 * 广度优先搜索的节点
 * [N]是節點類型，[P]是Payload類型。
 */
abstract class BfsNode<N, P>(
		val distance: Int,
		val fromLink: (P) -> P,
		val backLink: () -> N
) {
	companion object {
		val dummyLink = {
			error("無效link！")
		}
	}
}
