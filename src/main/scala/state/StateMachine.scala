package state

trait StateMachine {
	def getDefault(): State
}