package cn.jeff.game.boxfx.brain

import cn.jeff.utils.LocationXY

/**
 * # 箱子操作
 * 包括推和拉两种。
 */
abstract class BoxOperation(
	val manLocation: LocationXY,
	protected val direction: Direction,
) {
	class Push(
		manLocation: LocationXY,
		pushDirection: Direction,
	) : BoxOperation(manLocation, pushDirection) {
		val pushDirection get() = direction
	}

	class Pull(
		manLocation: LocationXY,
		pullDirection: Direction,
	) : BoxOperation(manLocation, pullDirection) {
		val pullDirection get() = direction
		fun inverseOperation(): Push {
			val pushManLocation = manLocation + pullDirection
			val pushDirection = pullDirection.inverseOperation
			return Push(pushManLocation, pushDirection)
		}
	}
}
