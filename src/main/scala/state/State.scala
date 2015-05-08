package state

import entity.Entity
import system.StateChange
import system.Changes

/**
  * Trigger for moving into new state. A state is changed when the [condition]
  * is met.
  */
class Trigger(
	val condition: (Entity => Boolean), 
	s: => State) {
	lazy val state = s
}

/**
  * An entity state. When the state is moved into, applies [changes] to the 
  * entity. When in a state, that state's triggers are check, as well as the
  * [anyState] triggers, to determine when a new state should be moved into.
  */
case class State(
	triggers: Vector[Trigger], 
	changes: Vector[StateChange] = Vector(), 
	anyState: AnyState) {
	
}

/**
  * Triggers that apply to all states. This could for example be a death trigger.
  */
case class AnyState(triggers: Vector[Trigger])