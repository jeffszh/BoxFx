import java.lang.StringBuilder

fun main() {
	println("Abc".hashCode())
	println("abc".hashCode())
	val sb = StringBuilder()
	sb.append('A')
	println(sb.toString().hashCode())
	sb.append('b')
	println(sb.toString().hashCode())
	sb.append('c')
	println(sb.toString().hashCode())
}
