package behaviour

import entity._
import entity.Component._
import scene.Scene
import system._
import math._
import event._
import scala.collection.mutable.ArrayBuffer

class ButtonBehaviour(val args: List[String]) extends Behaviour {
	require(args.length > 0)
	override def initialize(entity: Entity, scene: Scene): Unit = {
		if (!initialized) {
			initialized = true
		}
	}
	
	override def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		val cursorEvent = entity.privateEvents.find(event => {
			event match {
				case t: TriggerEvent if (t.triggerName == "button" && t.collider.tag == "cursor") => {
					true
				}
				case _ => {
					false
				}
			}
		})
		val events = ArrayBuffer[Event]()
		if (cursorEvent.isDefined) {
			if (args(0) == "changeScene" && entity.triggers("mouse1Released")) {
				require(args.length >= 3)
				events += SceneChangeEvent(false, args(1), args(2))
			}
		} else {
		}
		Changes(entity, Vector(), events.toVector)
	}
}