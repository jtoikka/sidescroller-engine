package system

import entity._
import scene.Scene
import entity.Component._
import math._
import math.VectorMath._
import physics._
import event.Event
import spatial._
import scala.collection.mutable.ArrayBuffer
import event._

/**
  * Entities are converted to physical objects, for handling internally in the
  * physics system. 
  */
case class PhysicalObject(
	entity: Entity,
	var position: Vec3,
	var velocity: Vec3,
	var acceleration: Vec3,
	mass: Float,
	bounciness: Float,
	collision: CollisionComponent,
	friction: Float,
	static: Boolean,
	maxHorizontal: Float,
	maxVertical: Float
	) extends Spatial {

	def getPosition = position
}

/**
  * Applies forces to entities, checks for collisions, and resolves an entity's
  * physical properties. 
  */
class PhysicsSystem(globalForces: Vector[Vec3]) 
	extends System(bitMask(PhysicsComp)) {

	// Reduce global forces into a single 3D vector.
	val globalForce = globalForces.reduce(_ + _)

	// Place physical objects in a spatial grid for faster collision detection
	var spatialGrid: SpatialGrid2D[PhysicalObject] = 
		new SpatialGrid2D[PhysicalObject](1, 1, 1, 1)

	def instantiate(scene: Scene) = {
		sceneToPhysical(scene)
	}
 
/**
  * Convert the scene's entities to physical objects.
  */
	def sceneToPhysical(scene: Scene) = {
		spatialGrid = new SpatialGrid2D[PhysicalObject](
			scene.width, scene.height, scene.columns, scene.rows)
		scene.entities foreach (entity => {
			(entity(PhysicsComp), entity(CollisionComp)) match {
				case (Some(PhysicsComponent(
					velocity, acceleration, mass, bounciness, 
					friction, frictionMultiplier,
					maxHorizontal, maxVertical, static)), 
					Some(collision)) => {
					spatialGrid += 
						PhysicalObject(
							entity,
							entity.position, 
							velocity, acceleration, 
							mass, bounciness, collision, friction, static,
							maxHorizontal, maxVertical)
				}
				case _ =>
			}
		})
	}

/**
  * Calculate where an object would move, given its initial velocity,
  * acceleration, and the duration of the travel.
  */
	private def calcDistanceTravelled(
		v: Vec3, a: Vec3, t: Float, 
		lockX: Boolean = false, lockY: Boolean = false): Vec3 = {
		val d = v * t + (globalForce + a) * t * t
		if (lockX && lockY) Vec3(0, 0, 0)
 		else if (lockX) Vec3(0, d.y, 0)
		else if (lockY) Vec3(d.x, 0, 0)
		else d
	}

/**
  * Check collisions between collision boxes and rigid boxes.
  */
	def checkRigidCollision(
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

		// Iterate through both objects' boundingboxes
		a.collision.collisionBoxes.foreach(hBox => {
			b.collision.rigidBoxes.foreach(rBox => {
				// Special case for one-way collisions
				if (b.collision.oneWay) {
					// Collide only if moving downward
					if (a.velocity.y <= 0) {
						val startCollision = CollisionTest(a.position, hBox, newPosB, rBox)
						// Collide only if object started outside of object (with a small amount of allowed error)
						if (startCollision.lengthSquared < 0.2f) {
							val intersect = CollisionTest(newPosA, hBox, newPosB, rBox)
							if (intersect.y > 0) {
								// Look for smallest intersection
								if (greatestIntersection.lengthSquared < intersect.lengthSquared) {
									greatestIntersection = intersect
								}
							}
						}
					}
				} else {
					// Look for smallest intersection
					val intersect = CollisionTest(newPosA, hBox, newPosB, rBox)
					if (greatestIntersection.lengthSquared < intersect.lengthSquared) {
						greatestIntersection = intersect
					}
				}
			})
		})
		(greatestIntersection, Vector())
	}

/**
  * Check collisions between triggers and rigid boxes.
  */
	private def checkTriggers(p: PhysicalObject, scene: Scene, delta: Float): Unit = {
		spatialGrid.getSurrounding(
			p.position.x,
			p.position.y).foreach(other => {
			if (other.entity != p.entity) {
				p.collision.triggers.foreach(tBox => {
					other.collision.rigidBoxes.foreach(rBox => {
						val intersect = CollisionTest(p.position, tBox, other.position, rBox)
						def addTriggerEvent(tBox: CollisionShape) = {
							tBox match {
								case t: Trigger => {
									p.entity.privateEvents += TriggerEvent(true, t.tag, other.entity)
								}
								case _ => throw new Exception("Invalid trigger shape")
							}
						}
						// If a collision occurs, create a TriggerEvent
						if (intersect.lengthSquared != 0) {
							if (other.collision.oneWay) {
								if (intersect.y < 1.0f && intersect.y > 0) {
									addTriggerEvent(tBox)
								}
							} else {
								addTriggerEvent(tBox)
							}
						}
					})
				})
			}
		})
	}

/**
  * Finds which object is colliding the most with object a.
  */
	def findMaxCollision(
		a: PhysicalObject, delta: Float,
		lockX: Boolean = false,
		lockY: Boolean = false): (Vec2, Option[PhysicalObject]) = {
		var colIntersect = Vec2(0, 0)
		var collider: Option[PhysicalObject] = None
		spatialGrid.getSurrounding(a.position.x, a.position.y).foreach(other => {
			if (other.entity != a.entity) {
				val (intersection, events) =
					checkRigidCollision(a, other, delta, lockX, lockY)

				if (intersection.lengthSquared != 0 && 
					  colIntersect.lengthSquared < intersection.lengthSquared) {
					colIntersect = intersection
					collider = Some(other)
				}
			}
		})
		(colIntersect, collider)
	}

	// Optimization to reduce amount of needed recursion checks.
	val minPermittedCollision = 0.2f

/**
  * Finds the point at which a collision first occurs.
  */
	def findPointOfCollision(
		a: PhysicalObject, delta: Float,
		lockX: Boolean = false,
		lockY: Boolean = false): (Float, Vec2, Option[PhysicalObject]) = {
		// Check collision at time 0
		val (max0, col0) = findMaxCollision(a, 0, lockX, lockY)
		// Check collision at time delta
		val (maxFull, colFull) = findMaxCollision(a, delta, lockX, lockY)
		// If colliding at start, return start point
		if (max0.length > minPermittedCollision) {
			(0, max0, col0)
		// If not colliding at end, return end point
		} else if (maxFull.length == 0) {
			(delta, maxFull, colFull)
		} else {
			// Recursively find the point of collision using a binary search
			def findSuitable(
				i: Int, time: Float):
				(Float, Vec2, Option[PhysicalObject]) = {
					
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

/**
  * Reflects vector [v] along normal [n].
  */
	def reflection(v: Vec3, n: Vec3): Vec3 = {
		v - (2 * (v.dot(n)) * n) 
	}

	val ZAxis = Vec3(0, 0, 1)

/**
  * Applies physics to a physical object. lockX and lockY can be used to lock 
  * movement along an axis.
  */
	def applyPhysics(
		physObj: PhysicalObject, 
		scene: Scene, 
		delta: Float, 
		lockX: Boolean = false,
		lockY: Boolean = false,
		count: Int = 6): Unit = {
		// If not static, and time left
		if (!physObj.static && delta > 0 && !(lockX && lockY)) {
			// Find point of collision
			val (pointOfCollision, colIntersect, collider) = 
				findPointOfCollision(physObj, delta, lockX, lockY)
			// Check normal of collision
			val normal = Vec3(colIntersect, 0).normalize
			// Check distance travelled before collision
			val displacement =
				calcDistanceTravelled(
					physObj.velocity, physObj.acceleration, 
					pointOfCollision, lockX, lockY) + Vec3(colIntersect, 0)
			// Check change in velocity before collision
			val changeVelocity = 
				(physObj.acceleration + globalForce) * pointOfCollision

			// Compute change in velocity based on reflection from collision
			val deltaVel = 
				if (colIntersect.lengthSquared != 0.0) {
					val v = physObj.velocity + changeVelocity
					if (v.dot(normal) < 1.0f) {
						val reflected = reflection(v, normal)
						val perpendicular = normal.cross(ZAxis)

						val colFriction = collider match {
							case Some(col) => col.friction
							case _ => 0
						}
						// Amount reflected along normal
						val bounce = reflected * normal.abs
						// Amount tangent to normal
						val tangent = reflected * perpendicular.abs
						val normalTangent = 
							if (tangent.lengthSquared != 0) tangent.normalize else Vec3(0, 0, 0)

						// (de)acceleration caused by fiction
						val frictionAccel = 
							(colFriction * physObj.friction) * 
							(globalForce + physObj.acceleration).dot(normal) * normalTangent

						// Change in velocity caused by friction
						var frictionVel = frictionAccel * pointOfCollision

						// If friction is in direction of tangent, include tangent velocity, else only bounce
						if ((tangent + frictionVel).normalize == tangent.normalize) {
							bounce * physObj.bounciness + tangent - physObj.velocity + frictionVel
						} else {
							bounce * physObj.bounciness + physObj.velocity.neg
						}
					} else {
						changeVelocity
					}
				} else 
					changeVelocity

			// Update position and velocity
			physObj.position += displacement
			physObj.velocity += deltaVel
			// If velocity exceeds maximum, decelerate
			if (physObj.velocity.x.abs > physObj.maxHorizontal) {
				val d = (physObj.velocity.x.abs - physObj.maxHorizontal)
				physObj.velocity -= Vec3((d * d) * physObj.velocity.x.signum, 0, 0)
			}

			// If recursive count left greater than 0, apply physics again
			if (count > 0) {
				applyPhysics(
					physObj, 
					scene, 
					delta - pointOfCollision, 
					lockX || (normal.x * physObj.velocity.x < 0), 
					lockY || (normal.y * physObj.velocity.y < 0),
					count - 1)
			}
		}
	}

/**
  * Apply physics to entity. 
  */
	def applyTo(
			entity: Entity,
			scene: Scene,
			delta: Float): Changes = {
		(entity(PhysicsComp), entity(CollisionComp)) match {
			case (Some(
				PhysicsComponent(
					velocity, acceleration, mass, bounciness,
				  friction, frictionMultiplier, 
				  maxHorizontal, maxVertical, static)), 
				Some(collision)) => {

				// Apply if not static
				if (!static) {
					// Create physical object from entity
					val physicalObj = 
						PhysicalObject(
							entity, entity.position, velocity, acceleration,
						  mass, bounciness, collision, 
						  friction * frictionMultiplier, 
						  static, maxHorizontal, maxVertical)

					applyPhysics(physicalObj, scene, delta)
					checkTriggers(physicalObj, scene, delta)
					// Compute change in position
					val deltaPos = physicalObj.position - entity.position

					Changes(entity, Vector(
						Translation(physicalObj.position - entity.position), 
						Acceleration(physicalObj.velocity - velocity)),
						Vector())
				} else {
					Changes(entity, Vector(), Vector())
				}
			}
			case _ => Changes(entity, Vector(), Vector())
		}
	}

}