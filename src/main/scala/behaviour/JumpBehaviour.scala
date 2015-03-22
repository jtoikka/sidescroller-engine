package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._

class JumpBehaviour(val args: List[String]) extends Behaviour {
	val Speed = 85.0f
	val TerminalSpeed = 10.0f
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			entity.timers("jump") = new Timer(0.12f, 0.8f)
			initialized = true
		}
	}

	var jumping = false
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		if (entity.privateEvents.find(event => {
			event match {
				case t: TriggerEvent if (t.triggerName == "groundCheck") => {
					true
				}
				case _ => false
			}
		}).isDefined) {
			entity.triggers("onGround") = true
		} else {
			entity.triggers("inAir") = true
		}
		if (entity.timers.contains("jump")) {
			val jumpTimer = entity.timers("jump")
			if (jumpTimer.isRunning) {
				if (jumpTimer.time == 0) {
					jumping = true
					Changes(
					entity, Vector(
						Acceleration(
							Vec3(0, 1, 0) * Speed)
						)
					)
				} else {
					Changes(entity)
				}
			} else if (jumping) {
				jumping = false
				entity(PhysicsComp) match {
					case Some(phys: PhysicsComponent) => {
						if (phys.velocity.y > 0) {
							Changes(
								entity, Vector(
										Acceleration(Vec3(0, -1.0f, 0) * phys.velocity.y + Vec3(0, 1, 0) * TerminalSpeed)
									)
							)
						} else {
							Changes(entity)
						}
					}
					case _ => Changes(entity)
				}
			} else {
				Changes(entity)
			}
		} else {
			Changes(entity)
		}
	}
}