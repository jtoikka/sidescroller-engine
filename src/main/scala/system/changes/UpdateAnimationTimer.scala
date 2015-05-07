package system

import entity.Entity
import entity.Component._
import entity._

case class UpdateAnimationTimer(delta: Float) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		if (entity.contains(AnimationComp)) {
			entity(AnimationComp) match {
				case Some(AnimationComponent(animationSheet, animation, timer)) => {
					entity.updateComponent(AnimationComponent(animationSheet, animation, timer + delta))
				}
				case _ =>
			}
		}
	}
}