package factory

import entity.Entity
import math._
import entity._
import input.InputReceiver
import input._
import org.lwjgl.glfw.GLFW._
import system.Translation
import behaviour._
import physics._

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

	def createLevelVisuals(
		mesh: String, texture: String, position: Vec3): Entity = {
		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(1, 1, 1)),
			ModelComponent(mesh, texture)
		)
		new Entity(components, Vector(), position, "levelVisuals")
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

	def createPassBlock(x: Int, y: Int, w: Int, h: Int, t: Int): Entity = {
		val pos = Vec3(x + w/2.0f, y - h/2.0f, 0.0f)

		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(w, h, 1)),
			// SpriteComponent("black", "tiles", 0),
			CollisionComponent(
				Vector(), 
				Vector(
					new BoxCollision(Vec2(0, 0), w, h)
				), Vector(), true),
			PhysicsComponent(
				Vec3(0, 0, 0),
				Vec3(0, 0, 0),
				0, 0, 1, 0, 0, 0, true)
		)

		new Entity(components, Vector(), pos, "block")
	}

	def createBlock(x: Int, y: Int, w: Int, h: Int, t: Int): Entity = {
		val pos = Vec3(x + w/2.0f, y - h/2.0f, 0.0f)

		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(w, h, 1)),
			// SpriteComponent("black", "tiles", 0),
			CollisionComponent(
				Vector(), 
				Vector(
					new BoxCollision(Vec2(0, 0), w, h)
				), Vector(), false),
			PhysicsComponent(
				Vec3(0, 0, 0),
				Vec3(0, 0, 0),
				0, 0, 1, 0, 0, 0, true)
		)

		new Entity(components, Vector(), pos, "block")
	}

	def createLevelTrigger(x: Int, y: Int, w: Int, h: Int, from: String, to: String): Entity = {
		val pos = Vec3(x + w/2.0f, y - h/2.0f, 0.0f)
		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(w, h, 1)),
			SpriteComponent("black", "tiles", 0),
			CollisionComponent(
				Vector(),
				Vector(),
				Vector(
					new TriggerBoxCollision(Vec2(0, 0), w, h, "levelTrigger")
				), false
			),
			PhysicsComponent(
				Vec3(0, 0, 0),
				Vec3(0, 0, 0),
				0, 0, 1, 0, 0, 0, true),
			BehaviourComponent(Vector(
				BehaviourManager.createBehaviour("levelTrigger", List(from, to))
			))
			)
		new Entity(components, Vector(), pos, "levelTrigger")
	}
}