package cn.jeff.game.boxfx.brain

/**
 * # 广度优先搜索
 * 抽象类，实现普遍意义的广度优先搜索。
 * [P]必须重载判断相等的操作符。
 */
abstract class BreathFirstSearch<N : BfsNode<N, P, L>, P, L : (P) -> P> {

	var name = "BFS"
	var onNewLevel: suspend (level: Int, nodeCount: Int) -> Unit = { _, _ -> }

	abstract fun N.generateNext(): List<N>

	abstract fun N.checkDone(): Boolean

	val searchedNodes = hashSetOf<N>()
	val searchingNodes = hashSetOf<N>()

	suspend fun search(root: N): List<L> {
		searchingNodes.add(root)
		return search(0)
	}

	/**
	 * # 搜索一层
	 * 为了方便实现双向搜索，使用逐层搜索的方式，因此跟通常的广度优先搜索算法略有不同。
	 */
	private tailrec suspend fun search(level: Int): List<L> {
		println("$name 准备搜索第 $level 层。")
		onNewLevel(level, searchedNodes.count() + searchingNodes.count())
		println("$name 开始搜索第 $level 层。")
		// 先查找是否有符合结束条件的节点
		searchingNodes.forEach {
			if (it.checkDone()) {
				// 如果成功，返回搜索路径。
				val result = mutableListOf<L>()
				var node = it
				while (node.distance > 0) {
					result.add(node.fromLink)
					node = node.backLink()
				}
				return result.reversed()
			}
		}
		// 然后展开下层节点
		val expandingNodes = hashSetOf<N>()
		searchingNodes.forEach {
			expandNode(it, expandingNodes)
		}
		if (expandingNodes.isEmpty()) {
			// 搜索失败，返回空列表。
			return emptyList()
		}
		// 搜索下层
		searchedNodes.addAll(searchingNodes)
		searchingNodes.clear()
		searchingNodes.addAll(expandingNodes)
		expandingNodes.clear()
		return search(level + 1)
	}

	private fun expandNode(node: N, expandingNodes: HashSet<N>) {
		expandingNodes.addAll(node.generateNext().filter {
			!searchedNodes.contains(it) &&
					!searchingNodes.contains(it) &&
					!expandingNodes.contains(it)
		})
	}

}
