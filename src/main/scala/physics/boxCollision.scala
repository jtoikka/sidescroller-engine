package physics

import math.Vec2

case class BoxCollision(
	origin: Vec2, 
	width: Int, 
	height: Int) extends CollisionShape {

	val halfWidth = width/2.0f
	val halfHeight = height/2.0f
	
	def intersection(offset: Vec2, point: Vec2): Vec2 = {
		val diff = (offset + origin - point)
		val dx = diff.x.abs - width
		val dy = diff.y.abs - height
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