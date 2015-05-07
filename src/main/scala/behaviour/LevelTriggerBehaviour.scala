package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import scala.collection.mutable.ArrayBuffer

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
			println("Aww yeah")
			require(args.length >= 2)
			events += SceneChangeEvent(false, args(0), args(1))
		}
		Changes(entity, Vector(), events.toVector)
	}
}