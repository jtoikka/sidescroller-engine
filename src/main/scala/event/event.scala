package event

import entity.Entity
import system.StateChange
import math._
import scene.Scene

class Event(priv: Boolean) {}

case class TriggerEvent(
	priv: Boolean, 
	triggerName: String, 
	collider: Entity) extends Event(priv)

case class SceneChangeEvent(
	priv: Boolean,
	inactivate: String,
	activate: String) extends Event(priv)

case class EntitySpawnEvent(
	priv: Boolean,
	prefab: String,
	position: Vec3,
	scene: Scene,
	modifications: Vector[StateChange] = Vector()) extends Event(priv)