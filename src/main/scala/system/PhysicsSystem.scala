package system

import entity._
import scene.Scene
import entity.Component._
import math._
import math.VectorMath._
import physics.CollisionTest
import event.Event
import spatial._

case class PhysicalObject(
	entity: Entity,
	var position: Vec3,
	var velocity: Vec3,
	var acceleration: Vec3,
	mass: Float,
	bounciness: Float,
	collision: CollisionComponent,
	friction: Float,
	static: Boolean
	) extends Spatial {

	def getPosition = position
}

class PhysicsSystem(globalForces: Vector[Vec3]) 
	extends System(bitMask(PhysicsComp)) {

	val globalForce = globalForces.reduce(_ + _)

	var spatialGrid: SpatialGrid2D[PhysicalObject] = new SpatialGrid2D[PhysicalObject](
			1, 1, 1, 1)

	def instantiate(scene: Scene) = {
		sceneToPhysical(scene)
	}
 
	def sceneToPhysical(scene: Scene) = {
		spatialGrid = new SpatialGrid2D[PhysicalObject](
			scene.width, scene.height, scene.columns, scene.rows)
		scene.entities foreach (entity => {
			(entity(PhysicsComp), entity(CollisionComp)) match {
				case (Some(PhysicsComponent(
					velocity, acceleration, mass, bounciness, friction, static)), 
					Some(collision)) => {
					spatialGrid += 
						PhysicalObject(
							entity,
							entity.position, 
							velocity, acceleration, 
							mass, bounciness, collision, friction, static)
				}
				case _ =>
			}
		})
	}

	def calcDistanceTravelled(
		v: Vec3, a: Vec3, t: Float, 
		lockX: Boolean = false, lockY: Boolean = false): Vec3 = {
		val d = v * t + (globalForce + a) * t * t
		if (lockX && lockY) Vec3(0, 0, 0)
 		else if (lockX) Vec3(0, d.y, 0)
		else if (lockY) Vec3(d.x, 0, 0)
		else d
	}

	def checkCollision(
		a: PhysicalObject, 
		b: PhysicalObject,
		time: Float,
		lockX: Boolean = false,
		lockY: Boolean = false): (Vec2, Vector[Event]) = {
		var greatestIntersection = Vec2(0, 0)

		val newPosA = if (!a.static) {
			a.position + calcDistanceTravelled(
				a.velocity, a.acceleration, time, lockX, lockY)
		} else {
			a.position
		}
		val newPosB = if (!b.static) {
			b.position + calcDistanceTravelled(
				b.velocity, b.acceleration, time, lockX, lockY)
		} else {
			b.position
		}

		a.collision.hurtBoxes.foreach(hBox => {
			b.collision.rigidBoxes.foreach(rBox => {
				val intersect = CollisionTest(newPosA, hBox, newPosB, rBox)
				if (greatestIntersection.lengthSquared < intersect.lengthSquared) {
					greatestIntersection = intersect
				}
			})
		})
		(greatestIntersection, Vector())
	}

	def findMaxCollision(
		a: PhysicalObject, delta: Float,
		lockX: Boolean = false,
		lockY: Boolean = false): (Vec2, Option[PhysicalObject]) = {
		var colIntersect = Vec2(0, 0)
		var collider: Option[PhysicalObject] = None
		spatialGrid.getInRange(
			a.position.x - maxCollisionRange/2.0f,
			a.position.y - maxCollisionRange/2.0f,
			maxCollisionRange, maxCollisionRange).foreach(other => {
				if (other.entity != a.entity) {
					val (intersection, events) =
						checkCollision(a, other, delta, lockX, lockY)

					if (intersection.lengthSquared != 0 && 
						  colIntersect.lengthSquared < intersection.lengthSquared) {
						colIntersect = intersection
						collider = Some(other)
					}
				}
		})
		(colIntersect, collider)
	}

	val minPermittedCollision = 0.1f

	def findPointOfCollision(
		a: PhysicalObject, delta: Float,
		lockX: Boolean = false,
		lockY: Boolean = false): (Float, Vec2, Option[PhysicalObject]) = {
		val (max0, col0) = findMaxCollision(a, 0, lockX, lockY)
		val (maxFull, colFull) = findMaxCollision(a, delta, lockX, lockY)
		if (max0.length > minPermittedCollision) {
			(0, max0, col0)
		} else if (maxFull.length == 0) {
			(delta, maxFull, colFull)
		} else {
			def findSuitable(i: Int, time: Float): (Float, Vec2, Option[PhysicalObject]) = {
				val (maxCollision, col) = findMaxCollision(a, time, lockX, lockY)
				if (maxCollision.length > minPermittedCollision) {
					findSuitable(i * 2, time - delta/(i * 2))
				} else if (maxCollision.length < 0) {
					findSuitable(i * 2, time + delta/(i * 2))
				} else {
					(time, maxCollision, col)
				}
			}
			findSuitable(1, delta)
		}
	}

	val maxCollisionRange = 60.0f

	def reflection(v: Vec3, n: Vec3): Vec3 = {
		v - (2 * (v.dot(n)) * n) 
	}

	val ZAxis = Vec3(0, 0, 1)

	def applyPhysics(
		physObj: PhysicalObject, 
		scene: Scene, 
		delta: Float, 
		lockX: Boolean = false,
		lockY: Boolean = false): Unit = {
		if (!physObj.static && delta > 0 && !(lockX && lockY)) {
			val (pointOfCollision, colIntersect, collider) = 
				findPointOfCollision(physObj, delta, lockX, lockY)
			val normal = Vec3(colIntersect, 0).normalize
			val displacement =
				calcDistanceTravelled(
					physObj.velocity, physObj.acceleration, 
					pointOfCollision, lockX, lockY) + Vec3(colIntersect, 0)
			val changeVelocity = 
				(physObj.acceleration + globalForce) * pointOfCollision

			val deltaVel = 
				if (colIntersect.lengthSquared != 0.0) {
					val v = physObj.velocity + changeVelocity
					val reflected = reflection(v, normal)
					val perpendicular = normal.cross(ZAxis)

					val colFriction = collider match {
						case Some(col) => col.friction
						case _ => 0
					}

					val bounce = reflected * normal.abs
					val tangent = reflected * perpendicular.abs
					val normalTangent = 
						if (tangent.lengthSquared != 0) tangent.normalize else Vec3(0, 0, 0)

					val frictionAccel = 
						(colFriction * physObj.friction) * 
						(globalForce + physObj.acceleration).dot(normal) * normalTangent

					var frictionVel = frictionAccel * pointOfCollision

					// if ((tangent + frictionVel).length <= 0) {
					// 	frictionVel = tangent.neg
					// }
					// println("overall friction: " + colFriction * physObj.friction)
					// println("normal tangent: " + tangent)
					// println("frictionVel: " + frictionVel)
					// val frictionlessVel = bounce * physObj.bounciness + tangent
					if ((tangent + frictionVel).normalize == tangent.normalize) {
						bounce * physObj.bounciness + tangent - physObj.velocity + frictionVel
					} else {
						bounce * physObj.bounciness + physObj.velocity.neg
					}

					// val withFriction = frictionlessVel + frictionVel

					// if (withFriction.normalize == frictionlessVel.normalize) {
					// 	withFriction
					// } else {
					// 	Vec3(0, 0, 0)
					// }
				} else 
					changeVelocity

			physObj.position += displacement
			physObj.velocity += deltaVel

			applyPhysics(
				physObj, 
				scene, 
				delta - pointOfCollision, 
				lockX || normal.x != 0, 
				lockY || normal.y != 0)
		}
	}

	def applyTo(
			entity: Entity,
			scene: Scene,
			delta: Float): Changes = {
		(entity(PhysicsComp), entity(CollisionComp)) match {
			case (Some(
				PhysicsComponent(velocity, acceleration, mass, bounciness, friction, static)), 
				Some(collision)) => {
				val physicalObj = 
					PhysicalObject(
						entity, entity.position, velocity, acceleration,
					  mass, bounciness, collision, friction, static)

				applyPhysics(physicalObj, scene, delta)
				Changes(entity, Vector(
					Translation(physicalObj.position - entity.position), 
					Acceleration(physicalObj.velocity - velocity)),
					Vector())
			}
			case _ => Changes(entity, Vector(), Vector())
		}
	}

}