package resource

import org.lwjgl.opengl.GL15._
import scala.io.Source
import org.lwjgl.BufferUtils

object Mesh {

/**
  * Load .ply formated meshes.
  */
	def apply(filePath: String) = {
		val lines = Source.fromFile(filePath).getLines().toVector

		var i = 0
		var line = lines(i)

		var vertices = 0
		var faces = 0
		var properties = 0

		while (line != "end_header") {
			line match {
				case s if s.startsWith("element vertex") => {
					vertices = line.split(' ')(2).toInt
				}
				case s if s.startsWith("element face") => {
					faces = line.split(' ')(2).toInt
				}
				case s if s.startsWith("property") => {
					properties += 1
				}
				case _ =>
			}
			i += 1
			line = lines(i)
		}

		i += 1
		line = lines(i)

		val vertexBuffer = BufferUtils.createFloatBuffer(vertices * properties)
		val indexBuffer = BufferUtils.createShortBuffer(faces * 3)
		for (j <- 0 until vertices) {
			line.split(' ').foreach(s => vertexBuffer.put(s.toFloat))
			i += 1
			line = lines(i)
		}
		vertexBuffer.flip()
		for (j <- 0 until faces) {
			line = lines(i)
			val split = line.split(' ')
			for (k <- 1 to split(0).toInt) {
				indexBuffer.put(split(k).toShort)
			}
			i += 1
		}
		indexBuffer.flip()
		new Mesh(vertexBuffer, indexBuffer, faces * 3)
	}
}

/**
  * Mesh object.
  */
class Mesh(
		vertexBuffer: java.nio.FloatBuffer, 
		indexBuffer: java.nio.ShortBuffer,
		val numIndices: Int) extends Resource {
	
	val vertexBufferId = glGenBuffers()
	val indexBufferId = glGenBuffers()

	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId)
	glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
	glBindBuffer(GL_ARRAY_BUFFER, 0)

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId)
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

	def free() = {
		// TODO: This should clear buffers from GPU memory
	}

}