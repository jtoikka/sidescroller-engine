package event

import scala.collection.mutable._

trait Listener {
	val privateEvents = ArrayBuffer[Event]()
	val events = ArrayBuffer[Event]()

	def handleEvents(): Unit
}