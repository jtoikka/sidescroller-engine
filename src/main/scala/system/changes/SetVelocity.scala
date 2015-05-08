package system

import state.State
import entity._
import math.Vec3
import entity.Component._

/**
  * Sets the velocity of an entity. The mask determines which x, y, z values
  * to keep from the previous velocity.
  */
case class SetVelocity(mask: Vec3, amount: Vec3) extends StateChange {
	def applyTo(entity: Entity) = {
		entity(PhysicsComp) match {
			case Some(comp: PhysicsComponent) => {
				entity.updateComponent(
					comp.copy(velocity = comp.velocity * mask + amount))
			}
		}
	}
}