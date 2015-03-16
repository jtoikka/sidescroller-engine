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
		"playerGround" -> new PlayerInputGround(40.0f * 4.8f),
		"playerAir" -> new PlayerInputAir(40.0f * 2.5f)
	)

	def instantiate(scene: Scene) = {}

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
					pair._2(delta, entity)
				})
				val heldChanges = receiver.keyHeldCallbacks.filter(pair =>
					heldKeys.contains(pair._1)
				).map(pair => {
					pair._2(delta, entity)
				})
				val releasedChanges = receiver.keyReleasedCallbacks.filter(pair =>
					releasedKeys.contains(pair._1)
				).map(pair => {
					pair._2(delta, entity)
				})
				Changes(entity, (pressedChanges ++ heldChanges ++ releasedChanges).toVector, Vector())
			}
			case _ => Changes(entity, Vector(), Vector())
		}
	}
}