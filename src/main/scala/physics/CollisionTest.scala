package physics

import math._

object CollisionTest {

	def apply(
		posA: Vec3, a: CollisionShape, 
		posB: Vec3, b: CollisionShape): Vec2 = {
		(a, b) match {
			case (boxA: BoxCollision, boxB: BoxCollision) => {
				boxBox(posA, boxA, posB, boxB)
			}
			case _ => throw new Exception("Missing collision check")
		}
	}
	
	def boxBox(posA: Vec3, a: BoxCollision, posB: Vec3, b: BoxCollision): Vec2 = {
		val aOrig = posA.xy + a.origin
		val bOrig = posB.xy + b.origin

		val dif = bOrig - aOrig
		val intersectX = dif.x.abs - (a.width/2.0f + b.width/2.0f)
		val intersectY = dif.y.abs - (a.height/2.0f + b.height/2.0f)
		if (intersectX < 0 && intersectY < 0) {
			if (intersectX.abs < intersectY.abs) {
				Vec2(intersectX * dif.x.signum, 0)
			} else {
				Vec2(0, intersectY * dif.y.signum)
			}
		} else {
			Vec2(0, 0)
		}
	}
}