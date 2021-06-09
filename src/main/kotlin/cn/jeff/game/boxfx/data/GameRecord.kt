package cn.jeff.game.boxfx.data

import com.google.gson.GsonBuilder
import java.io.FileReader
import java.io.FileWriter
import java.util.*

data class GameRecord(
	var lastPlayedRoom: Int? = null,
	var roomRecords: SortedMap<Int, RoomRecord> = sortedMapOf()
) {
	fun save() = saveGameRecord(this)
}

data class RoomRecord(
	var bestStepCount: Int? = null,
	var stepCountByAi: Int? = null,
	var aiSpendsTime: Long? = null,
)

private const val RECORD_FILE = "GameRecord.json"
private val gson = GsonBuilder()
	.setPrettyPrinting()
	.create()

val gameRecord = loadGameRecord().apply { save() }

private fun loadGameRecord(): GameRecord = try {
	FileReader(RECORD_FILE).use { reader ->
		gson.fromJson(reader, GameRecord::class.java)
	}
} catch (e: Exception) {
	e.printStackTrace()
	GameRecord()
}

private fun saveGameRecord(gameRecord: GameRecord) {
	FileWriter(RECORD_FILE).use { writer ->
		gson.toJson(gameRecord, writer)
	}
}
