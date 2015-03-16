package input

import math.Vec3
import system.Translation
import org.lwjgl.glfw.GLFW._

class PanInput(panSpeed: Float) extends InputReceiver(
	// Key pressed
	Map(
		(GLFW_KEY_W, (delta, entity) => Translation(Vec3(0, 1, 0) * delta * panSpeed)),
		(GLFW_KEY_A, (delta, entity) => Translation(Vec3(-1, 0, 0) * delta * panSpeed)),
		(GLFW_KEY_S, (delta, entity) => Translation(Vec3(0, -1, 0) * delta * panSpeed)),
		(GLFW_KEY_D, (delta, entity) => Translation(Vec3(1, 0, 0) * delta * panSpeed))
	),
	// Key held
	Map(
		(GLFW_KEY_W, (delta, entity) => Translation(Vec3(0, 1, 0) * delta * panSpeed)),
		(GLFW_KEY_A, (delta, entity) => Translation(Vec3(-1, 0, 0) * delta * panSpeed)),
		(GLFW_KEY_S, (delta, entity) => Translation(Vec3(0, -1, 0) * delta * panSpeed)),
		(GLFW_KEY_D, (delta, entity) => Translation(Vec3(1, 0, 0) * delta * panSpeed))
	),
	// Key released
	Map(
		(GLFW_KEY_W, (delta, entity) => Translation(Vec3(0, 1, 0) * delta * panSpeed)),
		(GLFW_KEY_A, (delta, entity) => Translation(Vec3(-1, 0, 0) * delta * panSpeed)),
		(GLFW_KEY_S, (delta, entity) => Translation(Vec3(0, -1, 0) * delta * panSpeed)),
		(GLFW_KEY_D, (delta, entity) => Translation(Vec3(1, 0, 0) * delta * panSpeed))
	)) {}