package factory

import scene.Scene
import system._
import math._
import resource.ResourceManager
import entity.Component._
import map.LevelMap
import entity.Entity

object SceneFactory {
	val ScreenWidth = 832
	val ScreenHeight = 480
	
/**
  * Creates the menu scene.
  */
	def createMenu(resourceManager: ResourceManager): Scene = {
		val scene = new Scene(
			ScreenWidth * 10, ScreenHeight * 10, 1, 1, Vector(
				new InputSystem(),
				new TriggerSystem(),
				new StateSystem(),
				new BehaviourSystem()
			),
			EntityFactory.createCamera(
				Vec3(ScreenWidth / 2, ScreenHeight / 2, 0), // Center
				ScreenWidth, ScreenHeight, 0.1f, 100.0f
			)
		)
		val startButton = 
			resourceManager.getPrefab(
				"startButton", 
				Vec3(ScreenWidth / 2, ScreenHeight / 2, 0)) // Center
		val cursor = resourceManager.getPrefab("cursor", Vec3(0, 0, 0))
		scene.addEntity(startButton)
		scene.addEntity(cursor)

		scene
	}

/**
  * The following parameters are from the game's level editor.
  */
	private val width = 30
	private val height = 20

	private val paddingTop = 2
	private val paddingBottom = 3
	private val paddingLeft = 2
	private val paddingRight = 2

	private val tileSize = 16

	private val numTilesW = 30
	private val numTilesH = 20

	private val numDivisionsW = 6
	private val numDivisionsH = 5

	private val pixelsWide = tileSize * width * numTilesW
	private val pixelsTall = tileSize * height * numTilesH

	private val origin = Vec2(
		(numTilesW/2.0f) * tileSize,
		(numTilesH/2.0f + (paddingBottom - paddingTop) / 2.0f) * tileSize
	)

/**
  * Creates the first game scene. Temporary hard-coded solution.
  */
	def createGame(resourceManager: ResourceManager): Scene = {
		val systems = Vector(
			new InputSystem(),
      new PhysicsSystem(Vector(Vec3(0, -9.81f * 16 * 3, 0))), // Gravity, value selected by what feels good in-game
      new TriggerSystem(),
      new BehaviourSystem(),
      new StateSystem(),
      new AnimationSystem(resourceManager)
    )

    val camera = EntityFactory.createCamera(
    	Vec3(origin, 0),
    	ScreenWidth/2, ScreenHeight/2, 0.1f, 1000.0f // Divide height and width by two
    )																							 // to zoom camera in

    val player = resourceManager.getPrefab("player", Vec3(4 * tileSize, 160, -50))
		val startEntities = resourceManager.getTemplate("0").toEntities(resourceManager, "0")

		val scene = new Scene(
			pixelsWide,
			pixelsTall,
			numDivisionsW,
			numDivisionsH,
			systems,
			camera)

		startEntities.foreach(scene.addEntity(_))
		scene.addEntity(player)

		val rightTrigger = EntityFactory.createLevelTrigger((28.5f * 16).toInt, 7 * 16, 16, 3 * 16, "0", "1")
		scene.addEntity(rightTrigger)

		scene
	}

/**
  * Loads a room and generates a scene from it.
  */
	def loadRoom(
		resourceManager: ResourceManager, 
		systems: Vector[System], 
		camera: Entity, room: Int, 
		player: Entity): Scene = {
		val scene = new Scene(
			pixelsWide,
			pixelsTall,
			numDivisionsW,
			numDivisionsH,
			systems,
			camera)

		val roomAsString = room.toString
		val entities = resourceManager.getTemplate(roomAsString).toEntities(resourceManager, roomAsString)

		entities.foreach(scene.addEntity(_))
		scene.addEntity(player)

		// The triggers are set to be just outside of the view of the camera.
		val leftTrigger = EntityFactory.createLevelTrigger((0.5f * 16).toInt, 7 * 16, 16, 3 * 16, roomAsString, (room - 1).toString)
		val rightTrigger = EntityFactory.createLevelTrigger((28.5f * 16).toInt, 7 * 16, 16, 3 * 16, roomAsString, (room + 1).toString)
		scene.addEntity(rightTrigger)
		scene.addEntity(leftTrigger)

		scene
	}
}