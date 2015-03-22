package input

import math.Vec3
import system.NoChange
import org.lwjgl.glfw.GLFW._

class ButtonInput() extends InputReceiver(
	mouseReleasedCallbacks = Map(
		(GLFW_MOUSE_BUTTON_1, (delta, entity) => {
			entity.triggers("mouse1Released") = true
			NoChange
		})
	)
) {}