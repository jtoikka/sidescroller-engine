package system

import state.State
import entity._

class SetState(newState: State) extends StateChange {
	def applyTo(entity: Entity) = {
		entity.updateComponent(StateComponent(newState))
	}
}