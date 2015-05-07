package input

import math.Vec3
import system._
import org.lwjgl.glfw.GLFW._

class PlayerInputGround(horizontalSpeed: Float) extends InputReceiver(
	// Key pressed
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("facingLeft") = true
			entity.flags("movingLeft") = true
			NoChange
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("facingLeft") = false
			entity.flags("movingRight") = true
			NoChange
		}),
		(GLFW_KEY_SPACE, (delta, entity) => {
			if (entity.timers.contains("jump")) {
				entity.timers("jump").start()
			}
			NoChange
		}),
		(GLFW_KEY_PERIOD, (delta, entity) => {
			if (entity.timers.contains("shoot")) {
				entity.timers("shoot").start()
			}
			NoChange
		})
	),
	// Key held
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("facingLeft") = true
			entity.flags("movingLeft") = true
			Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("facingLeft") = false
			entity.flags("movingRight") = true
			Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed)
		})
	),
	// Key released
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("facingLeft") = true
			entity.flags("movingLeft") = false
			Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("facingLeft") = false
			entity.flags("movingRight") = false
			Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_SPACE, (delta, entity) => {
			if (entity.timers.contains("jump")) {
				entity.timers("jump").reset()
			}
			Translation(Vec3(0, 0, 0)) // Do nothing
		})
	))