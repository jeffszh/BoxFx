package cn.jeff.game.boxfx.brain

import cn.jeff.game.boxfx.Cell
import cn.jeff.utils.LocationXY

/**
 * # 箱子操作
 * 包括推和拉两种。
 */
abstract class BoxOperation(
	val manLocation: LocationXY,
	val direction: Direction,
) {

	abstract operator fun invoke(evc: EvalCells)

	class Push(
		manLocation: LocationXY,
		pushDirection: Direction,
	) : BoxOperation(manLocation, pushDirection) {
		private val pushDirection get() = direction
		override fun invoke(evc: EvalCells) {
			val boxLocation = manLocation + pushDirection
			val destLocation = boxLocation + pushDirection
			evc[manLocation] = Cell.SPACE
			evc[boxLocation] = Cell.MAN
			evc[destLocation] = Cell.BOX
		}
	}

	class Pull(
		manLocation: LocationXY,
		pullDirection: Direction,
	) : BoxOperation(manLocation, pullDirection) {
		private val pullDirection get() = direction
		override fun invoke(evc: EvalCells) {
			val boxLocation = manLocation - pullDirection
			val newManLocation = manLocation + pullDirection
			evc[boxLocation] = Cell.SPACE
			evc[manLocation] = Cell.BOX
			evc[newManLocation] = Cell.MAN
		}

		fun inverseOperation(): Push {
			val pushManLocation = manLocation + pullDirection
			val pushDirection = pullDirection.inverseOperation
			return Push(pushManLocation, pushDirection)
		}
	}
}
