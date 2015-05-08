package event

import scala.collection.mutable._

/**
  * Listener trait. A listener is capable of receiving events.
  */
trait Listener {
	// Private events are received by the listener object itself.
	val privateEvents = ArrayBuffer[Event]()
	val events = ArrayBuffer[Event]()

/**
  * A function to determine how events should be handeled. At the very least
  * the list of events should be cleared, to avoid the accumulation of events.
  */
	def handleEvents(): Unit
}