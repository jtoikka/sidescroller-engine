package system

import entity.Entity
import math.Vec3

/**
  * Applies a translation to an entity (modifies position). Also updates entity's
  * children's positions.
  */
case class Translation(translation: Vec3) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		entity.position = entity.position + translation
		entity.children.foreach(child => {
			child.position = child.position + translation
		})
	}
}