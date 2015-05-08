package map

import scala.collection.mutable.ArrayBuffer

// Can be used to generate random maps. Not currently in use.
class LevelMap(iterations: Int) {
	val center = Room(0, 0, 1, 1)

	val rooms = ArrayBuffer[Room](center)

	// center.addRoomsRNG(rooms, iterations)
}