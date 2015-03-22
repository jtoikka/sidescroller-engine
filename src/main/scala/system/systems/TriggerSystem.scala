package system

import scene.Scene
import entity._
import entity.Component._
import physics._
import event._

class TriggerSystem extends System(bitMask(CollisionComp)) {

	def instantiate(scene: Scene): Unit = {}

	val MaxRange = 100.0f
	def checkTriggers(entity: Entity, scene: Scene, delta: Float): Unit = {
		scene.entities.getInRange(
			entity.position.x - MaxRange/2,
			entity.position.y - MaxRange/2,
		MaxRange, MaxRange).foreach(other => {
			if (other != entity) {
				if (other.contains(CollisionComp)) {
					val aCol = entity(CollisionComp).get
					val bCol = other(CollisionComp).get

					aCol.triggers.foreach(aBox => {
						bCol.triggers.foreach(bBox => {
							val intersect = 
								CollisionTest(entity.position, aBox, other.position, bBox)
							def addTriggerEvent(box: CollisionShape) = {
								box match {
									case t: Trigger => {
										entity.privateEvents += TriggerEvent(true, t.tag, other)
									}
								}
							}
							if (intersect.lengthSquared != 0) {
								addTriggerEvent(aBox)
							}
						})
					})
				}
			}
		})
	}

	def applyTo(entity: Entity, scene: Scene, delta: Float): Changes = {
		checkTriggers(entity, scene, delta)
		Changes(entity)
	}
}