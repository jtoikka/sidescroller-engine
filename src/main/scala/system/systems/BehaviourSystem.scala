package system

import entity.Component._
import entity._
import scene.Scene
import state._

class BehaviourSystem extends System(bitMask(BehaviourComp)) {
	def instantiate(scene: Scene) = {
		scene.entities.foreach(e => {
			e(BehaviourComp) match {
				case Some(BehaviourComponent(behaviours)) => {
					behaviours.foreach(behaviour => {
						behaviour.initialize(e, scene)
					})
				}
				case _ =>
			}
		})
	}

	def applyTo(entity: Entity, scene: Scene, delta: Float): Changes = {
		entity(BehaviourComp) match {
			case Some(BehaviourComponent(behaviours)) => {
				var changes = Changes(entity, Vector(), Vector())
 				behaviours.foreach(behaviour => {
 					changes ++= behaviour.fixedUpdate(entity, scene, delta)
 				})
 				changes
			}
			case _ => {
				Changes(entity, Vector(), Vector())
			}
		}
	}
}