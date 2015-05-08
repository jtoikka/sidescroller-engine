package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import event.EntitySpawnEvent

/**
  * Turret behaviour, shoots at a fixed rate.
  */
class TurretBehaviour(val args: List[String]) extends Behaviour {
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			entity.timers("shoot") = new Timer(maxTime = 1.0f)
			entity.timers("shoot").start()
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val events = if (entity.timers.contains("shoot")) {
			val timer = entity.timers("shoot")
			if (!timer.isRunning) {
				timer.start()
				Vector(
					EntitySpawnEvent(
						false, "bullet", 
						entity.position - Vec3(14.0f, 0, 0), 
						scene,
						Vector(Acceleration(Vec3(-80.0f, 0, 0))))
				)
			} else {
				Vector()
			}
		} else {
			Vector()
		}
		Changes(entity, events = events)
	}
}