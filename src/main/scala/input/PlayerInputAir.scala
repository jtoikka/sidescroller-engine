package input

import math.Vec3
import system._
import org.lwjgl.glfw.GLFW._

/**
  * Handles player input when airborn.
  */
class PlayerInputAir(horizontalSpeed: Float) extends InputReceiver(
	// Key pressed
	Map(
		// Move left
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = true
			NoChange
		}),
		// Move right
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = true
			NoChange
		}),
		// Shoot
		(GLFW_KEY_M, (delta, entity) => {
			if (entity.timers.contains("shoot")) {
				entity.timers("shoot").start()
			}
			NoChange
		})
	),
	// Key held
	Map(
		// Move left
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = true
			NoChange
		}),
		// Move right
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = true
			NoChange
		})
	),
	// Key released
	Map(
		// Move left
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = false
			NoChange
		}),
		// Move right
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = false
			NoChange
		}),
		// End jump
		(GLFW_KEY_SPACE, (delta, entity) => {
			if (entity.timers.contains("jump")) {
				entity.timers("jump").reset()
			}
			NoChange
		})
	)
) {}