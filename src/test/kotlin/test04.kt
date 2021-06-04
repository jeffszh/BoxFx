class Coordinate(
	val x: Int,
	val y: Int,
	val z: Int
) {
	override fun hashCode(): Int {
		print('H')
		return x + y * 100 + z * 10000
	}

	override fun equals(other: Any?): Boolean {
		print('E')
		return other is Coordinate && x == other.x && y == other.y && z == other.z
	}
}

fun main() {
	val crdSet = setOf(
		Coordinate(1, 2, 3),
		Coordinate(2, 3, 4),
		Coordinate(5, 6, 7)
	)
	println("-----------------------")
	val crd1 = Coordinate(2, 3, 4)
	println("${crd1.x}, ${crd1.y}, ${crd1.z}")
	println(crd1 in crdSet)
	println(Coordinate(8, 9, 10) in crdSet)
}
