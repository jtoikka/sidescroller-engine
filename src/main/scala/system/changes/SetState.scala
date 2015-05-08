package system

import state.State
import entity._

/**
  * Sets the state of an entity.
  */
class SetState(newState: State) extends StateChange {
	def applyTo(entity: Entity) = {
		entity.updateComponent(StateComponent(newState))
	}
}