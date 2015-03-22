package event

import scala.collection.mutable.ArrayBuffer

class EventManager {
	private val listeners = ArrayBuffer[Listener]()

	def addListener(listener: Listener) = {
		listeners += listener
	}

	def removeListener(listener: Listener) = {
		listeners -= listener
	}

	def delegateEvents(events: Vector[Event]) = {
		listeners.foreach(_.events ++= events)
	}
}