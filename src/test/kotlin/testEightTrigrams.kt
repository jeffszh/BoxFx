import cn.jeff.game.boxfx.brain.Direction
import cn.jeff.game.boxfx.brain.EightTrigrams
import cn.jeff.utils.LocationXY

fun main() {
	println("开始。")
	val location = LocationXY(1, 1)
	for (direction in Direction.values()) {
		println(direction)
		val diagram = EightTrigrams.values().associateBy {
			it.transform(location, direction)
		}
		for (y in 0 until 3) {
			for (x in 0 until 3) {
				print(diagram[LocationXY(x, y)] ?: "无")
			}
			println()
		}
		println("--------------")
	}

	val r1 = EightTrigrams.check(location, Direction.UP) {
		heaven { (x, y) ->
			x == 1 && y == 0
		}
		earth { (x, y) ->
			x == 1 && y == 2
		}
		mountain { (x, y) ->
			x == 2 && y == 2
		}
		water { (x, y) ->
			x == 2 && y == 1
		}
		wind { (x, y) ->
			x == 2 && y == 0
		}
		thunder { (x, y) ->
			x == 0 && y == 2
		}
		fire { (x, y) ->
			x == 0 && y == 1
		}
		swamp { (x, y) ->
			x == 0 && y == 0
		}
	}
	println(r1)

	val r2 = EightTrigrams.checkMirrored(location, Direction.LEFT) {
		heaven { (x, y) ->
			x == 0 && y == 1
		}
		earth { (x, y) ->
			x == 2 && y == 1
		}
		mountain { (x, y) ->
			x == 2 && y == 2
		}
		water { (x, y) ->
			x == 1 && y == 2
		}
		wind { (x, y) ->
			x == 0 && y == 2
		}
	}
	println(r2)

	val r3 = EightTrigrams.checkBoth(location, Direction.RIGHT) {
		heaven { (x, y) ->
			x == 2 && y == 1
		}
		earth { (x, y) ->
			x == 0 && y == 1
		}
		mountain { (x, y) ->
			x == 0 && y == 0
		}
	}
	println(r3)

	val r4 = EightTrigrams.checkEitherOr(location, Direction.RIGHT) {
		heaven { (x, y) ->
			x == 2 && y == 1
		}
		earth { (x, y) ->
			x == 0 && y == 1
		}
		mountain { (x, y) ->
			x == 0 && y == 0
		}
	}
	println(r4)

	val r5 = EightTrigrams.checkMirrored(location, Direction.DOWN) {
		heaven { (x, y) ->
			x == 1 && y == 2
		}
		earth { (x, y) ->
			x == 1 && y == 0
		}
		mountain { (x, y) ->
			x == 2 && y == 0
		}
	}
	println(r5)

	val r6 = EightTrigrams.checkMirrored(location, Direction.RIGHT) {
		heaven { (x, y) ->
			x == 2 && y == 1
		}
		earth { (x, y) ->
			x == 0 && y == 1
		}
		mountain { (x, y) ->
			x == 0 && (y == 0 || y == 2)
		}
	}
	println(r6)
}
