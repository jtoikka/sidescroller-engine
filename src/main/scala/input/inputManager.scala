package input

import org.lwjgl.glfw.GLFW._
import scala.collection.mutable.ArrayBuffer

class InputManager {
	val pressedKeys = ArrayBuffer[Int]()
	val heldKeys = ArrayBuffer[Int]()
	val releasedKeys = ArrayBuffer[Int]()

	def keyAction(key: Int, scancode: Int, action: Int, mods: Int) = {
		action match {
			case GLFW_PRESS => {
				pressedKeys += key
				heldKeys += key
			}
			case GLFW_REPEAT =>
			case GLFW_RELEASE => {
				releasedKeys += key
				heldKeys -= key
			}
			case _ => println("Invalid key action")
		}
	}

	def getInputs: (Vector[Int], Vector[Int], Vector[Int]) = {
		val inputs = (
			pressedKeys.toVector, 
			heldKeys.toVector, 
			releasedKeys.toVector)

		pressedKeys.clear()
		releasedKeys.clear()
		inputs
	}
}