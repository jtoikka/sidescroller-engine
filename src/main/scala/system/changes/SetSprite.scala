package system

import entity.Entity
import entity.Component._
import entity._

/**
  * Sets the sprite of an entity.
  */
case class SetSprite(name: String) extends StateChange {
	def applyTo(entity: Entity): Unit = {
		if (entity.contains(SpriteComp)) {
			entity(SpriteComp) match {
				case Some(SpriteComponent(sprite, spriteSheet, layer)) => {
					entity.updateComponent(SpriteComponent(name, spriteSheet, layer))
				}
				case _ =>
			}
		}
	}
}