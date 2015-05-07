package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import event.EntitySpawnEvent

class DestroyOnCollision(val args: List[String]) extends Behaviour {
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val hit = entity.privateEvents.find(event => {
			event match {
				case t: TriggerEvent if (t.collider.contains(PhysicsComp)) => {
					true
				}
				case _ => false
			}
		})
		if (hit.isDefined) entity.destroy()
		Changes(entity)
	}
}