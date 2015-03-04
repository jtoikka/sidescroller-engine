package state

import entity.Entity
import system.StateChange

case class Trigger(
	condition: (Entity => Boolean),
	state: State) {

}

case class State(triggers: Vector[Trigger], changes: Vector[StateChange]) {
	
}