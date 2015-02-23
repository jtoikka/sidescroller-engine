package resource

import entity.Entity

import scala.io.Source
import scala.collection.mutable.Map

import org.lwjgl.BufferUtils

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ResourceLoadException(message: String) extends Exception(message) {}

class ResourceManager(directory: String) {

	private val TexDir = "textures"
	private val MeshDir = "meshes"
	private val SoundDir = "audio"
	private val SpriteSheetDir = "spriteSheets"
	private val AnimationDir = "animations"
	private val PrefabDir = "prefabs"
	private val StateMachineDir = "stateMachines"
	private val ShaderDir = "shaders"

	private def getFilesInFolder[T](folder: String): Map[String, Option[T]]= {
		val files = new File(directory + folder).
			listFiles.filter(!_.getName.startsWith("."))

		Map(files.map {file =>
			(file.getName, Option.empty[T])
		}.toSeq: _*)
	}
	private val textures = getFilesInFolder[Texture](TexDir)
	private val meshes = getFilesInFolder[Mesh](MeshDir)
	private val sounds = getFilesInFolder[Sound](SoundDir)
	private val spriteSheets = getFilesInFolder[SpriteSheet](SpriteSheetDir)
	private val animation = getFilesInFolder[Animation](AnimationDir)
	private val prefabs = getFilesInFolder[Entity](PrefabDir)
	private val stateMachines = getFilesInFolder[StateMachine](StateMachineDir)
	private val shaderPrograms = getFilesInFolder[ShaderProgram](ShaderDir)

	def getTexture(name: String): Texture = {
		if (textures.contains(name)) {
			val texture = textures(name)
			texture match {
				case Some(tex) => tex
				case None => {
					val tex = loadTexture(directory + TexDir)
					textures(name) = Some(tex)
					tex
				}
			}
		} else {
			throw new ResourceLoadException("Texture " + name + " does not exist.")
		}
	}

	def getSpriteSheet(name: String): SpriteSheet = {
		if (spriteSheets.contains(name)) {
			val spriteSheet = spriteSheets(name)
			spriteSheet match {
				case Some(sheet) => sheet
				case None => {
					val sheet = loadSpriteSheet(directory + SpriteSheetDir)
					spriteSheets(name) = Some(sheet)
					sheet
				}
			}
		} else {
			throw new ResourceLoadException("Sprite sheet " + name + " does not exist.")
		}
	}

	def getShaderProgram(name: String): ShaderProgram = ???

	def getMesh(name: String): Mesh = ???

	private def loadMesh(filePath: String): Mesh = {
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

	private def loadTexture(filePath: String): Texture = {
		try {
      val file = new File(filePath)
      val image = ImageIO.read(file)
      new Texture(image)
    } catch {
      case e: Exception => {
        throw new ResourceLoadException("Texture " + filePath + " does not exist.")
      }
    }
	}

	private def loadSpriteSheet(filePath: String): SpriteSheet = {
		SpriteSheet(filePath)
	}

	private def loadShaderProgram() = {}

}