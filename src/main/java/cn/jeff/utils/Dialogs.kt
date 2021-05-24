package cn.jeff.utils

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextField
import tornadofx.*

/**
 * # 带限制输入字符功能的文本框
 *
 * 可设定能输入的字符，例如可以用来做纯数字输入框。
 * @param validationRegex 正则表达式，用于过滤可输入的字符
 * @param initialText 初始值
 */
open class RestrictedTextField(private val validationRegex: Regex, initialText: String = "") : TextField(initialText) {

	override fun replaceText(start: Int, end: Int, text: String?) {
		if (validate(text))
			super.replaceText(start, end, text)
	}

	private fun validate(text: String?): Boolean =
			text?.let { validationRegex.matches(it) } ?: false
}

//class NumberTextField(initialText: String = "") : RestrictedTextField(Regex("[0-9.\\-]*"), initialText)
class NumberTextField(initialText: String = "") : RestrictedTextField(Regex("[0-9]*"), initialText)

fun UIComponent.inputNumber(
		initVal: Int, title: String = "输入数字", prompt: String = "",
		op: (result: Int) -> Unit) {
	val input = SimpleStringProperty(initVal.toString())
	dialog(title) {
		style = "-fx-font-family: 'Courier New'; -fx-font-size: 20;"
		vbox {
			alignment = Pos.CENTER
			form {
				field(prompt) {
					// textfield(input)
					NumberTextField().apply {
						bind(input)
						attachTo(this@form)
					}
				}
			}
			hbox {
				alignment = Pos.CENTER
				spacing = 20.0
				button("确定") {
					isDefaultButton = true
					action {
						val stringValue = input.value
						if (stringValue.isInt()) {
							close()
							op(stringValue.toInt())
						}
					}
				}
				button("取消") {
					action {
						close()
					}
				}
			}
		}
	}
}
