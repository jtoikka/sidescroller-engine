package factory

import entity.Entity
import math._
import entity._
import input.InputReceiver
import org.lwjgl.glfw.GLFW._
import system.Translation

object EntityFactory {
	def createPlayer(position: Vec3): Entity = {
		val components = Vector(
			SpatialComponent(Quaternion.axisAngle(Vec3(0, 1, 0), 0), Vec3(8, 8, 0)),
			SpriteComponent("walkright1", "testsheet")
		)
		new Entity(components, Vector(), position, "player")
	}

	def createCamera(
		position: Vec3, 
		width: Int, height: Int, 
		zNear: Float, zFar: Float): Entity = {
		val camSpeed = 32.0f * 10.0f
		new Entity(Vector(
			InputComponent(new InputReceiver(
				Map(
					(GLFW_KEY_W, delta => Translation(Vec3(0.0f, 1.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_A, delta => Translation(Vec3(-1.0f, 0.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_S, delta => Translation(Vec3(0.0f, -1.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_D, delta => Translation(Vec3(1.0f, 0.0f, 0.0f) * delta * camSpeed))
				),
				Map(
					(GLFW_KEY_W, delta => Translation(Vec3(0.0f, 1.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_A, delta => Translation(Vec3(-1.0f, 0.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_S, delta => Translation(Vec3(0.0f, -1.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_D, delta => Translation(Vec3(1.0f, 0.0f, 0.0f) * delta * camSpeed))
				),
				Map(
					(GLFW_KEY_W, delta => Translation(Vec3(0.0f, 1.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_A, delta => Translation(Vec3(-1.0f, 0.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_S, delta => Translation(Vec3(0.0f, -1.0f, 0.0f) * delta * camSpeed)),
					(GLFW_KEY_D, delta => Translation(Vec3(1.0f, 0.0f, 0.0f) * delta * camSpeed))
				)
			)),
		CameraComponent(zNear, zFar, width, height, true)
		), Vector(), position, "camera")
	}
}