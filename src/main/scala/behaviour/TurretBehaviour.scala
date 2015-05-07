package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import event.EntitySpawnEvent

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
						entity.position, 
						scene)
				)
				// println("Shoot")
			} else {
				Vector()
			}
		} else {
			Vector()
		}
		Changes(entity, events = events)
	}
}