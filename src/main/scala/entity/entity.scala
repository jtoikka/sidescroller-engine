package entity

import spatial.Spatial
import scala.reflect.ClassTag
import math.Vec3
import Component._
import entity._
import behaviour.BehaviourManager
import event._

/**
  * Entities represent all objects within the game, whether visible or not. 
  * Entities are a collection of components which determine how they appear and
  * act. 
  * 
  * @param components The components that the entity is composed of.
  * @param children Child entities. Certain changes to the parent entity are
  *                 applied to the children (such as translations)
  * @param position The position of the entity in 3D space.
  * @param tag A tag describing the role of the entity.
  */

case class Entity(
	components: Vector[Component], 
	children: Vector[Entity],
	var position: Vec3,
	tag: String = "") extends Spatial with Listener {

/**
  * Flags, triggers and timers are helpful additions for working with behaviours
  * and inputs. They eliminate the need to create new components for minor
  * storage of entity state. These should only be used by the entity's own
  * updates: another entity should avoid accessing these parameters, as they
  * are not atomic (can be modified at any point during an update).
  *
  * Flags retain their state, and can be set to true or false. Triggers can be
  * set to true, but are reverted to false at the end of an update. See
  * the [Timer] class for more information of timers.
  */
	val flags = 
		collection.mutable.Map[String, Boolean]().withDefaultValue(false)

	val triggers = 
		collection.mutable.Map[String, Boolean]().withDefaultValue(false)

	val timers =
		collection.mutable.Map[String, Timer]()

// Sort components by their bitmask, for faster access.
	private val componentMap = scala.collection.mutable.HashMap(
		(components map (c => (bitMask(c.getClass()), c)): _*))

// The collective bitkey of the entity, formed by combining the bitmasks of all
// its components.
	private val bitkey: Long = componentMap.keys.foldLeft(0L) {
		(a, b) => a ^ b
	}

	def key = bitkey

/**
  * Gets the desired component from this entity.
  * 
  * @param c The class of the component to get.
  * 
  * @return The component if found, None otherwise.
  */ 
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

/**
  * Called at the end of an update, clears any remaining events.
  */
	def handleEvents(): Unit = {
		events.clear()
	}

	def getPosition = position

/**
  * Utility function, allows access to components via a shorter notation:
  *     entity(SpatialComp)
  * as opposed to:
  *     entity.getComponent(SpatialComp)
  */
	def apply[T <: Component : ClassTag](c: Class[T]) = getComponent(c)

/**
	* Checks if the entity contains a component.
	*/
	def contains[T <: Component](c: Class[T]): Boolean = {
		(bitMask(c) & bitkey) != 0
	}

/**
  * Creates a copy of this entity at the given position [pos].
  */
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
				case comp: ModelComponent => comp.copy()
				case comp: DamageComponent => comp.copy()
				case comp: HealthComponent => comp.copy()
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

/**
  * Sets the destroyed flag to true. A destroyed entity is removed from the 
  * scene it is contained in at the end of an update.
  */
	def destroy() = {
		destroyed = true
	}

/**
  * Replaces a component with a new component [c].
  */
	def updateComponent[T <: Component](c: T) = {
		componentMap(bitMask(c.getClass)) = c
	}
}