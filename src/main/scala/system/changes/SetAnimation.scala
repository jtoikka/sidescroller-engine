package system

import entity.Entity
import entity.Component._
import entity._

/**
  * Sets an animation cycle for an entity. Sets [nameLeft] if facingLeft is true,
  * [nameRight] if not.
  */
case class SetAnimation(nameRight: String, nameLeft: String) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		if (entity.contains(AnimationComp)) {
			entity(AnimationComp) match {
				case Some(AnimationComponent(animationSheet, animation, timer)) => {
					if (entity.flags("facingLeft")) {
						entity.updateComponent(AnimationComponent(animationSheet, nameLeft, 0.0))
					} else {
						entity.updateComponent(AnimationComponent(animationSheet, nameRight, 0.0))
					}
				}
				case _ =>
			}
		}
	}
}