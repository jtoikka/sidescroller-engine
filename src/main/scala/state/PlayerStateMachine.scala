package state

import system._
import entity.Component._


class PlayerStateMachine extends StateMachine {
	val anyState = AnyState(
		Vector()
	)

/*- Triggers -----------------------------------------------------------------*/
	lazy val onGroundMovingTrigger = new Trigger(
		{e => {
			e(PhysicsComp) match {
				case Some(physicsComp) => {
					(e.flags("movingLeft") && physicsComp.velocity.x < 0) ||
					(e.flags("movingRight") && physicsComp.velocity.x > 0)
				}
				case _ => false
			}
		}}, onGroundMoving
	)

	lazy val jumpingTrigger = new Trigger(
		{e => {
			e(PhysicsComp) match {
				case Some(physicsComp) => {
					(physicsComp.velocity.y > 0) && e.triggers("inAir")
				}
				case _ => false
			}
		}}, jumping
	)

	lazy val fallingTrigger = new Trigger(
		{e => {
			e(PhysicsComp) match {
				case Some(physicsComp) => {
					(physicsComp.velocity.y <= 0) && e.triggers("inAir")
				}
				case _ => false
			}
		}}, falling
	)

	lazy val onGroundTrigger = new Trigger(
		{_.triggers("onGround")},
		onGround
	)

	lazy val onGroundTurningTrigger = new Trigger(
		{e => {
			e(PhysicsComp) match {
				case Some(physicsComp) => {
					(e.flags("movingLeft") && physicsComp.velocity.x > 0) ||
					(e.flags("movingRight") && physicsComp.velocity.x < 0)
				}
				case _ => false
			}
		}}, onGroundTurning
	)

	lazy val doneTurningTrigger = new Trigger(
		{e => {
			e(PhysicsComp) match {
				case Some(physicsComp) => {
					(e.flags("movingLeft") && physicsComp.velocity.x <= 0) ||
					(e.flags("movingRight") && physicsComp.velocity.x >= 0)
				}
				case _ => false
			}
		}}, onGroundMoving
	)

	lazy val stationaryTrigger = new Trigger(
		{e => !e.flags("movingLeft") && !e.flags("movingRight") || e.flags("movingLeft") && e.flags("movingRight")}, onGround
	)
/*----------------------------------------------------------------------------*/

	lazy val onGround: State = State(
		triggers = Vector(
			onGroundMovingTrigger,
			jumpingTrigger,
			fallingTrigger
		), 
		changes = Vector(
			SetInput("playerGround"),
			SetFrictionMultiplier(1),
			SetAnimation("stand_right", "stand_left")
		),
		anyState
	)

	lazy val onGroundMoving: State = State(
		triggers = Vector(
			jumpingTrigger,
			fallingTrigger,
			onGroundTurningTrigger,
			stationaryTrigger
		),
		changes = Vector(
			SetFrictionMultiplier(0),
			SetAnimation("walk_right", "walk_left")
		),
		anyState
	)

	lazy val onGroundTurning: State = State(
		triggers = Vector(
			jumpingTrigger,
			fallingTrigger,
			doneTurningTrigger,
			stationaryTrigger
		),
		Vector(
			SetFrictionMultiplier(1.5f),
			SetAnimation("stand_right", "stand_left")
		),
		anyState
	)

	lazy val falling: State = State(
		triggers = Vector(
			onGroundTrigger
		), 
		Vector(
			SetInput("playerAir"),
			SetAnimation("fall_right", "fall_left")
		),
		anyState
	)

	lazy val jumping: State = State(
		triggers = Vector(
			fallingTrigger,
			onGroundTrigger
		),
		changes = Vector(
			SetInput("playerAir"),
			SetAnimation("jump_right", "jump_left")
		),
		anyState
	)

	def getDefault(): State = falling
}