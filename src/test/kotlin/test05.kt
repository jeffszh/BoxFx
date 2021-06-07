fun main() {
	val s0 = "one_two_three_four"
	val s1 = (0 until 6).map {
		s0.substring(it * 3, it * 3 + 3)
	}
	val s2 = s1.joinToString("\n")
	println(s2)
}
