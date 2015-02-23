package resource

import java.awt.image.BufferedImage

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._

class Texture(image: BufferedImage) extends Resource {
  val width = image.getWidth
  val height = image.getHeight

  private val data = Array.fill[Int](width * height)(0)
  image.getRGB(0, 0, width, height, data, 0, width) 

  private def getIndex(x: Int, y: Int) = {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      -1
    } else {
      x + y * width
    }
  }

  def bind(textureId: Int) = {
    glBindTexture(GL_TEXTURE_2D, textureId)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
  }

  /**
   * Returns the value for color component as scala.Int; for pixel at (x, y).
   * 
   * @param x The x coordinate of the pixel whose color data is needed.
   * @param y The y coordinate of the pixel whose color data is needed.
   * 
   * @return The color data as Int, each component takes 8 bits (value range 
   *         is 0-255).
   */
  def getColor(x: Int, y: Int) = {
    val index = getIndex(x, y)
    if (index < 0) {
      -1
    } else {
      data(index)
    }
  }

  /**
   * Gets a subcomponent of a pixel (a, R, G or B).
   */
  private def getComponent(x: Int, y: Int, component: Int): Byte = {
    val aRGB = getColor(x, y) 
    getComponent(aRGB, component)
  }

  private def getComponent(color: Int, component: Int): Byte = {
    if (component >= 0 && component < 4) {
      val shift = (3 - component) * 8
      val mask = 0xFF
      if (color == -1) {
        -1
      } else {
        ((color >> shift) & mask).toByte
      }
    } else {
      -1
    }
  }

  private def getComponents(color: Int): Array[Byte] = {
    val a = getComponent(color, 0)
    val r = getComponent(color, 1)
    val g = getComponent(color, 2)
    val b = getComponent(color, 3) 
    Array(r, g, b, a)
  }
  
  def getBuffer = {
    val bytesPerColor = 4
    val buf = BufferUtils.createByteBuffer(width * height * bytesPerColor)
    data.foreach(color => {
      buf.put(getComponents(color))
    })
    buf.flip()
    buf
  }
}