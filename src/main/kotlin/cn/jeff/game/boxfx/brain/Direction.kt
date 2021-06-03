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
	DUMMY(0, 0) {
		override val inverseOperation: Direction
			get() {
				error("内部出错1")
			}

		override fun invoke(p1: LocationXY): LocationXY {
			error("内部出错2")
		}
	}
	;

	override fun invoke(p1: LocationXY) =
		LocationXY(p1.x + dx, p1.y + dy)

	abstract val inverseOperation: Direction

	companion object {
		val meaningfulValues = values().subtract(listOf(DUMMY))
	}
}
