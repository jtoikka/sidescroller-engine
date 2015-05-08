package physics

import math.Vec2

/**
  * Extends box collision with trigger info [t] parameter.
  */
class TriggerBoxCollision(
	origin: Vec2, 
	width: Int, 
	height: Int,
	t: String) extends BoxCollision(origin, width, height) with Trigger {
	def tag = t
}