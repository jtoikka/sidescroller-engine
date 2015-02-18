package scene

import entity.Entity
import spatial.SpatialGrid2D
import scala.collection.mutable.ArrayBuffer

class Scene(
	width: Float, height: Float,
  columns: Int, rows: Int,
  systems: Vector[System], 
  filters: Map[String, ArrayBuffer[Entity]]) {
	val entities = new SpatialGrid2D[Entity](width, height, columns, rows)

	def update(delta: Float) = {
		entities.foreach(entity => {

		})
	}

	def addEntity(entity: Entity) = {
		if (filters.contains(entity.tag)) {
			filters(entity.tag) += entity
		}
		entities += entity
	}

	def removeEntity(entity: Entity) = {
		if (filters.contains(entity.tag)) {
			filters(entity.tag) -= entity
		}
		entities -= entity
	}
}