package entity

import math.Vec3
import math.Quaternion

import physics.CollisionShape
import behaviour.Behaviour
import input.InputReceiver

object Component {
	val SpriteComp = classOf[SpriteComponent]
	val SpatialComp = classOf[SpatialComponent]
	val ModelComp = classOf[ModelComponent]
	val CameraComp = classOf[CameraComponent]
	val CollisionComp = classOf[CollisionComponent]
	val PhysicsComp = classOf[PhysicsComponent]
	val AnimationComp = classOf[AnimationComponent]
	val InputComp = classOf[InputComponent]
	val BehaviourComp = classOf[BehaviourComponent]
	val StateMachineComp = classOf[StateMachineComponent]
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
			case StateMachineComp => 1L << 9
			case _ => -1
		}
	}
}

class Component() {}

case class SpatialComponent(
	rotation: Quaternion,
	scale: Vec3) extends Component {}

case class SpriteComponent(
	sprite: String,
	spriteSheet: String) extends Component {}

case class ModelComponent(
	mesh: String,
	texture: String) extends Component {}

case class CameraComponent(
	zNear: Float, zFar: Float,
	width: Float, height: Float,
	orthographic: Boolean) extends Component {}

case class CollisionComponent(
	hurtboxes: Vector[CollisionShape],
	hitBoxes: Vector[CollisionShape],
	triggers: Vector[CollisionShape]) extends Component {}

case class PhysicsComponent(
	velocity: Vec3,
	acceleration: Vec3,
	mass: Float,
	static: Boolean) extends Component {}

case class AnimationComponent(
	animation: String,
	timer: Double) extends Component {}

case class InputComponent(
	inputReceiver: InputReceiver) extends Component {}

case class BehaviourComponent(
	behaviours: Vector[Behaviour]) extends Component {}

case class StateMachineComponent(
	stateMachine: String,
	state: String) extends Component {}