package render

import scene.Scene
import entity.Component._
import entity._
import resource.ResourceManager
import resource.Mesh
import resource.ShaderProgram
import math._

import org.lwjgl.opengl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.BufferUtils

import scala.collection.mutable.ArrayBuffer

class Renderer(screenWidth: Int, screenHeight: Int) {

	this.initialize

	def initialize() {
		glEnable(GL_BLEND)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_CULL_FACE)
    glEnable(GL_TEXTURE_2D)
    glDepthFunc(GL_LEQUAL)
    glCullFace(GL_BACK)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    glViewport(0, 0, screenWidth, screenHeight)
	}

	private val NumBytesFloat = 4

	private val spriteMap = collection.mutable.Map[String, ArrayBuffer[Float]]()

	def renderScene(
		scene: Scene, 
		resourceManager: ResourceManager) = {
		val meshShaderProgram = resourceManager.getShaderProgram("mesh")
		val spriteShaderProgram = resourceManager.getShaderProgram("sprite")

		setProgram(meshShaderProgram)
		setCameraMatrix(scene.camera, meshShaderProgram)

		scene.entities foreach {renderEntity(_, resourceManager, meshShaderProgram)}

		setProgram(spriteShaderProgram)
		setCameraMatrix2D(scene.camera, spriteShaderProgram)

		renderSpritesToScreen(resourceManager, spriteShaderProgram)
	}

	def setProgram(program: ShaderProgram) = {
		glUseProgram(program.id)
	}

	def setCameraMatrix(camera: Entity, program: ShaderProgram) = {
    camera(CameraComp) match {
      case Some(CameraComponent(zNear, zFar, width, height, orthographic)) => {
        val camLeft = camera.position.x - width / 2.0f
        val camRight = camera.position.x + width / 2.0f
        val camUp = camera.position.y - height / 2.0f
        val camDown = camera.position.y + height / 2.0f

        // TODO: Add perspective projection
        val cameraToClip = Camera.orthographicProjection4(
			    camLeft, camRight, camUp, camDown, zNear, zFar
			  )
	      val camClipBuffer = BufferUtils.createFloatBuffer(cameraToClip.size)
		    camClipBuffer.put(cameraToClip.asArray)
		    camClipBuffer.flip()

		    glUniformMatrix4(
		      program.uniforms("cameraToClipMatrix"), false, camClipBuffer)
      }
      case _ => {
      	println("Camera missing camera component")
      }
    }
	}

	def setCameraMatrix2D(camera: Entity, program: ShaderProgram) = {
    camera(CameraComp) match {
      case Some(CameraComponent(zNear, zFar, width, height, orthographic)) => {
        val camLeft = camera.position.x - width / 2.0f
        val camRight = camera.position.x + width / 2.0f
        val camUp = camera.position.y - height / 2.0f
        val camDown = camera.position.y + height / 2.0f

        // TODO: Add perspective projection
        val cameraToClip = Camera.orthographicProjection3(
			    camLeft, camRight, camUp, camDown
			  )
	      val camClipBuffer = BufferUtils.createFloatBuffer(cameraToClip.size)
		    camClipBuffer.put(cameraToClip.asArray)
		    camClipBuffer.flip()

		    glUniformMatrix3(
		      program.uniforms("cameraToClipMatrix"), false, camClipBuffer)
      }
      case _ => {
      	println("Camera missing camera component")
      }
    }
	}

	def renderEntity(
		entity: Entity, 
		resourceManager: ResourceManager, 
		program: ShaderProgram) = {
		entity(ModelComp) match {
			case Some(ModelComponent(meshId, textureId)) => {
				val transformation = entity(SpatialComp) match {
					case Some(SpatialComponent(rotation, scale)) => {
						Utility.scaleMatrix(scale) * 
						Utility.translationMatrix(entity.position) * 
						rotation.toMat
					}
					case _ => Utility.translationMatrix(entity.position)
				}

				val transBuffer = BufferUtils.createFloatBuffer(transformation.size)
            transBuffer.put(transformation.asArray)
            transBuffer.flip()

        glUniformMatrix4(
        	program.uniforms("modelToCameraMatrix"), false, transBuffer)

        renderMesh(resourceManager.getMesh(meshId), program)
			}
			case _ => 
		}
		entity(SpriteComp) match {
			case Some(SpriteComponent(sprite, spriteSheet)) => {
				renderSprite(entity, resourceManager)
			}
			case _ =>
		}
	}

	def renderMesh(mesh: Mesh, program: ShaderProgram) = {
		glBindBuffer(GL_ARRAY_BUFFER, mesh.vertexBufferId)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.indexBufferId)

    glVertexAttribPointer(
	  	program.attributes("position"),
	  	3, GL_FLOAT, false, NumBytesFloat * 8, 0)
    glVertexAttribPointer(
	  	program.attributes("position"),
	  	3, GL_FLOAT, false, NumBytesFloat * 8, NumBytesFloat * 3)
	  glVertexAttribPointer(
	  	program.attributes("uv"), 
	  	2, GL_FLOAT, false, NumBytesFloat * 8, NumBytesFloat * 6)

    glDrawElements(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_SHORT, 0)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
	}



	def renderSprite(entity: Entity, resourceManager: ResourceManager) = {
		entity(SpriteComp) match {
			case Some(SpriteComponent(spriteId, spriteSheetId)) => {
				val sheet = resourceManager.getSpriteSheet(spriteSheetId)
				val sprite = sheet(spriteId)

				val halfWidth = sprite.w / 2.0f
				val halfHeight = sprite.h / 2.0f

				val x1 = entity.position.x - halfWidth
				val x2 = entity.position.x + halfWidth
				val y1 = entity.position.y + halfHeight
				val y2 = entity.position.y - halfHeight

				// Texture coordinates
        val ux = sprite.x / sheet.width.toFloat
        val uy = sprite.y / sheet.height.toFloat
        val vx = (sprite.x + sprite.w) / sheet.width.toFloat
        val vy = (sprite.y + sprite.h) / sheet.height.toFloat

        if (!spriteMap.contains(spriteSheetId)) {
        	spriteMap(spriteSheetId) = new ArrayBuffer[Float]()
        }
        spriteMap(spriteSheetId) ++= Vector(
          x1, y1, ux, uy,
          x1, y2, ux, vy,
          x2, y1, vx, uy,
          x1, y2, ux, vy,
          x2, y2, vx, vy,
          x2, y1, vx, uy
        )
			}
			case _ => println("[error]Attempting to render sprite for invalid entity")
		}
	}

	var spriteVBO = 0

	def renderSpritesToScreen(resourceManager: ResourceManager, program: ShaderProgram) = {
		spriteMap.foreach {case (spriteSheetId, spriteBuffer) => {
			println(spriteBuffer)
			val sheet = resourceManager.getSpriteSheet(spriteSheetId)
			val tex = sheet.texture

      glActiveTexture(GL_TEXTURE0)
      val texIndex = getTexture(tex, resourceManager)
      val texUnif = glGetUniformLocation(program.id, tex)
    	glUniform1i(texUnif, texIndex)
      glBindTexture(GL_TEXTURE_2D, texIndex)


      val verticesBuffer = BufferUtils.createFloatBuffer(spriteBuffer.size)
      verticesBuffer.put(spriteBuffer.toArray)
      verticesBuffer.flip()

      if (spriteVBO == 0) spriteVBO = glGenBuffers()

      glBindBuffer(GL_ARRAY_BUFFER, spriteVBO)
      glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STREAM_DRAW)

      glVertexAttribPointer(
      	program.attributes("position"),
      	2, GL_FLOAT, false, NumBytesFloat * 4, 0)
      glVertexAttribPointer(
      	program.attributes("uv"), 
      	2, GL_FLOAT, false, NumBytesFloat * 4, NumBytesFloat * 2)

      glDrawArrays(GL_TRIANGLES, 0, spriteBuffer.size/4)

      glBindBuffer(GL_ARRAY_BUFFER, 0)
      spriteBuffer.clear()
		}}
	}

	private val loadedTextures = collection.mutable.Map[String, Int]()

	private def getTexture(
    texName: String, 
    resourceManager: ResourceManager) = {
    if (loadedTextures.contains(texName)) {
      loadedTextures(texName)
    } else {
      loadTexture(texName, resourceManager.getTexture(texName))
    }
  }

	def loadTexture(name: String, texture: resource.Texture): Int = {
    val id = glGenTextures()
    texture.bind(id)
    glTexImage2D(
      GL_TEXTURE_2D, 0, GL_RGBA, texture.width, texture.height, 0, 
      GL_RGBA, GL_UNSIGNED_BYTE, texture.getBuffer)
    loadedTextures(name) = id
    id
  }
}
