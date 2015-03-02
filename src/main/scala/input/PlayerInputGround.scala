package input

import math.Vec3
import system.Acceleration
import org.lwjgl.glfw.GLFW._

class PlayerInputGround(horizontalSpeed: Float) extends InputReceiver(
	// Key pressed
	Map(
		// (GLFW_KEY_W, delta => Acceleration(Vec3(0, 1, 0) * delta * panSpeed)),
		(GLFW_KEY_A, delta => Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)),
		// (GLFW_KEY_S, delta => Translation(Vec3(0, -1, 0) * delta * panSpeed)),
		(GLFW_KEY_D, delta => Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed))
	),
	// Key held
	Map(
		// (GLFW_KEY_W, delta => Acceleration(Vec3(0, 1, 0) * delta * panSpeed)),
		(GLFW_KEY_A, delta => Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)),
		// (GLFW_KEY_S, delta => Translation(Vec3(0, -1, 0) * delta * panSpeed)),
		(GLFW_KEY_D, delta => Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed))
	),
	// Key released
	Map(
		// (GLFW_KEY_W, delta => Acceleration(Vec3(0, 1, 0) * delta * panSpeed)),
		(GLFW_KEY_A, delta => Acceleration(Vec3(-1, 0, 0) * delta * horizontalSpeed)),
		// (GLFW_KEY_S, delta => Translation(Vec3(0, -1, 0) * delta * panSpeed)),
		(GLFW_KEY_D, delta => Acceleration(Vec3(1, 0, 0) * delta * horizontalSpeed))
	)) {}