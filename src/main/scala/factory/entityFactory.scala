package factory

import entity.Entity
import math._
import entity._
import input.InputReceiver
import input._
import org.lwjgl.glfw.GLFW._
import system.Translation
import behaviour.CameraBehaviour

object EntityFactory {
	def createPlayer(position: Vec3): Entity = {
		val playerSpeed = 32.0f * 1.0f
		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(1, 1, 1)),
			SpriteComponent("walkright1", "testsheet", 0)//,
			// InputComponent(new PanInput(playerSpeed))
		)
		new Entity(components, Vector(), position, "player")
	}

	def createCamera(
		position: Vec3, 
		width: Int, height: Int, 
		zNear: Float, zFar: Float): Entity = {
		val camSpeed = 32.0f * 10.0f
		new Entity(Vector(
		CameraComponent(zNear, zFar, width, height, true),
		BehaviourComponent(Vector(new CameraBehaviour(List())))
		), Vector(), position, "camera")
	}
}