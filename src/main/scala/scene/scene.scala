package scene

import entity.Entity
import spatial.SpatialGrid2D
import scala.collection.mutable.ArrayBuffer
import system._
import system.StateChange
import math.Vec2
import event._

/**
  * The scene class is a container for entities and systems. When update is
  * called for the scene, each entity is passed through every system for which
  * it meets the necessary requirements. Internally the scene uses a spatial
  * grid for sorting entities, to improve the speed of spatial queries (for
  * applications such as collision detection).
  *
  * @param width Width of the scene in pixels
  * @param height Height of the scene in pixels
  * @param columns How many columns to split scene in to
  * @param rows How many rows to split scene in to
  * @param systems The systems to apply to scene's entities
  * @param camera The camera via which the scene is visualized
	*/
class Scene(
	val width: Float, val height: Float,
  val columns: Int, val rows: Int,
  val systems: Vector[System],
  val camera: Entity) {

	val entities = new SpatialGrid2D[Entity](width, height, columns, rows)

	// Add camera to entities to ensure it gets updated
	entities += camera

/**
  * Update all entities.
  */ 
	def update(delta: Float, eventManager: EventManager) = {
		if (!isPaused) {
			// Instantiate each system
			systems.foreach{_.instantiate(this)}
			// Run entities through each system, and accumulate changes
			val changes = entities.flatMap(entity => {
				updateEntity(entity, delta)
			})
			// Collect events from updates, and apply changes to entities
			val events = changes.flatMap {
				case Changes(entity, stateChanges, events) => {
					val oldPos = entity.position
					// Apply changes
					stateChanges.foreach {_.applyTo(entity)}
					// If the entity moved, update position in spatial grid
					if (oldPos != entity.position) {
						entities.move(entity, oldPos)
					}
					// Remove destroyed entities
					if (entity.isDestroyed) {
						entities -= entity
					}
					events
				}
			}
			// Delegate events to listeners
			eventManager.delegateEvents(events)
		}
	}

/**
  * Updates inputs for InputSystem.
  */
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

/**
  * Runs entity through systems, and returns the made changes.
  */
	def updateEntity(entity: Entity, delta: Float): Changes = {
		val changes = systems.filter(s => (s.key & entity.key) > 0) map(system => {
			system.applyTo(entity, this, delta)
    })

    val stateChanges = changes flatMap (_.stateChanges)
    val events = changes flatMap (_.events)

    // Clear triggers
    entity.triggers.clear()
    // Update timers
    entity.timers.values.foreach(timer => {
    	if (timer.isRunning) {
    		timer += delta
    	}
    })
    // Clear private events
    entity.privateEvents.clear()
    Changes(entity, stateChanges, events)
	}

/**
  * Add an entity.
  */
	def addEntity(entity: Entity) = {
		entities += entity
	}

/**
  * Shorthand for adding an entity.
  */ 
	def += (entity: Entity) = addEntity(entity)

	def removeEntity(entity: Entity) = {
		entities -= entity
	}

/**
  * Shorthand for removing an entity.
  */
	def -= (entity: Entity) = removeEntity(entity)

	private var isPaused = false

	def pause = isPaused = true
	def unPause = isPaused = false
}