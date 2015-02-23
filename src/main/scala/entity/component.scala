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
	def bitMask(c: Class[_]): Long = {
		c match {
			case SpriteComp => 0x1L
			case SpatialComp => 0x2L
			case ModelComp => 0x4L
			case CameraComp => 0x8L
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

case class StateMachine(
	stateMachine: String,
	state: String) extends Component {}