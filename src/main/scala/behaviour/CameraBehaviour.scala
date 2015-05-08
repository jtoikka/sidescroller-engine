// package behaviour

// import entity._
// import entity.Component._
// import scene.Scene
// import system._
// import math._
// import event._

// class CameraBehaviour(val args: List[String]) extends Behaviour {
// 	var toFollow: Option[Entity] = None
// 	override def initialize(entity: Entity, scene: Scene): Unit = {
// 		if (!initialized) {
// 			toFollow = scene.entities.find(_.tag == "player")
// 			initialized = true
// 		}
// 	}

// 	def lerp(a: Vec3, b: Vec3, amount: Float): Vec3 = {
// 		(b - a) * amount
// 	}

	
// 	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
// 		// val ox = scene.width / 2
// 		// val oy = scene.height / 2
// 		Changes(entity)

// 		// Changes(entity, Vector(Translation(Vec3(ox, oy, 0) - entity.position)))
// 		// toFollow match {
// 		// 	case Some(follow) => {
// 		// 		val deltaPosition = {
// 		// 			val rounded = Vec3(follow.position.x.round, follow.position.y.round, 0.0f)
// 		// 			val d = rounded - entity.position
// 		// 			if (d.lengthSquared > 20.0f) {
// 		// 				val l = lerp(entity.position, rounded, 2.0f * delta)
// 		// 				Vec3(l.x, 0, l.z)
// 		// 			} else {
// 		// 				Vec3(0, 0, 0)
// 		// 			}
// 		// 			// l
// 		// 		}
// 		// 		// val deltaPosition = Vec3(160, 120, 0) - entity.position
// 		// 		Changes(entity, Vector(Translation(deltaPosition)))
// 		// 	}
// 		// 	case _ => Changes(entity)
// 		// }
// 	}
// }