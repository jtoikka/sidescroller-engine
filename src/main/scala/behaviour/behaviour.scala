package behaviour

import entity.Entity
import scene.Scene

abstract class Behaviour {
	def update(entity: Entity, scene: Scene, delta: Float)

	def fixedUpdate(entity: Entity, scene: Scene, delta: Float)

	def onTriggerEnter(collider: Entity)

	def onCollision(collider: Entity)
}