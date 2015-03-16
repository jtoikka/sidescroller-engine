package system

import entity.Component._
import entity._
import scene.Scene
import state._

class StateSystem extends System(bitMask(StateComp)) {
	def instantiate(scene: Scene) = {}

	def applyTo(entity: Entity, scene: Scene, delta: Float): Changes = {
		entity(StateComp) match {
			case Some(StateComponent(state)) => {
 				val trig = state.triggers.find(trigger => {
 					trigger.condition(entity)
 				})
 				trig match {
 					case Some(trigger) => {
 						Changes(entity, trigger.state.changes ++ 
 							Vector(new SetState(trigger.state)), Vector())
 					}
 					case _ => {
 						Changes(entity, Vector(), Vector())
 					}
 				}
			}
			case _ => {
				Changes(entity, Vector(), Vector())
			}
		}
	}
}