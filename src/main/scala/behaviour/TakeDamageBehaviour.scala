package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._

/**
  * If a collision occurs with a damaging object, reduce health.
  */
class TakeDamageBehaviour(val args: List[String]) extends Behaviour {
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val hit = entity.privateEvents.find(event => {
			event match {
				case t: TriggerEvent => if (t.collider.contains(DamageComp)) {
					true
				} else {
					false
				}
				case _ => false
			}
		})
		if (hit.isDefined) {
			val changes = entity(HealthComp) match {
				case Some(hp) => {
					if (hp.amount - 1 == 0) {
						entity.destroy
					}
					Changes(entity, Vector(new SetHealth(hp.amount - 1)))
				}
				case _ => Changes(entity)
			}
			changes ++ (entity(PhysicsComp) match {
				case Some(phys) => {
					if (entity.timers.contains("recoil")) {
						entity.timers("recoil").start()
					} else {
						entity.timers("recoil") = new Timer(maxTime = 0.4f)
						entity.timers("recoil").start()
					}
					val xVelo = if (entity.flags("facingLeft")) 70.0f else -70.0f
					Changes(entity, Vector(SetVelocity(Vec3(0, 0, 0), Vec3(xVelo, 100.0f, 0.0f))))
				}
				case _ => Changes(entity)
			})
		} else {
			Changes(entity)
		}
	}
}