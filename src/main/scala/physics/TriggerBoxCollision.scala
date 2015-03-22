package physics

import math.Vec2

class TriggerBoxCollision(
	origin: Vec2, 
	width: Int, 
	height: Int,
	t: String) extends BoxCollision(origin, width, height) with Trigger {
	def tag = t
}