package behaviour

import entity.Entity
import scene.Scene
import system._

/**
  * Behaviours are essentially scripts. They are run through the
  * BehaviourSystem, where a behaviours update methods are called. They are a
  * convenient way to add new behaviours to game entities. 
  */

abstract class Behaviour {
	var initialized = false
/**
  * Called once every frame: set initialized to true to avoid repeated calls. 
  */
	def initialize(entity: Entity, scene: Scene): Unit = {}

/**
  * Called once every rendered frame.
  */
	def update(entity: Entity, scene: Scene, delta: Float): Changes = {
		Changes(entity)
	}
/**
  * Called once every logic update.
  */
	def fixedUpdate(entity: Entity, scene: Scene, delta: Float): Changes = {
		Changes(entity)
	}

/**
  * Called when the entity's trigger area collides with another entity.
  */
	def onTriggerEnter(entity: Entity, scene: Scene, collider: Entity): Changes = {
		Changes(entity)
	}

/**
  * Called in the event of a rigid collision.
  */
	def onCollision(entity: Entity, scene: Scene, collider: Entity): Changes = {
		Changes(entity)
	}
}