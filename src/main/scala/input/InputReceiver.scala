package input

import system.StateChange

case class InputReceiver(
	keyPressedCallbacks: Map[Int, Float => StateChange],
	keyHeldCallbacks: Map[Int, Float => StateChange],
	keyReleasedCallbacks: Map[Int, Float => StateChange]) {}