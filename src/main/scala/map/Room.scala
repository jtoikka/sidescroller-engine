package map

import scala.collection.mutable.ArrayBuffer

case class Room(x: Int, y: Int, w: Int, h: Int) {

	val linkedRooms = ArrayBuffer[Room]()

	val options = {
		// top
		val ops = ArrayBuffer.tabulate(w)(i => (x + i, y - 1))
		// bottom
		ops ++= ArrayBuffer.tabulate(w)(i => (x + i, y + h))
		// left
		ops ++= ArrayBuffer.tabulate(h)(j => (x - 1, y + j))
		// right
		ops ++= ArrayBuffer.tabulate(h)(j => (x + w, y + j))

		ops
	}

	def intersect(other: Room) = {
		val origX = x + w / 2.0
		val origY = y + h / 2.0

		val otherX = other.x + other.w/2.0
		val otherY = other.y + other.h/2.0

		if ((origX - otherX).abs < (w/2.0 + other.w/2.0) &&
			(origY - otherY).abs < (h/2.0 + other.h/2.0)) {
			true
		} else {
			false
		}
	}

	def touching(other: Room) = {
		val origX = x + w / 2.0
		val origY = y + h / 2.0

		val otherX = other.x + other.w/2.0
		val otherY = other.y + other.h/2.0

		if (
			(origX - otherX).abs == (w/2.0 + other.w/2.0) &&
			(origY - otherY).abs <= (h/2.0 + other.h/2.0) ||
			(origX - otherX).abs <= (w/2.0 + other.w/2.0) &&
			(origY - otherY).abs == (h/2.0 + other.h/2.0)) {
			false
		} else {
			true
		}
	}

	def addRoom(rooms: ArrayBuffer[Room], room: Room): Boolean = {
		var unconnectedRoom = true
		for (r <- rooms) {
			if (room.intersect(r)) {
				unconnectedRoom = false
			}
		}

		if (unconnectedRoom) {
			println("unconnected")
			linkedRooms += room
			room.linkedRooms += this
			rooms += room
			true
		} else {
			if (!linkedRooms.contains(room)) {
				if (touching(room)) {
					linkedRooms += room
					room.linkedRooms += this
					true
				} else {
					false
				}
			} else {
				false
			}
		}
	}

	val rng = new util.Random()

	def mixedBag(limit: Int, n: Int): ArrayBuffer[Int] = {
		val a = ArrayBuffer[Int]()
		while (a.length < n) {
			var r = rng.nextInt(limit)
			while (a.contains(r)) {
				r = rng.nextInt(limit)
			}
			a += r
		}
		a
	}

	def addRoomsRNG(rooms: ArrayBuffer[Room], steps: Int): Unit = {
		println("steps: " + steps)
		if (steps > 0) {
			var numRooms = rng.nextInt(options.length) + 1
			if (linkedRooms.length == 0 && numRooms == 0) {
				numRooms = 1
			} 
			val m = mixedBag(options.length, numRooms)
			for (r <- m) {
				val c = options(r)
				val roomH = rng.nextInt(2) + 1
				val roomW = rng.nextInt(2) + 1
				val roomY: Int = if (roomH > 1 && c._2 < this.y) this.y - roomH else c._2
				val roomX = if (roomW > 1 && c._1 < this.x) this.x - roomW else c._1
				val room = Room(c._1, roomY, roomW, roomH)
				if (addRoom(rooms, room)) {
					room.addRoomsRNG(rooms, steps - 1)
				}
			}
		}
	}
}