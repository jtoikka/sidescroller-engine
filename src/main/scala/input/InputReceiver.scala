package input

import system.StateChange
import entity.Entity
import math._

/**
  * A base input receiver. Provided callbacks give functionality to entities.
  */
case class InputReceiver(
	keyPressedCallbacks: Map[Int, (Float, Entity) => StateChange] = Map(),
	keyHeldCallbacks: Map[Int, (Float, Entity) => StateChange] = Map(),
	keyReleasedCallbacks: Map[Int, (Float, Entity) => StateChange] = Map(),
	mousePressedCallbacks: Map[Int, (Float, Entity) => StateChange] = Map(),
	mouseHeldCallbacks: Map[Int, (Float, Entity) => StateChange] = Map(),
	mouseReleasedCallbacks: Map[Int, (Float, Entity) => StateChange] = Map(),
	cursorCallbacks: Vector[(Vec2, Entity) => StateChange] = Vector()) {}