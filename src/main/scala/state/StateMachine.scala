package state

/**
  * State machine trait, determines what the initial state of an entity is.
  */
trait StateMachine {
	def getDefault(): State
}