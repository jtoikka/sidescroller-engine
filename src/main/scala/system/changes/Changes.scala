package system

import event.Event
import entity.Entity

/**
  * A collection of state changes and events for an entity.
  */
case class Changes(
	entity: Entity, 
	stateChanges: Vector[StateChange] = Vector(), 
	events: Vector[Event] = Vector()) {

	def ++ (other: Changes) = {
		Changes(entity, stateChanges ++ other.stateChanges, events ++ other.events)
	}
}