package system

import entity._
import entity.Entity
import entity.Component._
import math.Vec3

/**
  * Applies an acceleration to an entity (modifies velocity)
  */
case class Acceleration(velocityChange: Vec3) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		entity(PhysicsComp) match {
			case Some(comp: PhysicsComponent) => {
				entity.updateComponent(
					comp.copy(velocity = comp.velocity + velocityChange))
			}
		}
	}
}