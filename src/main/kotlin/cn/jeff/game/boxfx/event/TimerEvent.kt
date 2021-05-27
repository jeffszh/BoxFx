package cn.jeff.game.boxfx.event

import tornadofx.*
import tornadofx.EventBus.RunOn.ApplicationThread

class TimerEvent : FXEvent(runOn = ApplicationThread)
