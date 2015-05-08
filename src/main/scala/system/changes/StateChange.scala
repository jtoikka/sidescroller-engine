package system

import entity.Entity

/**
  * An interface for entity changes. Changes are applied to an entity at the
  * end of a frame update.
  */
abstract class StateChange {
	def applyTo(entity: Entity): Unit
}