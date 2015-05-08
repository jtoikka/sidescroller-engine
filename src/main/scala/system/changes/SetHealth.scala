package system

import state.State
import entity._

/**
  * Sets the health of an entity.
  */
class SetHealth(health: Int) extends StateChange {
	def applyTo(entity: Entity) = {
		entity.updateComponent(HealthComponent(health))
	}
}