package input

import system.StateChange
import entity.Entity

case class InputReceiver(
	keyPressedCallbacks: Map[Int, (Float, Entity) => StateChange],
	keyHeldCallbacks: Map[Int, (Float, Entity) => StateChange],
	keyReleasedCallbacks: Map[Int, (Float, Entity) => StateChange]) {}