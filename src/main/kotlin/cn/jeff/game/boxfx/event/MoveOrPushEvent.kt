package cn.jeff.game.boxfx.event

import tornadofx.*

class MoveOrPushEvent(
	val dx: Int,
	val dy: Int,
) : FXEvent(runOn = EventBus.RunOn.ApplicationThread)
