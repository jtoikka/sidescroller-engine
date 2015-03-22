package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._

class CameraBehaviour(val args: List[String]) extends Behaviour {
	var toFollow: Option[Entity] = None
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			toFollow = scene.entities.find(_.tag == "player")
			initialized = true
		}
	}

	def lerp(a: Vec3, b: Vec3, amount: Float): Vec3 = {
		(b - a) * amount
	}

	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		toFollow match {
			case Some(follow) => {
				val deltaPosition = {
					val l = lerp(entity.position, follow.position, 0.5f)
					Vec3(l.x, 0, l.z)
					// l
				}
				// val deltaPosition = Vec3(160, 120, 0) - entity.position
				Changes(entity, Vector(Translation(deltaPosition)))
			}
			case _ => Changes(entity)
		}
	}
}