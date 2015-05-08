package input

import math.Vec3
import system.Translation
import org.lwjgl.glfw.GLFW._

/**
  * Entity follows mouse cursos.
  */
class CursorInput() extends InputReceiver(
	cursorCallbacks = Vector(
		(cursor, entity) => {
			val deltaPos = cursor - entity.position.xy
			Translation(Vec3(deltaPos, 0))
		}
	)
) {}