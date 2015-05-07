package resource

import argonaut._, Argonaut._

import scala.io.Source
import scala.collection.mutable.ArrayBuffer

import org.lwjgl.BufferUtils

class TiledMap(tiled: Tiled) extends Resource {


	def toMesh: Mesh = {
		val tileset = tiled.tilesets.head
		val tilesetTileW = tileset.tilewidth
		val tilesetTileH = tileset.tileheight
		val tilesetW = tileset.imagewidth
		val tilesetH = tileset.imageheight

		val tilewidth = tiled.tilewidth
		val tileheight = tiled.tileheight

		val tilesX = tilesetW / tilewidth
		val tilesY = tilesetH / tileheight

		val vertices = ArrayBuffer[Float]()
		val indices = ArrayBuffer[Short]()

		var indexCount = 0

		var layerDepth = -100.0f
		for (layer <- tiled.layers) {
			for (x <- 0 until tiled.width; y <- 0 until tiled.height) {
				// for (x <- 0 until 1; y <- 0 until tiled.height) {
				val dataPoint = layer.data(x + y * layer.width)
				if (dataPoint != 0) {
					val origX = x * tilewidth
					val origY = y * tileheight

					val tilePosY = (dataPoint - 1) / tilesX
					// println("tilePos: " + tilePosY)
					// println("d: " + dataPoint)
					// println("tiles x: " + tilesX)
					val tilePosX = dataPoint - 1 - tilePosY * tilesX

					val u = tilePosX / tilesX.toFloat
					val v = tilePosY / tilesY.toFloat
					val s = (tilePosX + 1) / tilesX.toFloat
					val t = (tilePosY + 1) / tilesY.toFloat

					val x1 = origX
					val x2 = origX + tilewidth
					val y1 = tiled.height * tileheight - origY
					val y2 = tiled.height * tileheight - (origY + tileheight)
					val z  = layerDepth
					vertices ++= Vector(
						x1, y1, z,  0, 0, 1,  u, v,
						x1, y2, z,  0, 0, 1,  u, t,
						x2, y2, z,  0, 0, 1,  s, t,
						x2, y1, z,  0, 0, 1,  s, v
					)
					indices ++= Vector[Short](
						(indexCount + 0).toShort, (indexCount + 1).toShort, (indexCount + 2).toShort,
						(indexCount + 2).toShort, (indexCount + 3).toShort, (indexCount + 0).toShort
					)
					indexCount += 4
				} 
			}
			layerDepth += 30.0f
		}
		// println(vertices)
		val properties = 8
		val vertexBuffer = BufferUtils.createFloatBuffer(vertices.length)
		for (f <- vertices) {
			vertexBuffer.put(f)
		}
		vertexBuffer.flip()
		val indexBuffer = BufferUtils.createShortBuffer(indices.length)
		for (i <- indices) {
			indexBuffer.put(i)
		}
		indexBuffer.flip()

		new Mesh(vertexBuffer, indexBuffer, indices.length)
	}
}

object TiledMap {
	def apply(filePath: String): TiledMap = {
		val input = Source.fromFile(filePath).mkString
		val source = input.decodeOption[Tiled]

		new TiledMap(source.get)
	}
}

case class Tiled(
	width: Int, height: Int, 
	layers: List[Layer], 
	tileheight: Int, tilewidth: Int, 
	tilesets: List[TileSet], 
	renderorder: String, version: Int)

object Tiled {
	implicit def TiledCodecJson: CodecJson[Tiled] =
		casecodec8(Tiled.apply, Tiled.unapply)(
			"width", "height", 
			"layers", 
			"tileheight", "tilewidth", 
			"tilesets", 
			"renderorder", "version")
}

case class Layer(
		data: List[Int],
		width: Int, height: Int,
		name: String,
		opacity: Int,
		layerType: String,
		visible: Boolean,
		x: Int, y: Int)

object Layer {
	implicit def LayerCodecJson: CodecJson[Layer] =
		casecodec9(Layer.apply, Layer.unapply)(
			"data", 
			"width", "height", 
			"name", 
			"opacity", 
			"type", 
			"visible", 
			"x", "y")
}

case class TileSet(
	firstgid: Int,
	image: String,
	imageheight: Int,
	imagewidth: Int,
	margin: Int,
	name: String,
	spacing: Int,
	tileheight: Int,
	tilewidth: Int)

object TileSet {
	implicit def TileSetCodecJson: CodecJson[TileSet] = 
		casecodec9(TileSet.apply, TileSet.unapply)(
			"firstgid", "image", 
			"imageheight", "imagewidth", 
			"margin", "name", 
			"spacing", 
			"tileheight", "tilewidth"
		)
}