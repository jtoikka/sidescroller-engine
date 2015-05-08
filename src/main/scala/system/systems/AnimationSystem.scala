package system

import entity.Component._
import entity._
import scene.Scene
import state._
import resource.ResourceManager

/**
  * Updates animation timers and sets sprites according to the entity's current 
  * animation cycle.
  */ 
class AnimationSystem(resourceManager: ResourceManager) extends System(bitMask(AnimationComp)) {
	def instantiate(scene: Scene) = {}

	def applyTo(entity: Entity, scene: Scene, delta: Float): Changes = {
		entity(AnimationComp) match {
			case Some(AnimationComponent(animationSheet, animation, timer)) => {
				val anims = resourceManager.getAnimation(animationSheet)
				val frames = anims(animation)
				var accum = 0.0f
				var i = -1
				// Cycle through frames until the frame, after which move to the beginning
				do {
					i += 1
					if (i == frames.length) i = 0
					accum += frames(i).duration
				} while (accum < timer)
				Changes(entity, Vector(SetSprite(frames(i).name), UpdateAnimationTimer(delta)), Vector())
			}
 			case _ => {
				Changes(entity, Vector(), Vector())
			}
		}
	}
}