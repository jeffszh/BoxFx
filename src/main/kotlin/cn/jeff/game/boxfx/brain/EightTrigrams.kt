package cn.jeff.game.boxfx.brain

import cn.jeff.utils.LocationXY

/**
 * # 八卦方位
 * 用于判断周围8个方位格子的情况。
 */
enum class EightTrigrams(private vararg val matrix: Int) {

	/** 乾，0点方向 */
	HEAVEN(1, 0, 0, 1) {
		override fun toString() = "乾"
		override val mirror: EightTrigrams get() = HEAVEN
	},

	/** 巽，1点半方向 */
	WIND(1, -1, 1, 1) {
		override fun toString() = "巽"
		override val mirror: EightTrigrams get() = SWAMP
	},

	/** 坎，3点方向 */
	WATER(0, -1, 1, 0) {
		override fun toString() = "坎"
		override val mirror: EightTrigrams get() = FIRE
	},

	/** 艮，4点半方向 */
	MOUNTAIN(-1, -1, 1, -1) {
		override fun toString() = "艮"
		override val mirror: EightTrigrams get() = THUNDER
	},

	/** 坤，6点钟方向 */
	EARTH(-1, 0, 0, -1) {
		override fun toString() = "坤"
		override val mirror: EightTrigrams get() = EARTH
	},

	/** 震，7点半方向 */
	THUNDER(-1, 1, -1, -1) {
		override fun toString() = "震"
		override val mirror: EightTrigrams get() = MOUNTAIN
	},

	/** 离，9点钟方向 */
	FIRE(0, 1, -1, 0) {
		override fun toString() = "离"
		override val mirror: EightTrigrams get() = WATER
	},

	/** 兑，10点半方向 */
	SWAMP(1, 1, -1, 1) {
		override fun toString() = "兑"
		override val mirror: EightTrigrams get() = WIND
	};

	protected abstract val mirror: EightTrigrams

	fun transform(locationXY: LocationXY, direction: Direction): LocationXY {
		val dx = direction.dx
		val dy = direction.dy
		val newDx = dx * matrix[0] + dy * matrix[1]
		val newDy = dx * matrix[2] + dy * matrix[3]
		return LocationXY(locationXY.x + newDx, locationXY.y + newDy)
	}

	class EightTrigramsContext private constructor(
		private val center: LocationXY,
		private val startDirection: Direction,
		private val mirrored: Boolean,
		result: Boolean
	) {
		var result = result
			private set

		companion object {
			fun create(
				center: LocationXY,
				startDirection: Direction,
				mirrored: Boolean
			) = EightTrigramsContext(center, startDirection, mirrored, true)
		}

		/** 乾，0点方向 [HEAVEN] */
		infix fun heaven(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(HEAVEN, op)

		/** 巽，1点半方向 [WIND] */
		infix fun wind(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(WIND, op)

		/** 坎，3点方向 [WATER] */
		infix fun water(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(WATER, op)

		/** 艮，4点半方向 [MOUNTAIN] */
		infix fun mountain(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(MOUNTAIN, op)

		/** 坤，6点钟方向 [EARTH] */
		infix fun earth(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(EARTH, op)

		/** 震，7点半方向 [THUNDER] */
		infix fun thunder(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(THUNDER, op)

		/** 离，9点钟方向 [FIRE] */
		infix fun fire(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(FIRE, op)

		/** 兑，10点半方向 [SWAMP] */
		infix fun swamp(op: EightTrigramsContext.(location: LocationXY) -> Boolean) =
			process(SWAMP, op)

		private fun process(
			eightTrigrams: EightTrigrams,
			op: EightTrigramsContext.(location: LocationXY) -> Boolean
		) {
			result = result && this.op(
				if (mirrored) {
					eightTrigrams.mirror
				} else {
					eightTrigrams
				}.transform(center, startDirection)
			)
		}
	}

	companion object {
		/**
		 * 检查八卦方位上的各个位置是否都符合条件。
		 *
		 * @param center 圆心
		 * @param direction 起始方向
		 * @param op 检测操作，可运用[EightTrigramsContext.heaven]、[EightTrigramsContext.wind]
		 * 等infix函数来进行检查。
		 * @return 若所有条件满足，返回true。
		 */
		fun check(
			center: LocationXY, direction: Direction,
			op: EightTrigramsContext.() -> Unit
		) = EightTrigramsContext.create(center, direction, false).apply(op).result

		/**
		 * 镜像地检查八卦方位上的各个位置是否都符合条件，
		 * 类似[EightTrigrams.check]，只是以左右镜像了的方向。
		 *
		 * @param center 圆心
		 * @param direction 起始方向
		 * @param op 检测操作，可运用[EightTrigramsContext.heaven]、[EightTrigramsContext.wind]
		 * 等infix函数来进行检查。
		 * @return 若所有条件满足，返回true。
		 */
		fun checkMirrored(
			center: LocationXY, direction: Direction,
			op: EightTrigramsContext.() -> Unit
		) = EightTrigramsContext.create(center, direction, true).apply(op).result

		/**
		 * 以正像、镜像两个方向同时检查八卦方位上的各个位置是否都符合条件，
		 * 即同时使用[EightTrigrams.check]和[EightTrigrams.checkMirrored]来进行检查。
		 *
		 * @return 若两个方向的所有条件都满足，返回true。
		 */
		fun checkBoth(
			center: LocationXY, direction: Direction,
			op: EightTrigramsContext.() -> Unit
		) = check(center, direction, op) && checkMirrored(center, direction, op)

		/**
		 * 以正像或镜像两个方向，检查八卦方位上的各个位置是否都符合条件，
		 * 即先后使用[EightTrigrams.check]和[EightTrigrams.checkMirrored]来进行检查，
		 * 看看是否有其中一个满足条件。
		 *
		 * @return 若两个方向的其中一个所有条件都满足，返回true。
		 */
		fun checkEitherOr(
			center: LocationXY, direction: Direction,
			op: EightTrigramsContext.() -> Unit
		) = check(center, direction, op) || checkMirrored(center, direction, op)
	}
}
