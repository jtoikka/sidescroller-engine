package system

import entity.Component._
import entity._
import scene.Scene
import state._

/**
  * Runs behaviours attached to entities.
  */
class BehaviourSystem extends System(bitMask(BehaviourComp)) {

/**
  * Initialize behaviours for an entity.
  */
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

/**
  * Apply update method of entity's behaviours to entity. 
  */
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