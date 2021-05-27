package cn.jeff.game.boxfx

import cn.jeff.game.boxfx.event.TimerEvent
import tornadofx.*
import java.util.*
import kotlin.concurrent.timer

class BoxFx : App(MainWnd::class) {

	private val globalTimer: Timer

	init {
		println("开始运行。")
		globalTimer = timer(period = 500) {
			// println("fire!")
			fire(TimerEvent())
		}
	}

	override fun stop() {
		super.stop()
		globalTimer.cancel()
	}

}
