package input

import math.Vec3
import system._
import org.lwjgl.glfw.GLFW._

class PlayerInputAir(horizontalSpeed: Float) extends InputReceiver(
	// Key pressed
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = true
			Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = true
			Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed)
		})
	),
	// Key held
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = true
			Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = true
			Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed)
		})
	),
	// Key released
	Map(
		(GLFW_KEY_A, (delta, entity) => {
			entity.flags("movingLeft") = false
			Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_D, (delta, entity) => {
			entity.flags("movingRight") = false
			Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed)
		}),
		(GLFW_KEY_SPACE, (delta, entity) => {
			if (entity.timers.contains("jump")) {
				entity.timers("jump").reset()
			}
			Translation(Vec3(0, 0, 0)) // Do nothing
		})
	)
) {}