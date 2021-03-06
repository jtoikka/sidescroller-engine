package system

import entity.Entity
import scene.Scene

/**
  * Interface for systems.
  */
abstract class System (val key: Long) {
	def instantiate(scene: Scene): Unit

	def applyTo(entity: Entity, scene: Scene, delta: Float): Changes
}