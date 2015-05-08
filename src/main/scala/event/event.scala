package event

import entity.Entity
import system.StateChange
import math._
import scene.Scene

/**
  * Events enable different parts of the engine to communicate with one another.
  * 
  * @param priv Short for private. Private events do not get sent to the event
  *             manager. They are delegated only to the object that spawned it.
  */
class Event(priv: Boolean) {}

/**
  * Event in the case of a collider entering a trigger area.
  */
case class TriggerEvent(
	priv: Boolean, 
	triggerName: String, 
	collider: Entity) extends Event(priv)

/**
  * A scene change event. Contains information on which scene to inactivate, and
  * which scene to activate.
  */
case class SceneChangeEvent(
	priv: Boolean,
	inactivate: String,
	activate: String) extends Event(priv)

/**
  * Removes a scene [remove], and loads a new scene in its place. This event is
  * for level loading, where each level identifier is numeric (0 being the
  * first level, and levels increasing incrementally from there).
  */
case class SceneLoadEvent(
	priv: Boolean,
	remove: Int,
	replace: Int) extends Event(priv)

/**
  * Spawns a new entity of type [prefab] at a given [position], in the provided
  * [scene]. Applied [modifications] to the entity.
  */
case class EntitySpawnEvent(
	priv: Boolean,
	prefab: String,
	position: Vec3,
	scene: Scene,
	modifications: Vector[StateChange] = Vector()) extends Event(priv)