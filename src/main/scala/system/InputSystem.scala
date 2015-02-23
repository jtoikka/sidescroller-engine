package system

import event.Event
import entity.Entity
import entity.Component._
import entity.InputComponent
import scene.Scene

class InputSystem extends System(bitMask(InputComp)) {
	var pressedKeys = Vector[Int]()
	var heldKeys = Vector[Int]()
	var releasedKeys = Vector[Int]()

	def applyTo(
			entity: Entity,
			scene: Scene,
			delta: Float): Changes = {
		entity(InputComp) match {
			case Some(InputComponent(receiver)) => {
				val pressedChanges = receiver.keyPressedCallbacks.filter(pair => {
					pressedKeys.contains(pair._1)
				}).map(pair => {
					pair._2(delta)
				})
				val heldChanges = receiver.keyHeldCallbacks.filter(pair =>
					heldKeys.contains(pair._1)
				).map(pair => {
					pair._2(delta)
				})
				val releasedChanges = receiver.keyReleasedCallbacks.filter(pair =>
					releasedKeys.contains(pair._1)
				).map(pair => {
					pair._2(delta)
				})
				Changes(entity, (pressedChanges ++ heldChanges ++ releasedChanges).toVector, Vector())
			}
			case _ => Changes(entity, Vector(), Vector())
		}
	}
}