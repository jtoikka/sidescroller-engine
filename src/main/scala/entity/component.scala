package entity

import math.Vec3
import math.Quaternion

import physics.CollisionShape
import behaviour.Behaviour
import input.InputReceiver
import state.State

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
			case StateComp => 1L << 9
			case _ => -1
		}
	}
}

class Component() {
}

case class SpatialComponent(
	rotation: Quaternion,
	scale: Vec3) extends Component {}

case class SpriteComponent(
	sprite: String,
	spriteSheet: String,
	layer: Int) extends Component {}

case class ModelComponent(
	mesh: String,
	texture: String) extends Component {}

case class CameraComponent(
	zNear: Float, zFar: Float,
	width: Float, height: Float,
	orthographic: Boolean) extends Component {}

case class CollisionComponent(
	collisionBoxes: Vector[CollisionShape],
	rigidBoxes: Vector[CollisionShape],
	triggers: Vector[CollisionShape],
	oneWay: Boolean) extends Component {}

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

case class AnimationComponent(
	animation: String,
	timer: Double) extends Component {}

case class InputComponent(
	inputReceiver: String) extends Component {}

case class BehaviourComponent(
	behaviours: Vector[Behaviour]) extends Component {}

case class StateComponent(
	state: State) extends Component {}