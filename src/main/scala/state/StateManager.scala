package state

object StateManager {
	val stateMachines = Map(
		"player" -> new PlayerStateMachine()
	)
}