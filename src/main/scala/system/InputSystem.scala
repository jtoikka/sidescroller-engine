package system

import event.Event
import entity.Entity
import entity.Component._
import entity.InputComponent
import scene.Scene
import input._

class InputSystem extends System(bitMask(InputComp)) {
	var pressedKeys = Vector[Int]()
	var heldKeys = Vector[Int]()
	var releasedKeys = Vector[Int]()

	val inputReceivers = Map(
		"pan" -> new PanInput(32.0f),
		"player" -> new PlayerInputGround(10.0f)
	)

	def applyTo(
			entity: Entity,
			scene: Scene,
			delta: Float): Changes = {
		entity(InputComp) match {
			case Some(InputComponent(id)) => {
				val receiver = inputReceivers(id)
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