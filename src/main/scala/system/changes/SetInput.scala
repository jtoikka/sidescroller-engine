package system

import entity.Entity
import entity.Component._
import entity.InputComponent

/**
  * Sets the InputReceiver of an entity.
  */
case class SetInput(input: String) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		if (entity.contains(InputComp)) {
			entity.updateComponent(InputComponent(input))	
		}
	}
}