package scene

import entity.Entity
import spatial.SpatialGrid2D
import scala.collection.mutable.ArrayBuffer
import system._
import system.StateChange
import math.Vec2
import event._

class Scene(
	val width: Float, val height: Float,
  val columns: Int, val rows: Int,
  val systems: Vector[System],
  val camera: Entity) {
	val entities = new SpatialGrid2D[Entity](width, height, columns, rows)
	// val entities = new SpatialGrid[Entity]()

	entities += camera

	def update(delta: Float, eventManager: EventManager) = {
		if (!isPaused) {
			systems.foreach{_.instantiate(this)}
			val changes = entities.flatMap(entity => {
				updateEntity(entity, delta)
			})
			val events = changes.flatMap {
				case Changes(entity, stateChanges, events) => {
					val oldPos = entity.position
					stateChanges.foreach {_.applyTo(entity)}
					// If the entity has moved, move it in the spatial grid
					if (oldPos != entity.position) {
						entities.move(entity, oldPos)
					}
					events
				}
			}
			eventManager.delegateEvents(events)
		}
	}

	def setInputs(
      pressed: Vector[Int], 
      held: Vector[Int], 
      released: Vector[Int],
      mousePressed: Vector[Int],
      mouseHeld: Vector[Int],
      mouseReleased: Vector[Int],
      cursor: Vec2) = {
    systems.foreach(_ match {
      case s: InputSystem => {
        s.pressedKeys = pressed
        s.heldKeys = held
        s.releasedKeys = released
        s.mousePressed = mousePressed
        s.mouseHeld = mouseHeld
        s.mouseReleased = mouseReleased
        s.cursor = cursor
      }
      case _ => 
    })
  }

	def updateEntity(entity: Entity, delta: Float): Changes = {
		val changes = systems.filter(s => (s.key & entity.key) > 0) map(system => {
			system.applyTo(entity, this, delta)
    })
    val stateChanges = changes flatMap (_.stateChanges)
    val events = changes flatMap (_.events)
    entity.triggers.clear()
    entity.timers.values.foreach(timer => {
    	if (timer.isRunning) {
    		timer += delta
    	}
    })
    entity.privateEvents.clear()
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