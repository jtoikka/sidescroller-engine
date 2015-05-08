package physics

import math.Vec2

/**
  * Basic axis-aligned bounding box
  */ 
class BoxCollision(
	val origin: Vec2, 
	val width: Float, 
	val height: Float) extends CollisionShape {

	val halfWidth = width/2.0f
	val halfHeight = height/2.0f

/**
  * Check intersection by separation of axes.
  */
	def intersection(offset: Vec2, point: Vec2): Vec2 = {
		val diff = (offset + origin - point)
		val dx = diff.x.abs - width // difference x
		val dy = diff.y.abs - height // difference y
		if (dx < 0 && dy < 0) {
			if (dx > dy) {
				Vec2(dx * diff.x.signum, 0)
			} else {
				Vec2(0, dy * diff.y.signum)
			}
		} else {
			Vec2(0, 0)
		}
	}
}