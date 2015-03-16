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
			(e.flags("movingLeft")) ||
			(e.flags("movingRight"))
		}}, onGroundMoving
	)

	lazy val inAirTrigger = new Trigger(
		{_.triggers("inAir")}, 
		inAir
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
		{e => !e.flags("movingLeft") && !e.flags("movingRight")}, onGround
	)
/*----------------------------------------------------------------------------*/

	lazy val onGround: State = State(
		triggers = Vector(
			onGroundMovingTrigger,
			inAirTrigger
		), 
		changes = Vector(
			SetInput("playerGround"),
			SetFrictionMultiplier(1)
		),
		anyState
	)

	lazy val onGroundMoving: State = State(
		triggers = Vector(
			inAirTrigger,
			onGroundTurningTrigger,
			stationaryTrigger
		),
		changes = Vector(
			SetFrictionMultiplier(0)
		),
		anyState
	)

	lazy val onGroundTurning: State = State(
		triggers = Vector(
			inAirTrigger,
			doneTurningTrigger,
			stationaryTrigger
		),
		Vector(SetFrictionMultiplier(1.5f)),
		anyState
	)

	lazy val inAir: State = State(
		triggers = Vector(
			onGroundTrigger
		), 
		Vector(
			SetInput("playerAir")
		),
		anyState
	)

	def getDefault(): State = inAir
}