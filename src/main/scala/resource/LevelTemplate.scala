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

/**
  * A level template can be turned into collision data for a level.
  */
class LevelTemplate(src: String) extends Resource {

	protected case class Block(x: Short, y: Short, w: Short, h: Short, t: Short)
	
	var width = 0
	var height = 0

/* Parameters from level editor --------------------------------------------- */
	val paddingTop = 2
	val paddingBottom = 3
	val paddingLeft = 2
	val paddingRight = 2

	val tileSize = 16

	val numTilesW = 30
	val numTilesH = 20

	val numDivisionsW = 6
	val numDivisionH = 5
/* -------------------------------------------------------------------------- */

/**
  * Read blocks from binary file. The format is as follows:
  * - 4 bytes for version info
  * - 2 bytes for level width
  * - 2 bytes for level height
  * - 4 bytes for number of blocks
  * - 10 bytes per number of block
  * - 4 bytes for number of pass-through blocks
  * - 10 bytes per number of pass-through block
  */
	val blocks = {
		val byteArray = Files.readAllBytes(Paths.get(src))
		val buf = java.nio.ByteBuffer.allocate(byteArray.size)
		buf.put(byteArray)
		buf.flip()

		val version = (buf.get, buf.get, buf.get, buf.get)

		width = buf.getShort.toInt
		height = buf.getShort.toInt

		val numBlocks = buf.getInt
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

/**
  * Transform blocks into entities, and add level visuals [tiledMap].
  */
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
		}) ++ 
		Vector(
			EntityFactory.createLevelVisuals(tiledMap, "tileSheet", Vec3(2*16, 3*16, 0))
		) ++ resourceManager.getLevelEntities(tiledMap) 
	}
}