package factory

import scene.Scene
import system._
import math._
import resource.ResourceManager
import entity.Component._
import map.LevelMap

object SceneFactory {
	
	def createMenu(resourceManager: ResourceManager): Scene = {
		val scene = new Scene(
			832, 480, 1, 1, Vector(
				new InputSystem(),
				new TriggerSystem(),
				new StateSystem(),
				new BehaviourSystem()
			),
			EntityFactory.createCamera(
				Vec3(854 / 2, 480 / 2, 0),
				832, 480, 0.1f, 100.0f
			)
		)
		val startButton = resourceManager.getPrefab("startButton", Vec3(832 / 2, 480 / 2, 0))
		val cursor = resourceManager.getPrefab("cursor", Vec3(0, 0, 0))
		scene.addEntity(startButton)
		scene.addEntity(cursor)

		scene
	}

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

	def createGame(resourceManager: ResourceManager): Scene = {
		val systems = Vector(
			new InputSystem(),
      new PhysicsSystem(Vector(Vec3(0, -9.81f * 16 * 3, 0))),
      new TriggerSystem(),
      new BehaviourSystem(),
      new StateSystem(),
      new AnimationSystem(resourceManager)
    )

    val origin = Vec2(
			(numTilesW/2.0f) * tileSize,
			(numTilesH/2.0f + (paddingBottom - paddingTop) / 2.0f) * tileSize
		)

    val camera = EntityFactory.createCamera(
    	Vec3(origin, 0),
    	832/2, 480/2, 0.1f, 1000.0f
    )

    val player = resourceManager.getPrefab("player", Vec3(4 * tileSize, 160, -50))
		val startEntities = resourceManager.getTemplate("start").toEntities(resourceManager, "start")

		val scene = new Scene(
			pixelsWide,
			pixelsTall,
			numDivisionsW * width,
			numDivisionsH * height,
			systems,
			camera)

		startEntities.foreach(scene.addEntity(_))
		scene.addEntity(player)

		val rightTrigger = EntityFactory.createLevelTrigger((27.5f * 16).toInt, 7 * 16, 16, 3 * 16, "start", "1")
		scene.addEntity(rightTrigger)

		scene
	}
}