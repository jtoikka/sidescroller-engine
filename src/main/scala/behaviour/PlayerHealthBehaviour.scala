package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import event.EntitySpawnEvent

/**
  * Turret behaviour, shoots at a fixed rate.
  */
class PlayerHealthBehaviour(val args: List[String]) extends Behaviour {
	var player: Option[Entity] = None
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			player = scene.entities.find(e => e.tag == "player")
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val changes = player match {
			case Some(p) => {
				p(HealthComp) match {
					case Some(hp) => {
						Vector(SetSprite(hp.amount.toString))
					}
					case _ => Vector()
				}
			}
			case _ => Vector()
		}
		Changes(entity, changes)
	}
}