package input

import org.lwjgl.glfw.GLFW._
import scala.collection.mutable.ArrayBuffer
import math.Vec2

class InputManager {
	val pressedKeys = ArrayBuffer[Int]()
	val heldKeys = ArrayBuffer[Int]()
	val releasedKeys = ArrayBuffer[Int]()
	val mousePressed = ArrayBuffer[Int]()
	val mouseHeld = ArrayBuffer[Int]()
	val mouseReleased = ArrayBuffer[Int]()
	var cursor = Vec2(0, 0)

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

	def mouseAction(button: Int, action: Int, mods: Int) = {
		action match {
			case GLFW_PRESS => {
				mousePressed += button
				mouseHeld += button
			}
			case GLFW_REPEAT =>
			case GLFW_RELEASE => {
				mouseReleased += button
				mouseHeld -= button
			}
			case _ => println("Invalid mouse action")
		}
	}

	def getInputs: 
		(Vector[Int], Vector[Int], Vector[Int],
		 Vector[Int], Vector[Int], Vector[Int], Vec2) = {
		val inputs = (
			pressedKeys.toVector, 
			heldKeys.toVector, 
			releasedKeys.toVector,
			mousePressed.toVector,
			mouseHeld.toVector,
			mouseReleased.toVector,
			cursor)

		pressedKeys.clear()
		releasedKeys.clear()
		mousePressed.clear()
		mouseReleased.clear()
		inputs
	}

	def setCursor(x: Double, y: Double) = {
		cursor = Vec2(x.toFloat, y.toFloat)
	}
}