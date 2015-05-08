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
class BossBehaviour(val args: List[String]) extends Behaviour {
	var player: Option[Entity] = None
	override def initialize(entity: Entity, scene: Scene): Unit = {
		player = scene.entities.find(e => e.tag == "player")
		if (!initialized) {
			if (!entity.flags("movingLeft") && !entity.flags("movingRight")) {
				entity.flags("movingLeft") = true
				entity.flags("facingLeft") = true
			}
			entity.timers("doJump") = new Timer(maxTime = 0.9f)
			entity.timers("doTurn") = new Timer(maxTime = 1.0f)
			entity.timers("doShoot") = new Timer(maxTime = 0.8f)
			entity.timers("doJump").start()
			initialized = true
		}
	}

	val rng = new util.Random()
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		if (entity.timers.contains("doJump")) {
			val timer = entity.timers("doJump")
			if (!timer.isRunning) {
				if (entity(PhysicsComp).get.velocity.y > -1.0f) {
					timer.start()
					entity.timers("jump").start()
				}
			}
		}
		val events = if (entity.timers.contains("doShoot")) {
			val timer = entity.timers("doShoot")
			if (!timer.isRunning) {
				timer.start()
				player match {
					case Some(p) => {
						val dir = (p.position - entity.position).normalize
						Vector(
							EntitySpawnEvent(
								false, "bullet", 
								entity.position + dir * 15.0f, 
								scene,
								Vector(
									Acceleration(dir * 130.0f)
								)
							)
						)
					}
					case _ => Vector()
				}
			} else {
				Vector()
			}
		} else Vector()
		
		// Change direction if at right wall
		if (entity.position.x > 16 * 26) {
			entity.flags("movingLeft") = true
			entity.flags("movingRight") = false
			entity.flags("facingLeft") = true
		// Change fireaction if at left wall
		} else if (entity.position.x < 16 * 4) {
			entity.flags("movingLeft") = false
			entity.flags("movingRight") = true
			entity.flags("facingLeft") = false
		// Randomly change direction at center
		} else if (entity.position.x/16 >= 14 && entity.position.x/16 < 15) {
			if (entity.timers.contains("doTurn")) {
				val timer = entity.timers("doTurn")
				if (!timer.isRunning) {
					timer.start()
					val r = rng.nextInt(2)
					if (r == 1) {
						entity.flags("movingLeft") = true
						entity.flags("movingRight") = false
						entity.flags("facingLeft") = true
					} else {
						entity.flags("movingLeft") = false
						entity.flags("movingRight") = true
						entity.flags("facingLeft") = false
					}
				}
			}
		} else {
			// println(entity.position.x)
		}
		Changes(entity, events = events)
	}
}