package cn.jeff.game.boxfx.brain

import cn.jeff.utils.LocationXY

enum class Direction(val dx: Int, val dy: Int) : (LocationXY) -> LocationXY {
	LEFT(-1, 0) {
		override val inverseOperation get() = RIGHT
	},
	RIGHT(1, 0) {
		override val inverseOperation get() = LEFT
	},
	UP(0, -1) {
		override val inverseOperation get() = DOWN
	},
	DOWN(0, 1) {
		override val inverseOperation get() = UP
	},
	;

	override fun invoke(p1: LocationXY) =
		LocationXY(p1.x + dx, p1.y + dy)

	abstract val inverseOperation: Direction

}
