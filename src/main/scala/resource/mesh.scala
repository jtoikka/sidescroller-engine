package resource

import org.lwjgl.opengl.GL15._

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