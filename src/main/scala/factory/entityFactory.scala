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

/**
  * Factory methods for creating entities. When possible, entities should be
  * created through prefabs instead.
  */ 
object EntityFactory {
	// def createPlayer(position: Vec3): Entity = {
	// 	val playerSpeed = 32.0f * 1.0f
	// 	val components = Vector(
	// 		SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(1, 1, 1)),
	// 		SpriteComponent("walkright1", "testsheet", 0)//,
	// 		// InputComponent(new PanInput(playerSpeed))
	// 	)
	// 	new Entity(components, Vector(), position, "player")
	// }

/**
  * Creates a purely visual entity for a level.
  *
  * @param mesh The name of the level's mesh.
  * @param texture The name of the tile sheet to use.
  * @param position The level's offset.
  */
	def createLevelVisuals(
		mesh: String, texture: String, position: Vec3): Entity = {
		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(1, 1, 1)),
			ModelComponent(mesh, texture)
		)
		new Entity(components, Vector(), position, "levelVisuals")
	}

/**
  * Creates a camera entity.
  *
  * @param position The position of the camera.
  * @param width The width of the viewport in pixels
  * @param height The height of the viewport in pixels
  * @param zNear The near plane
  * @param zFar The far plane
  */
	def createCamera(
		position: Vec3, 
		width: Int, height: Int, 
		zNear: Float, zFar: Float): Entity = {
		new Entity(Vector(
			CameraComponent(zNear, zFar, width, height, true),
			BehaviourComponent(Vector())//new CameraBehaviour(List())))
		), Vector(), position, "camera")
	}

/**
  * Creates a pass-through block at the given coordinates.
  */
	def createPassBlock(x: Int, y: Int, w: Int, h: Int, t: Int): Entity = {
		val pos = Vec3(x + w/2.0f, y - h/2.0f, 0.0f)

		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(w, h, 1)),
			// SpriteComponent("black", "tiles", 0), //uncomment for debugging
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

/**
  * Creates a solid block at the given coordinates.
  */
	def createBlock(x: Int, y: Int, w: Int, h: Int, t: Int): Entity = {
		val pos = Vec3(x + w/2.0f, y - h/2.0f, 0.0f)

		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(w, h, 1)),
			// SpriteComponent("black", "tiles", 0), //uncomment for debugging
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

/**
  * Creates a level-change trigger.
  *
  * @param x Horizontal coordinate
  * @param y Vertical coordinate
  * @param w Width
  * @param h Height
  * @param from The level (scene) to change from
  * @param to The level (scene) to change to
  */
	def createLevelTrigger(x: Int, y: Int, w: Int, h: Int, from: String, to: String): Entity = {
		val pos = Vec3(x + w/2.0f, y - h/2.0f, 0.0f)
		val components = Vector(
			SpatialComponent(Utility.axisAngle(Vec3(0, 1, 0), 0), Vec3(w, h, 1)),
			// SpriteComponent("black", "tiles", 0), //uncomment for debugging
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