package state

import entity.Entity
import system.StateChange
import system.Changes

class Trigger(
	val condition: (Entity => Boolean),
	s: => State) {
	lazy val state = s
}

case class State(
	triggers: Vector[Trigger], 
	changes: Vector[StateChange] = Vector(), 
	anyState: AnyState) {
	
}

case class AnyState(triggers: Vector[Trigger])