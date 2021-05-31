import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() {
	println("开始")
	runBlocking {
		val fun1Result = async {
			fun1()
		}
		val fun2Result = async {
			fun2()
		}
		awaitAll(fun1Result, fun2Result).forEach {
			println(it)
		}
	}
}

var power1 = 0
var power2 = 0

suspend fun fun1(): Int {
	repeat(10) {
		power1++
		println("我爱你 $it 次！power1=$power1")
		if (power1 > power2) {
			yield()
		}
	}
	return 22
}

suspend fun fun2(): Int {
	repeat(10) {
		power2 += 2
		println("我恨你 $it 次！power2=$power2")
		if (power2 > power1) {
			yield()
		}
	}
	return 33
}
