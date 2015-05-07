package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import scala.collection.mutable.ArrayBuffer

class JumpBehaviour(val args: List[String]) extends Behaviour {
	val Speed = 50.0f //29.0f * 2
	val InitialSpeed = 60.0f //88.55f
	// val TerminalSpeed = 20.0f * 2
	val EndTime = 0.18f
	val AdjustedSpeed = Speed / EndTime

	val airSpeedX = 80.0f // This shouldn't be here...

	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			entity.timers("jump") = new Timer(0.06f, EndTime)
			initialized = true
		}
	}

	var jumping = false
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		// Check to see if player is on the ground, and set the appropriate triggers
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

		val changes = ArrayBuffer[StateChange]()

		// If the player is jumping, accelerate vertically
		if (entity.timers.contains("jump")) {
			val jumpTimer = entity.timers("jump")
			val time = jumpTimer.time
			if (jumpTimer.isRunning) {
				if (time == 0) {
					jumping = true
					changes += Acceleration(Vec3(0, 1, 0) * InitialSpeed)
				} else {
					val timeLeft = EndTime - time
					changes += Acceleration(
						Vec3(0, 1, 0) * AdjustedSpeed * scala.math.pow(timeLeft, 1.2).toFloat
					)
				}
			}
		}
		if (entity.triggers("inAir")) {
			entity(PhysicsComp) match {
				case Some(p) => {
					if (!entity.flags("movingLeft") && !entity.flags("movingRight") || entity.flags("movingLeft") && entity.flags("movingRight")) {
						changes += SetVelocity(Vec3(0, 1, 1), Vec3(0, 0, 0))
					} else {
						if (entity.flags("movingLeft")) {
							changes += SetVelocity(Vec3(0, 1, 1), Vec3(-airSpeedX , 0, 0))
						}
						if (entity.flags("movingRight")) {
							changes += SetVelocity(Vec3(0, 1, 1), Vec3(airSpeedX , 0, 0))
						}
					}
				}
				case _ =>
			}
		}
		Changes(entity, changes.toVector)
	}
}