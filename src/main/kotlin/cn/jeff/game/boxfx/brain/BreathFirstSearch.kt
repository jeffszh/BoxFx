package cn.jeff.game.boxfx.brain

/**
 * # 广度优先搜索
 * 抽象类，实现普遍意义的广度优先搜索。
 * [T]必须重载判断相等的操作符。
 */
abstract class BreathFirstSearch<T : BfsNode<T>> {

	var name = "BFS"

	abstract fun T.generateNext(): List<T>

	abstract fun T.checkDone(): Boolean

	private val searchedNodes = hashSetOf<T>()
	private val searchingNodes = hashSetOf<T>()

	fun search(root: T): List<T> {
		searchedNodes.add(root)
		return search(0)
	}

	/**
	 * # 搜索一层
	 * 为了方便实现双向搜索，使用逐层搜索的方式，因此跟通常的广度优先搜索算法略有不同。
	 */
	private tailrec fun search(level: Int): List<T> {
		println("$name 开始搜索第 $level 层。")
		// 先查找是否有符合结束条件的节点
		searchingNodes.forEach {
			if (it.checkDone()) {
				// 如果成功，返回搜索路径。
				val result = mutableListOf<T>()
				var node = it
				while (node.distance > 0) {
					result.add(node)
					node = node.backLink.link()
				}
				return result.reversed()
			}
		}
		// 然后展开下层节点
		val expandingNodes = hashSetOf<T>()
		searchingNodes.forEach {
			expandNode(it, expandingNodes)
		}
		if (expandingNodes.isEmpty()){
			// 搜索失败，返回空列表。
			return emptyList()
		}
		// 搜索下层
		searchingNodes.addAll(searchedNodes)
		searchingNodes.clear()
		searchingNodes.addAll(expandingNodes)
		expandingNodes.clear()
		return search(level + 1)
	}

	private fun expandNode(node: T, expandingNodes: HashSet<T>) {
		expandingNodes.addAll(node.generateNext().filter {
			!searchedNodes.contains(it) &&
					!searchingNodes.contains(it) &&
					!expandingNodes.contains(it)
		})
	}

}
