package system

import event.Event
import entity.Entity

case class Changes(entity: Entity, stateChanges: Vector[StateChange], events: Vector[Event]) {}