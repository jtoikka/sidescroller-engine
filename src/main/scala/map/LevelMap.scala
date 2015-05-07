package map

import scala.collection.mutable.ArrayBuffer

class LevelMap(iterations: Int) {
	val center = Room(0, 0, 1, 1)

	val rooms = ArrayBuffer[Room](center)

	// center.addRoomsRNG(rooms, iterations)
}