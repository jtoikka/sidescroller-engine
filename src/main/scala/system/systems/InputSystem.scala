package system

import event.Event
import entity.Entity
import entity.Component._
import entity.InputComponent
import scene.Scene
import input._
import math.Vec2

class InputSystem extends System(bitMask(InputComp)) {
	var pressedKeys = Vector[Int]()
	var heldKeys = Vector[Int]()
	var releasedKeys = Vector[Int]()
	var mousePressed = Vector[Int]()
	var mouseHeld = Vector[Int]()
	var mouseReleased = Vector[Int]()
	var cursor = Vec2(0, 0)


	// The values need to be moved elsewhere
	val inputReceivers = Map(
		"pan" -> new PanInput(32.0f),
		"playerGround" -> new PlayerInputGround(40.0f * 4.8f * 2),
		"playerAir" -> new PlayerInputAir(40.0f * 8.5f * 2),
		"cursor" -> new CursorInput(),
		"button" -> new ButtonInput()
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
				val mousePressedChanges = receiver.mousePressedCallbacks.filter(pair => {
					mousePressed.contains(pair._1)
				}).map(pair => {
					pair._2(delta, entity)
				})
				val mouseHeldChanges = receiver.mouseHeldCallbacks.filter(pair =>
					mouseHeld.contains(pair._1)
				).map(pair => {
					pair._2(delta, entity)
				})
				val mouseReleasedChanges = receiver.mouseReleasedCallbacks.filter(pair =>
					mouseReleased.contains(pair._1)
				).map(pair => {
					pair._2(delta, entity)
				})
				val cursorChanges = receiver.cursorCallbacks.map(input => 
					input(cursor, entity)
				)
				Changes(entity, 
					(pressedChanges ++ 
					 heldChanges ++ 
					 releasedChanges ++ 
					 mousePressedChanges ++
					 mouseHeldChanges ++
					 mouseReleasedChanges ++
					 cursorChanges).toVector, Vector())
			}
			case _ => Changes(entity, Vector(), Vector())
		}
	}
}