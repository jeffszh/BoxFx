package cn.jeff.utils

import cn.jeff.game.boxfx.brain.Direction

data class LocationXY(val x: Int, val y: Int) {
	override fun hashCode(): Int =
		(y shl 16) + x

	override fun equals(other: Any?): Boolean {
		// print("判斷相等：")
		return (other is LocationXY &&
				x == other.x && y == other.y)//.also {
		//println(it)
		//}
	}

	fun delta(dx: Int, dy: Int) =
		LocationXY(x + dx, y + dy)

	operator fun plus(dir: Direction) =
		dir(this)

	operator fun minus(dir: Direction) =
		dir.inverseOperation(this)
}

interface ArrayXY<T> {
	operator fun get(locationXY: LocationXY): T
	operator fun set(locationXY: LocationXY, value: T)
}
