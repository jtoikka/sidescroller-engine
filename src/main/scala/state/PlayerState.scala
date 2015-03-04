package state

class PlayerState {
	val jumpTrigger = Trigger({
		entity => true
	},
	inAir)
	val onGround = State(Vector(jumpTrigger), Vector())
	val inAir = State(Vector(), Vector())
}