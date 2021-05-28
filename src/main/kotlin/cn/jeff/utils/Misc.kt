package cn.jeff.utils

data class LocationXY(val x: Int, val y: Int)

interface ArrayXY<T> {
	operator fun get(locationXY: LocationXY): T
	operator fun set(locationXY: LocationXY, value: T)
}
