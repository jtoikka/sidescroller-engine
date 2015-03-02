package scene

import entity.Entity
import spatial.SpatialGrid2D
import scala.collection.mutable.ArrayBuffer
import system._
import system.StateChange

class Scene(
	width: Float, height: Float,
  columns: Int, rows: Int,
  systems: Vector[System],
  val camera: Entity) {
	val entities = new SpatialGrid2D[Entity](width, height, columns, rows)
	// val entities = new SpatialGrid[Entity]()

	entities += camera

	def update(delta: Float) = {
		if (!isPaused) {
			val changes = entities.flatMap(entity => {
				updateEntity(entity, delta)
			})
			val events = changes.flatMap {
				case Changes(entity, stateChanges, events) => {
					// println("A: " + entity.position)
					val oldPos = entity.position
					stateChanges.foreach {_.applyTo(entity)}
					// println("B: " + entity.position)
					if (oldPos != entity.position) {
						entities.move(entity, oldPos)
					}
					events
				}
			}
			// changes.foreach { case Changes(entity, stateChanges, events) =>
			// 	stateChanges.foreach { stateChange =>
			// 		val oldPos = entity.position
			// 		stateChange.applyTo(entity)
			// 		if (oldPos != entity.position) {
			// 			entities.move(entity, oldPos)
			// 		}
			// 	}
			// }
		}
	}

	def setInputs(
      pressed: Vector[Int], 
      held: Vector[Int], 
      released: Vector[Int]) = {
    systems.foreach(_ match {
      case s: InputSystem => {
        s.pressedKeys = pressed
        s.heldKeys = held
        s.releasedKeys = released
      }
      case _ => 
    })
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