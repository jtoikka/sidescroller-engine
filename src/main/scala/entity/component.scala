package entity

import math.Vec3
import math.Quaternion

import physics.CollisionShape
import behaviour.Behaviour
import input.InputReceiver
import state.State

/** 
	* The component object contains utility functions to make code
	* more readable, e.g. entity(SpriteComp) can be called instead
	* of entity(classOf[SpriteComponent]).
	*/
object Component {
	implicit val SpriteComp = classOf[SpriteComponent]
	implicit val SpatialComp = classOf[SpatialComponent]
	implicit val ModelComp = classOf[ModelComponent]
	implicit val CameraComp = classOf[CameraComponent]
	implicit val CollisionComp = classOf[CollisionComponent]
	implicit val PhysicsComp = classOf[PhysicsComponent]
	implicit val AnimationComp = classOf[AnimationComponent]
	implicit val InputComp = classOf[InputComponent]
	implicit val BehaviourComp = classOf[BehaviourComponent]
	implicit val StateComp = classOf[StateComponent]

/**
  * Returns a bitmask for the given class [c]. The bitmasks allow for fast
  * checks if an entity contains a component.
  *
  * @param c Component subclass
  * 
  * @return bitmask
  */ 
	def bitMask(c: Class[_]): Long = {
		c match {
			case SpriteComp       => 1L << 0
			case SpatialComp      => 1L << 1
			case ModelComp        => 1L << 2
			case CameraComp       => 1L << 3
			case CollisionComp    => 1L << 4
			case PhysicsComp      => 1L << 5
			case AnimationComp    => 1L << 6
			case InputComp        => 1L << 7
			case BehaviourComp    => 1L << 8
			case StateComp        => 1L << 9
			case _ => -1
		}
	}
}

/**
  * Every entity consists of a number of components, which determine its
  * features. 
  */
class Component() {
}

/**
  * Gives an entity rotation and scale parameters.
  */
case class SpatialComponent(
	rotation: Quaternion,
	scale: Vec3) extends Component {}

/**
  * A visual component, renders a sprite at the entity's location.
  */
case class SpriteComponent(
	sprite: String,
	spriteSheet: String,
	layer: Int) extends Component {}

/**
  * A visual component, renders a model at the entity's location.
  */
case class ModelComponent(
	mesh: String,
	texture: String) extends Component {}

/**
  * A container for camera parameters. zNear and zFar determine the clipping
  * planes, the width and height determine how zoomed in the camera is, and
  * the orthographic parameter informs whether othographic or perspective
  * rendering should be used.
  */
case class CameraComponent(
	zNear: Float, zFar: Float,
	width: Float, height: Float,
	orthographic: Boolean) extends Component {}

/**
  * Allows an entity to collide and interact with its environment. Collision
  * boxes collide with rigid boxes, rigid boxes do not collide, and triggers
  * collide with collision/rigidboxes as well as other triggers. The
  * functionality of the triggers should be realized through behaviours.
  */ 
case class CollisionComponent(
	collisionBoxes: Vector[CollisionShape],
	rigidBoxes: Vector[CollisionShape],
	triggers: Vector[CollisionShape],
	oneWay: Boolean) extends Component {}

/**
  * Gives an object physical properties, and enables forces to work on it. The
  * frictionMultiplier parameter modifies friction in instances such as when
  * the entity becomes slippery.
  */
case class PhysicsComponent(
	velocity: Vec3,
	acceleration: Vec3,
	mass: Float,
	bounciness: Float,
	friction: Float,
	frictionMultiplier: Float,
	maxHorizontal: Float,
	maxVertical: Float,
	static: Boolean) extends Component {}

/**
  * A visual component, works with an entity's SpriteComponent to create a 
  * moving animation. 
  */
case class AnimationComponent(
	animationSheet: String,
	animation: String,
	timer: Double) extends Component {}

/**
  * Allows an entity to be modified by user-inputs.
  */
case class InputComponent(
	inputReceiver: String) extends Component {}

/**
  * Allows script-esque behaviours to be attached to entities. 
  */
case class BehaviourComponent(
	behaviours: Vector[Behaviour]) extends Component {}

/**
  * Allows for an entity to work with a statemachine, to facilitate enabling
  * rules when the entity enters different states.
  */
case class StateComponent(
	state: State) extends Component {}