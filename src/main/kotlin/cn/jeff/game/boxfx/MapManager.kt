package cn.jeff.game.boxfx

import java.io.InputStreamReader

object MapManager {

	val maps: List<GameMap>

	init {
		val lines = InputStreamReader(
				javaClass.getResourceAsStream("/map/build_in_maps.txt")).use {
			it.readLines()
		}
		maps = loadMaps(lines)
	}

	private tailrec fun loadMaps(
			lines: List<String>,
			tempResult: MutableList<GameMap> = mutableListOf()): List<GameMap> {
		val lastHeaderIndex = lines.indexOfLast {
			// 标题是以“M”开头
			it.startsWith('M')
		}

		// 以最后一个标题为界分成两部分
		val part1 = lines.subList(0, lastHeaderIndex)
		val part2 = lines.subList(lastHeaderIndex, lines.count())

		// 去掉前面的“M”作为关卡号码
		val mapNo = lines[lastHeaderIndex].substring(1).toInt()
		// 添加至中间结果
		tempResult.add(GameMap(
				mapNo = mapNo,
				width = lines[lastHeaderIndex + 1].length,
				height = lines.count() - lastHeaderIndex,
				lines = part2.subList(1, part2.count())
		))

		// 尾递归
		return if (lastHeaderIndex <= 0) {
			tempResult.reversed().also {
				println("加载了${it.count()}个地图。")
			}
		} else {
			loadMaps(part1, tempResult)
		}
	}

	class GameMap(
			val mapNo: Int,
			val width: Int, val height: Int,
			val lines: List<String>
	)

}
