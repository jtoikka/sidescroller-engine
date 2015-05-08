package state

/**
  * Manages state machines. Allows a state machine to be created from a String
  * tag. 
  */
object StateManager {
	val stateMachines = Map(
		"player" -> new PlayerStateMachine()
	)
}