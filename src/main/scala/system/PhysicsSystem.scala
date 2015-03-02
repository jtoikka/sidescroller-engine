package system

import entity._
import scene.Scene
import entity.Component._
import math._
import math.VectorMath._
import physics.CollisionTest
import event.Event

class PhysicsSystem(globalForces: Vector[Vec3]) 
	extends System(bitMask(PhysicsComp)) {

	def calcDistanceTravelled(
		v: Vec3, a: Vec3, t: Float, 
		lockX: Boolean = false, lockY: Boolean = false): Vec3 = {
		val d = v * t + (globalForces.reduce(_ + _) + a) * t * t
		if (lockX && lockY) Vec3(0, 0, 0)
 		else if (lockX) Vec3(0, d.y, 0)
		else if (lockY) Vec3(d.x, 0, 0)
		else d
	}

	//TODO: Does not yet return events
	def checkCollision(
		a: Entity, b: Entity, 
		time: Float,
		lockX: Boolean = false,
		lockY: Boolean = false): (Vec2, Vector[Event]) = {
		var greatestIntersection = Vec2(0, 0)
		(a(PhysicsComp), b(PhysicsComp), a(CollisionComp), b(CollisionComp)) match {
			case (Some(physA), Some(physB), Some(colA), Some(colB)) => {
				val newPosA = if (!physA.static) {
					a.position + calcDistanceTravelled(
						physA.velocity, physA.acceleration, time, lockX, lockY)
				} else {
					a.position
				}
				val newPosB = if (!physB.static) {
					b.position + calcDistanceTravelled(
						physB.velocity, physB.acceleration, time)
				} else {
					b.position
				}

				colA.hurtBoxes.foreach(hBox => {
					colB.rigidBoxes.foreach(rBox => {
						val intersect = CollisionTest(newPosA, hBox, newPosB, rBox)
						if (greatestIntersection.lengthSquared < intersect.lengthSquared) {
							greatestIntersection = intersect
						}
					})
				})
			}
			case _ =>
		}
		(greatestIntersection, Vector())
	}

	def findMaxCollision(entity: Entity, scene: Scene, delta: Float): Vec2 = {
		var colIntersect = Vec2(0, 0)
		scene.entities.getInRange(
			entity.position.x - maxCollisionRange/2.0f,
			entity.position.y - maxCollisionRange/2.0f,
			maxCollisionRange, maxCollisionRange).foreach(other => {
				if (other != entity) {
					val (intersection, events) = checkCollision(entity, other, delta)
					if (intersection.lengthSquared != 0) {
						if (colIntersect.lengthSquared < intersection.lengthSquared) {
							colIntersect = intersection
						}
					}
				}
			})
		colIntersect
	}

	val minPermittedCollision = 0.2f

	def findPointOfCollision(
		entity: Entity, 
		scene: Scene, 
		delta: Float): (Float, Vec2) = {
		val max0 = findMaxCollision(entity, scene, 0)
		val maxFull = findMaxCollision(entity, scene, delta)
		if (max0.length > minPermittedCollision) {
			println("Yup")
			(0, max0)
		} else if (maxFull.length == 0) {
			(delta, maxFull)
		} else {
			def findSuitable(i: Int, time: Float): (Float, Vec2) = {
				val maxCollision = findMaxCollision(entity, scene, time)
				if (maxCollision.length > minPermittedCollision) {
					findSuitable(i * 2, time - delta/(i * 2))
				} else if (maxCollision.length < 0) {
					findSuitable(i * 2, time + delta/(i * 2))
				} else {
					println("i: " + i)
					println("time: " + time)
					(time, maxCollision)
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
		entity: Entity, 
		scene: Scene, 
		delta: Float, 
		lockX: Boolean = false, 
		lockY: Boolean = false,
		deltaPosition: Vec3 = Vec3(0, 0, 0),
		deltaVelocity: Vec3 = Vec3(0, 0, 0)): Changes = {
		entity(PhysicsComp) match {
			case Some(PhysicsComponent(velocity, acceleration, mass, bounciness, static)) 
			if (!static && delta > 0 && !(lockX && lockY)) => {
				println(delta)
				entity(CollisionComp) match {
					case Some(CollisionComponent(hurtBoxes, hitBoxes, rigidBoxes, triggers)) => {
						val (pointOfCollision, colIntersect) = findPointOfCollision(entity, scene, delta)
						
						val normal = Vec3(colIntersect, 0.0f).normalize
						val perpendicular = normal.dot(ZAxis)
						val displacement = 
							calcDistanceTravelled(velocity, acceleration, pointOfCollision) + Vec3(colIntersect, 0.0f)
						val changeVelocity = (acceleration + globalForces.reduce(_ + _)) * pointOfCollision

						println("Point of collision: " + pointOfCollision) 

						val deltaVel = 
							if (colIntersect.lengthSquared != 0.0) {
								val reflected = reflection(velocity + changeVelocity, normal)
								val bounce = reflected * normal
								val tangent = reflected * perpendicular
								(bounce * bounciness + tangent - velocity)
							} else 
								changeVelocity

						val newEntity = entity.copy()
						// newEntity.updateComponent(entity)
						Changes(entity, Vector(
								Translation(displacement), 
								Acceleration(deltaVel)), Vector()) ++ 
						applyPhysics(
							entity, 
							scene, 
							delta - pointOfCollision, 
							lockX || normal.x != 0, 
							lockY || normal.y != 0,
							displacement,
							deltaVelocity)
					}
				}
			}
			case _ => {
				Changes(entity, Vector(), Vector())
			}
		}
	}

	def applyTo(
			entity: Entity,
			scene: Scene,
			delta: Float): Changes = {
		applyPhysics(entity, scene, delta)
	}

}