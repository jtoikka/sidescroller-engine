package system

import entity.Entity
import scene.Scene

abstract class System (key: Long) {
	def applyTo(entity: Entity, scene: Scene, delta: Float): Changes
}