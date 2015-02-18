package spatial

import scala.collection.mutable.ArrayBuffer
import math.Vec3
import scala.math._

class SpatialGridException(message: String) extends Exception(message) {}

class SpatialGrid2D[T <: Spatial](
	width: Float, height: Float,
	columns: Int, rows: Int) {

	private val cellWidth = width / columns
	private val cellHeight = height / rows

	private val grid = ArrayBuffer.fill(columns * rows)(ArrayBuffer[T]())

	def size = columns * rows

	def apply(index: Int) = grid(index)

/** 
	* Adds an object to the collection.
	*	
	* @param an object to add.
	* @return True if the object was successfully added, false otherwise
	*/
	def add(elem: T): Boolean = {
		val pos = elem.position
		val index = getIndex(pos.x, pos.y)
		if (index >= 0) {
			grid(index) += elem
			true
		} else {
			false
		}
	}

/**
  * Removes an element.
  */
  def remove(elem: T): T = {
  	val pos = elem.position
		val index = getIndex(pos.x, pos.y)
		if (index >= 0) {
			grid(index).remove(grid(index).indexOf(elem))
		} else {
			throw new SpatialGridException("Element to remove not found.")
		}
  }

/**
  * Removes an element if it exists.
  */
  def -= (elem: T): Unit = {
  	try {
  		remove(elem)
  	} catch {
  		case e: SpatialGridException =>
  	}
  }

/** 
	* Adds an object to the collection.
	*	
	* @param an object to add.
	* @return True if the object was successfully added, false otherwise
	*/
	def += (elem: T): Boolean = add(elem)

/**
  * Moves an object from one grid position to another
  */
	def move(elem: T, previousPosition: Vec3) = {
		val previousIndex = getIndex(previousPosition.x, previousPosition.y)
		if (previousIndex >= 0) {
			val previousCell = grid(previousIndex)
			val i = previousCell.indexOf(elem)
			if (i >= 0)
				previousCell.remove(i)
			else
				throw new SpatialGridException("Trying to move an invalid element.")
		}
		add(elem)
	}

/**
  * Returns elements within the given spatial range.
	* 
	* @param x left
	* @param y top
	* @param w width
	* @param h height
	* @return All elements in the given range
  */ 

	def getInRange(x: Float, y: Float, w: Float, h: Float): Vector[T] = {
		val temp = for (
			i <- max(x, 0) to min(x + w + cellWidth, width - 1) by cellWidth;
			j <- max(y, 0) to min(y + h + cellHeight, height - 1) by cellHeight)
			yield grid(getIndex(i, j)).toVector

		temp.flatten.filter(elem => 
			elem.position.x >= x && elem.position.x <= x + width &&
			elem.position.y >= y && elem.position.y <= y + height).toVector
	}

/**
  * Applies a function for each element.
  */
	def foreach(op: T => Unit) = {
		for (i <- 0 until size) {
			grid(i).foreach(op)
		}
	}

/**
  * Finds the first element to match the predicate function.
  */
	def find(pred: T => Boolean): Option[T] = {
		var i = 0
		var found = false
		var r: Option[T] = None
		while (i < size && !found) {
			val cell = grid(i)
			var j = 0
			while (j < size && !found) {
				found = pred(cell(j))
				if (found) r = Some(cell(j))
				j += 1
			}
			i += 1
		}
		r
	}


/** 
  * Returns the grid index the point resides in.
  */
	private def getIndex(x: Float, y: Float): Int = {
		val xIndex = scala.math.floor(x / cellWidth)
		val yIndex = scala.math.floor(y / cellHeight)

		if (xIndex >= 0 && xIndex < columns && yIndex >= 0 && yIndex < rows) {
			yIndex.toInt * columns + xIndex.toInt
		} else {
			-1
		}
	}
}