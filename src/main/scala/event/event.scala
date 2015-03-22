package event

import entity.Entity

// object EventType extends Enumeration {

// }

class Event(priv: Boolean) {}

case class TriggerEvent(
	priv: Boolean, 
	triggerName: String, 
	collider: Entity) extends Event(priv) {

}

case class SceneChangeEvent(
	priv: Boolean,
	inactivate: String,
	activate: String) extends Event(priv)