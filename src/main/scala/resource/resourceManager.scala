package resource

import entity._
import entity.Component._
import state.StateMachine

import scala.io.Source
import scala.collection.mutable.Map

import org.lwjgl.BufferUtils

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import math.Vec3
import math.Vec2

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
	private val TemplateDir = "templates"
	private val TiledMapDir = "tiledMap"
	private val LevelEntitiesDir = "levelEntities"

	private def getFilesInFolder[T](folder: String): Map[String, Option[T]]= {
		println("Getting files in folder: " + folder)
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
	private val animations = getFilesInFolder[Animation](AnimationDir)
	private val prefabs = getFilesInFolder[Entity](PrefabDir)
	private val stateMachines = getFilesInFolder[StateMachine](StateMachineDir)
	private val shaderPrograms = Map[String, ShaderProgram]()
	private val templates = getFilesInFolder[LevelTemplate](TemplateDir)
 	private val tiledMaps = getFilesInFolder[TiledMap](TiledMapDir)
 	private val levelEntities = getFilesInFolder[LevelEntities](LevelEntitiesDir)

	def getTexture(name: String): Texture = {
		val fullName = if (name.split('.').size > 1) name else name + ".png"
		if (textures.contains(fullName)) {
			val texture = textures(fullName)
			texture match {
				case Some(tex) => tex
				case None => {
					val tex = loadTexture(directory + TexDir + "/" + fullName)
					textures(fullName) = Some(tex)
					tex
				}
			}
		} else {
			throw new ResourceLoadException("Texture " + name + " does not exist.")
		}
	}

	def getSpriteSheet(name: String): SpriteSheet = {
		val fullName = if (name.split('.').size > 1) name else name + ".json"
		if (spriteSheets.contains(fullName)) {
			val spriteSheet = spriteSheets(fullName)
			spriteSheet match {
				case Some(sheet) => sheet
				case None => {
					val sheet = 
						loadSpriteSheet(directory + SpriteSheetDir + "/" + fullName)
					spriteSheets(fullName) = Some(sheet)
					sheet
				}
			}
		} else {
			throw new ResourceLoadException("Sprite sheet " + name + " does not exist.")
		}
	}

	def getAnimation(name: String): Animation = {
		val fullName = if (name.split('.').size > 1) name else name + ".json"
		if (animations.contains(fullName)) {
			val animation = animations(fullName)
			animation match {
				case Some(anim) => anim
				case None => {
					val anim = 
						loadAnimation(directory + AnimationDir + "/" + fullName)
					animations(fullName) = Some(anim)
					anim
				}
			}
		} else {
			throw new ResourceLoadException("Animation " + name + " does not exist.")
		}
	}

	def getShaderProgram(name: String): ShaderProgram = {
		if (shaderPrograms.contains(name)) {
			shaderPrograms(name)
		} else {
			val shaderProgram = ShaderManager.createProgram(
				directory + ShaderDir + "/" + name + ".vsh",
				directory + ShaderDir + "/" + name + ".fsh")
			shaderPrograms(name) = shaderProgram
			shaderProgram
		}
	}

	def getMesh(name: String): Mesh = {
		val fullName = if (name.split('.').size > 1) name else name + ".ply"
		if (meshes.contains(fullName)) {
			val mesh = meshes(fullName)
			mesh match {
				case Some(m) => m
				case None => {
					val m = Mesh(directory + MeshDir + "/" + fullName)
					meshes(fullName) = Some(m)
					m
				}
			}
		} else {
			throw new ResourceLoadException("Mesh " + name + " does not exist.")
		}
	}

	def getPrefab(
		name: String, 
		position: Vec3 = Vec3(0, 0, 0), 
		components: Vector[Component] = Vector()): Entity = {
		val fullName = if (name.split('.').size > 1) name else name + ".json"
		if (prefabs.contains(fullName)) {
			val prefab = prefabs(fullName)
			val entity = prefab match {
				case Some(entity) => entity
				case None => {
					val ent = PrefabLoader.load(directory + PrefabDir + "/" + fullName)
					prefabs(fullName) = Some(ent)
					ent
				}
			}
			val newEntity = entity.createCopy(position)
			components foreach (comp => {
				newEntity.updateComponent(comp)
			})
			newEntity
		} else {
			throw new ResourceLoadException("Prefab " + name + " does not exist.")
		}
	}

	def getTemplate(name: String): LevelTemplate = {
		val fullName = if (name.split('.').size > 1) name else name + ".lvl"
		if (templates.contains(fullName)) {
			val template = templates(fullName)
			template match {
				case Some(t) => t
				case None => {
					val t = new LevelTemplate(directory + TemplateDir + "/" + fullName)
					templates(fullName) = Some(t)
					t
				}
			}
		} else {
			throw new ResourceLoadException("Template " + name + " does not exist.")
		}
	}

	def getTiledMap(name: String): TiledMap = {
		val fullName = if (name.split('.').size > 1) name else name + ".json"
		if (tiledMaps.contains(fullName)) {
			val tiledMap = tiledMaps(fullName)
			tiledMap match {
				case Some(tiled) => tiled
				case None => {
					val tiled = 
						loadTiledMap(directory + TiledMapDir + "/" + fullName)
					tiledMaps(fullName) = Some(tiled)
					meshes(name + ".ply") = Some(tiled.toMesh)
					tiled
				}
			}
		} else {
			throw new ResourceLoadException("Tiled Map " + name + " does not exist.")
		}
	}

	def getLevelEntities(name: String): Vector[Entity] = {
		val fullName = name + ".json"
		if (levelEntities.contains(fullName)) {
			val ents = levelEntities(fullName)
			val levelEnts = ents match {
				case Some(s) => s
				case None => {
					val s = loadLevelEntities(directory + LevelEntitiesDir + "/" + fullName)
					levelEntities(fullName) = Some(s)
					s
				}
			}
			levelEnts.seeds.map(seed => {
				getPrefab(seed._1, Vec3(seed._2, -50.0f))
			})
		} else {
			throw new ResourceLoadException("Level Entities " + name + " does not exist.")
		}
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

	private def loadLevelEntities(filePath: String): LevelEntities = {
		LevelEntities(filePath)
	}

	private def loadSpriteSheet(filePath: String): SpriteSheet = {
		SpriteSheet(filePath)
	}

	private def loadAnimation(filePath: String): Animation = {
		Animation(filePath)
	}

	private def loadTiledMap(filePath: String): TiledMap = {
		TiledMap(filePath)
	}

	private def loadShaderProgram() = {}

}