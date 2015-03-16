package behaviour

import entity.Entity
import scene.Scene
import system._

abstract class Behaviour {
	var initialized = false
	def initialize(entity: Entity, scene: Scene): Unit = {}

	def update(entity: Entity, scene: Scene, delta: Float): Changes = {
		Changes(entity)
	}

	def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		Changes(entity)
	}

	def onTriggerEnter(entity: Entity, scene: Scene, collider: Entity): Changes = {
		Changes(entity)
	}

	def onCollision(entity: Entity, scene: Scene, collider: Entity): Changes = {
		Changes(entity)
	}
}