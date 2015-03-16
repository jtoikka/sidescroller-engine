package system

import entity.Entity
import entity.Component._

case class SetFrictionMultiplier(friction: Float) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		entity(PhysicsComp) match {
			case Some(physComp) => {
				entity.updateComponent(
					physComp.copy(frictionMultiplier = friction))
			}
		}
	}
}