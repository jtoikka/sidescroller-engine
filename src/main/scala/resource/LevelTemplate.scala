package resource

import java.io._
import java.nio.file._
import system._
import math._
import scene.Scene
import factory.EntityFactory
import map._
import scala.collection.mutable.ArrayBuffer
import entity.Entity

class LevelTemplate(src: String) extends Resource {

	protected case class Block(x: Short, y: Short, w: Short, h: Short, t: Short)
	
	var width = 0
	var height = 0

	val paddingTop = 2
	val paddingBottom = 3
	val paddingLeft = 2
	val paddingRight = 2

	val tileSize = 16

	val numTilesW = 30
	val numTilesH = 20

	val numDivisionsW = 6
	val numDivisionH = 5

	val blocks = {
		val byteArray = Files.readAllBytes(Paths.get(src))
		val buf = java.nio.ByteBuffer.allocate(byteArray.size)
		buf.put(byteArray)
		buf.flip()
		val version = (buf.get, buf.get, buf.get, buf.get)
		println("Version: " + version)
		width = buf.getShort.toInt
		height = buf.getShort.toInt
		val numBlocks = buf.getInt
		println("Num blocks: " + numBlocks)
		val solid = for (i <- 0 until numBlocks) yield {
			val x = buf.getShort
			val y = buf.getShort
			val w = buf.getShort
			val h = buf.getShort
			val t = buf.getShort
			Block(x, y, w, h, t)
		}
		val numPass = buf.getInt
		val pass = for (i <- 0 until numPass) yield {
			val x = buf.getShort
			val y = buf.getShort
			val w = buf.getShort
			val h = buf.getShort
			val t = buf.getShort
			Block(x, y, w, h, t)
		}

		(solid, pass)
	}

	def toEntities(resourceManager: ResourceManager, tiledMap: String): Vector[Entity] = {
		resourceManager.getTiledMap(tiledMap) // Ensures the tiled map gets loaded

		blocks._1.map(block => {
			EntityFactory.createBlock(
				block.x * tileSize,
				block.y * tileSize,
				block.w * tileSize,
				block.h * tileSize, 
				block.t)
		}).toVector ++
		blocks._2.map(block => {
			EntityFactory.createPassBlock(
				block.x * tileSize,
				block.y * tileSize,
				block.w * tileSize,
				block.h * tileSize, 
				block.t)
		}) ++ Vector(EntityFactory.createLevelVisuals(tiledMap, "tileSheet", Vec3(2*16, 3*16, 0)))
	}

	//Temporary
	def toScene(resourceManager: ResourceManager, tiledMap: String): Scene = {
		val origin = Vec2(
			(numTilesW/2.0f) * tileSize,
			(numTilesH/2.0f + (paddingBottom - paddingTop) / 2.0f) * tileSize
		)
		val pixelsWide = tileSize * width * numTilesW
		val pixelsTall = tileSize * height * numTilesH
		val scene = new Scene(
			pixelsWide, 
			pixelsTall,  
			numDivisionsW * width,
			numDivisionH * height,
			Vector(
				new InputSystem(),
        new PhysicsSystem(Vector(Vec3(0, -9.81f * 16 * 3, 0))),
        new BehaviourSystem(),
        new StateSystem(),
        new AnimationSystem(resourceManager)
      ),
      EntityFactory.createCamera(
      	Vec3(origin, 0),
      	832/2, 480/2, 0.1f, 1000.0f
      )
		)
		val testPlayer0 = resourceManager.getPrefab("player", Vec3(4 * tileSize, 160, -50))

		// left wall
		// for (j <- 0 until room.h) {
		// 	scene.addEntity(EntityFactory.createBlock(0, 12 * tileSize + 15 * j * tileSize, 3 * tileSize, 4 * tileSize, 1))
		// }



		// val testTurret = resourceManager.getPrefab("turret", Vec3(296, 144, -50))

		scene.addEntity(testPlayer0)
		// scene.addEntity(testTurret)
		blocks._1.foreach(block => {
			scene.addEntity(
				EntityFactory.createBlock(
					block.x * tileSize,
					block.y * tileSize,
					block.w * tileSize,
					block.h * tileSize, 
					block.t))
		})
		blocks._2.foreach(block => {
			scene.addEntity(
				EntityFactory.createPassBlock(
					block.x * tileSize,
					block.y * tileSize,
					block.w * tileSize,
					block.h * tileSize, 
					block.t))
			})
		scene
	}

	// Level format TODO:
	/*
	 * + Add physics parameters? 
	 * + Add spawn points
	 */
}