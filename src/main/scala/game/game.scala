package game

import org.lwjgl.Sys
import org.lwjgl.BufferUtils
import org.lwjgl.glfw._
import org.lwjgl.opengl._

import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.system.MemoryUtil._


object Main extends App {
  val WIDTH = 640
  val HEIGHT = 480
  
  val errorCallback = errorCallbackPrint(System.err)

  val keyCallback = new GLFWKeyCallback() {
    override def invoke(
        window: Long, key: Int,
        scancode: Int, action: Int, mods: Int) {
    }
  }
  
  // Window handle
  var window: Long = 0
  
  def run(): Unit = {
    println("Hello LWJGL " + Sys.getVersion + "!")
    try {
      init()
      loop()
      
      // Release window and window callbacks
      glfwDestroyWindow(window)
      keyCallback.release()
    } finally {
      // Terminate GLFW and release the GLFWerrorfun
      glfwTerminate()
      errorCallback.release()
    }
  }
  
  private def init(): Unit = {
    glfwSetErrorCallback(errorCallback)
    
    if (glfwInit() != GL11.GL_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }
    
    // Configure our window
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE) // hide window for now
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)

    // // Set core profile
    // glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    // glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2); 
    // glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    // glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    
    // Create the window
    window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL)
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW window")
    
    glfwSetKeyCallback(window, keyCallback)
    
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    // Center our window
    glfwSetWindowPos(
        window,
        (GLFWvidmode.width(vidmode) - WIDTH) / 2,
        (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    )
    
    glfwMakeContextCurrent(window)
    // Enable v-sync
    glfwSwapInterval(1)
    glfwShowWindow(window)
  }


  private def loop() = {
    GLContext.createFromCurrent()
 
    // Set the clear color
    glClearColor(230.0f/255.0f, 230.0f/255.0f, 230.0f/255.0f, 0.0f)

    glEnable(GL_BLEND)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_CULL_FACE)
    glDepthFunc(GL_LEQUAL)
    glCullFace(GL_BACK)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    glfwSetTime(0)
    var timer = 0.0
    var frameCount = 0
    var longestFrame = 0.0
    var frameAccumulator = 0.0
    while ( glfwWindowShouldClose(window) == GL_FALSE ) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer

      val delta = glfwGetTime()
      glfwSetTime(0)

      frameAccumulator += delta

      val FrameDuration = 1.0/60.0

      // fixed update
      while(frameAccumulator >= FrameDuration) {
        frameAccumulator -= FrameDuration
      }

      timer += delta
      frameCount += 1
      longestFrame = scala.math.max(delta, 0)
      if (timer > 1) {
        println("FPS: " + frameCount)
        timer -= 1
        frameCount = 0
        println("Longest frame: " + longestFrame)
        longestFrame = 0
      }

      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }
  
  run()
}