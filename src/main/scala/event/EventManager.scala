package event

import scala.collection.mutable.ArrayBuffer

/**
  * Receives and delegates events.
  */
class EventManager {
	private val listeners = ArrayBuffer[Listener]()

	def addListener(listener: Listener) = {
		listeners += listener
		println(listeners)
	}

	def removeListener(listener: Listener) = {
		listeners -= listener
	}

/**
  * Sends the given events to all listeners.
  */
	def delegateEvents(events: Vector[Event]) = {
		listeners.foreach(_.events ++= events)
	}
}