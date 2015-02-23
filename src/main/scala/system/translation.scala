package system

import entity.Entity
import math.Vec3

case class Translation(translation: Vec3) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		entity.position = entity.position + translation
	}
}