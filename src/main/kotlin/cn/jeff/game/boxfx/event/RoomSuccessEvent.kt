package cn.jeff.game.boxfx.event

import tornadofx.*
import tornadofx.EventBus.RunOn.ApplicationThread

/**
 * # 过关事件
 * 玩家完成一关的时候，发送此事件。
 *
 * @property stepCount 完成时的步数
 */
class RoomSuccessEvent(val stepCount: Int) : FXEvent(ApplicationThread)
