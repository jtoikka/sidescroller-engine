package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import scala.collection.mutable.ArrayBuffer

/**
  * Trigger a scene load event if the player collides with the entity.
  */
class LevelTriggerBehaviour(val args: List[String]) extends Behaviour {
	require(args.length > 0)
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val playerEvent = entity.privateEvents.find(event => {
			event match {
				case t: TriggerEvent if (t.collider.tag == "player") => true
				case _ => false
			}
		})
		val events = ArrayBuffer[Event]()
		if (playerEvent.isDefined) {
			require(args.length >= 2)
			events += SceneLoadEvent(false, args(0).toInt, args(1).toInt)
		}
		Changes(entity, Vector(), events.toVector)
	}
}