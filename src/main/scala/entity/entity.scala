package entity

import spatial.Spatial
import scala.reflect.ClassTag
import math.Vec3
import Component._

case class Entity(
	components: Vector[Component], 
	children: Vector[Entity],
	var position: Vec3,
	tag: String = "") {

	private val componentMap = scala.collection.mutable.HashMap(
		(components map (c => (bitMask(c.getClass()), c)): _*))

	private val bitkey: Long = componentMap.keys.foldLeft(0L) {
		(a, b) => a ^ b
	}

	def key = bitkey

	def getComponent[T <: Component : ClassTag](c: Class[T]): Option[T] = {
		if (contains(c)) {
			componentMap(bitMask(c)) match {
				case comp : T => Some(comp)
				case _ => None
			}
		} else {
			None
		}
	}

	def apply[T <: Component : ClassTag](c: Class[T]) = getComponent(c)

	def contains[T <: Component](c: Class[T]): Boolean = {
		(bitMask(c) & bitkey) != 0
	}

	private var destroyed = false

	def isDestroyed = destroyed

	def destroy() = {
		destroyed = true
	}

	def updateComponent[T <: Component](c: T) = {
		componentMap(bitMask(c.getClass)) = c
	}
}