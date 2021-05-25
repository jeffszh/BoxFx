package cn.jeff.utils

data class LocationXY(val x: Int, val y: Int)

@FunctionalInterface
interface ArrayXY<T> {
	operator fun get(locationXY: LocationXY): T
}
