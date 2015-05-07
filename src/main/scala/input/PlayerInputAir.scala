package input

import math.Vec3
import system._
import org.lwjgl.glfw.GLFW._

class PlayerInputAir(horizontalSpeed: Float) extends InputReceiver(
	// Key pressed
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = true
			NoChange
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = true
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
			entity.flags("movingLeft") = true
			NoChange
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = true
			NoChange
		})
	),
	// Key released
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = false
			NoChange
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = false
			NoChange
		}),
		(GLFW_KEY_SPACE, (delta, entity) => {
			if (entity.timers.contains("jump")) {
				entity.timers("jump").reset()
			}
			NoChange
		})
	)
) {}