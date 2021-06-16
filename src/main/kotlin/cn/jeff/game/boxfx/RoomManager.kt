package cn.jeff.game.boxfx

import java.io.*
import java.util.*

/**
 * 游戏中的一关，称为[Room]。
 */
object RoomManager {

	val rooms: SortedMap<Int, Room>

	init {
		val lines = InputStreamReader(
			javaClass.getResourceAsStream("/room/build_in_rooms.txt")!!
		).use {
			it.readLines()
		}
		val roomList = loadRooms(lines)

		val extraReader = try {
			FileReader("extra_rooms.txt")
		} catch (e: IOException) {
			e.printStackTrace()
			javaClass.getResourceAsStream("/room/extra_rooms.txt")!!.use { ins ->
				FileOutputStream("extra_rooms.txt").use { fos ->
					ins.copyTo(fos)
				}
			}
			FileReader("extra_rooms.txt")
		}
		val extraLines = extraReader.use { it.readLines() }
		val extraRoomList = loadRooms(extraLines)

		rooms = (roomList + extraRoomList).associateBy { it.roomNo }.toSortedMap()
	}

	private tailrec fun loadRooms(
		lines: List<String>,
		tempResult: MutableList<Room> = mutableListOf()
	): List<Room> {
		val lastHeaderIndex = lines.indexOfLast {
			// 标题是以“M”开头
			it.startsWith('M')
		}

		// 以最后一个标题为界分成两部分
		val part1 = lines.subList(0, lastHeaderIndex)
		val part2 = lines.subList(lastHeaderIndex, lines.count())

		// 去掉前面的“M”作为关卡号码
		val roomNo = lines[lastHeaderIndex].substring(1).toInt()
		// 添加至中间结果
		tempResult.add(
			Room(
				roomNo = roomNo,
				width = lines[lastHeaderIndex + 1].length,
				height = lines.count() - lastHeaderIndex - 1,
				lines = part2.subList(1, part2.count())
			)
		)

		// 尾递归
		return if (lastHeaderIndex <= 0) {
			tempResult.reversed().also {
				println("加载了${it.count()}个地图。")
			}
		} else {
			loadRooms(part1, tempResult)
		}
	}

	class Room(
		val roomNo: Int,
		val width: Int, val height: Int,
		val lines: List<String>
	)

}
