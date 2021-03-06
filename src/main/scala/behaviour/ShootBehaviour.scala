package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import event.EntitySpawnEvent

/**
  * Allows for shooting of bullets, limited to a maximum rate.
  */
class ShootBehaviour(val args: List[String]) extends Behaviour {
	val MaximumRate = 0.18f
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			entity.timers("shoot") = new Timer(maxTime = MaximumRate)
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val events = if (entity.timers.contains("shoot")) {
			val timer = entity.timers("shoot")
			if (timer.isRunning) {
				if (timer.time == 0) {
					val facingLeft = entity.flags("facingLeft")
					// Vector(
					// 	EntitySpawnEvent(
					// 		false, 
					// 		(if (facingLeft)
					// 			"playerBulletLeft" 
					// 		else 
					// 			"playerBulletRight"), 
					// 		(if (facingLeft) 
					// 			entity.position - Vec3(10.0f, 0, 0)
					// 		else 
					// 			entity.position + Vec3(10.0f, 0, 0)),
					// 		scene)
					// )
					val dir = (
						if (facingLeft) 
							Vec3(-1.0f, 0, 0)
						else 
							Vec3(1.0f, 0, 0))
					Vector(
						EntitySpawnEvent(
							false, "bullet", 
							entity.position + dir * 10.0f, 
							scene,
							Vector(
								Acceleration(dir * 150.0f)
							)
						)
					)
				} else Vector()
			} else {
				Vector()
			}
		} else {
			Vector()
		}
		Changes(entity, events = events)
	}
}