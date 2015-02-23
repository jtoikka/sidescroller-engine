package system

import entity.Entity

abstract class StateChange {
	def applyTo(entity: Entity): Unit
}