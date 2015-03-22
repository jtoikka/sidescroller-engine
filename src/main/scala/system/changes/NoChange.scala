package system

import entity._
import entity.Entity
import entity.Component._
import math.Vec3

object NoChange extends StateChange {
	def applyTo(entity: Entity): Unit = {
	}
}