package entity

import spatial.Spatial
import scala.reflect.ClassTag
import math.Vec3
import Component._
import entity._
import behaviour.BehaviourManager
import event._

case class Entity(
	components: Vector[Component], 
	children: Vector[Entity],
	var position: Vec3,
	tag: String = "") extends Spatial with Listener {

	val flags = 
		collection.mutable.Map[String, Boolean]().withDefaultValue(false)

	val triggers = 
		collection.mutable.Map[String, Boolean]().withDefaultValue(false)

	val timers =
		collection.mutable.Map[String, Timer]()

	private val componentMap = scala.collection.mutable.HashMap(
		(components map (c => (bitMask(c.getClass()), c)): _*))

	private val bitkey: Long = componentMap.keys.foldLeft(0L) {
		(a, b) => a ^ b
	}

	def key = bitkey

	def getComponent[T <: Component : ClassTag](c: Class[T]): Option[T] = {
		if (contains(c)) {
			componentMap(bitMask(c)) match {
				case comp : T => Some(comp)
				case _ => None
			}
		} else {
			None
		}
	}

	def handleEvents(): Unit = {
		events.clear()
	}

	def getPosition = position

	def apply[T <: Component : ClassTag](c: Class[T]) = getComponent(c)

	def contains[T <: Component](c: Class[T]): Boolean = {
		(bitMask(c) & bitkey) != 0
	}

	def createCopy(pos: Vec3 = position) = {
		val newComponents = componentMap.values.map(c => {
			c match {
				case comp: SpriteComponent => comp.copy()
				case comp: SpatialComponent => comp.copy()
				case comp: CameraComponent => comp.copy()
				case comp: CollisionComponent => comp.copy()
				case comp: PhysicsComponent => comp.copy()
				case comp: AnimationComponent => comp.copy()
				case comp: InputComponent => comp.copy()
				case comp: BehaviourComponent => {
					BehaviourComponent(comp.behaviours.map(b => {
						BehaviourManager.copy(b)
					}))
				}
				case comp: StateComponent => comp.copy()
			}
		}).toVector
		Entity(newComponents, children, pos, tag)
	}

	private var destroyed = false

	def isDestroyed = destroyed

	def destroy() = {
		destroyed = true
	}

	def updateComponent[T <: Component](c: T) = {
		componentMap(bitMask(c.getClass)) = c
	}
}