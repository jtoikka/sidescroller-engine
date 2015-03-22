package physics

import math.Vec2

abstract class CollisionShape {
	def intersection(offset: Vec2, point: Vec2): Vec2 
}

trait Trigger {
	def tag: String
}