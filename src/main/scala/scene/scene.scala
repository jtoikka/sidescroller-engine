package scene

import entity.Entity
import spatial.SpatialGrid2D
import scala.collection.mutable.ArrayBuffer
import system.System
import system.Changes

class Scene(
	width: Float, height: Float,
  columns: Int, rows: Int,
  systems: Vector[System],
  val camera: Entity) {
	val entities = new SpatialGrid2D[Entity](width, height, columns, rows)

	def update(delta: Float) = {
		if (!isPaused) {
			val changes = entities flatMap(entity => {
				updateEntity(entity, delta)
			})
			val events = changes.flatMap {
				case Changes(entity, stateChanges, events) => {
					val oldPos = entity.position
					stateChanges.foreach {_.applyTo(entity)}
					if (oldPos != entity.position) {
						entities.move(entity, oldPos)
					}
					events
				}
			}
		}
	}

	def updateEntity(entity: Entity, delta: Float): Changes = {
		val changes = systems map(system => {
      system.applyTo(entity, this, delta)
    })
    val stateChanges = changes flatMap (_.stateChanges)
    val events = changes flatMap (_.events)
    Changes(entity, stateChanges, events)
	}

	def addEntity(entity: Entity) = {
		entities += entity
	}

	def += (entity: Entity) = addEntity(entity)

	def removeEntity(entity: Entity) = {
		entities -= entity
	}

	def -= (entity: Entity) = removeEntity(entity)

	var isPaused = false

	def pause = isPaused = true
	def unPause = isPaused = false
}