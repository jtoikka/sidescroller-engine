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

import scene.Scene
import factory.EntityFactory
import math.Vec3
import render.Renderer
import resource.ResourceManager
import system._
import input.InputManager
import resource.PrefabLoader


object Main extends App {
  val WIDTH = 640
  val HEIGHT = 360
  
  val errorCallback = errorCallbackPrint(System.err)

  val inputManager = new InputManager()

  val keyCallback = new GLFWKeyCallback() {
    override def invoke(
        window: Long, key: Int,
        scancode: Int, action: Int, mods: Int) {
      inputManager.keyAction(key, scancode, action, mods)
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
    window = glfwCreateWindow(WIDTH, HEIGHT, "Super Game", NULL, NULL)
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

    val testScene = new Scene(
      1000, 1000, 20, 20, Vector(
        new InputSystem(),
        new PhysicsSystem(Vector(Vec3(0, -9.81f * 8, 0)))
      ), 
      EntityFactory.createCamera(
        Vec3(0, 0, 0),
        640, 360, 0.1f, 1000.0f))

    // testScene.addEntity(EntityFactory.createPlayer(Vec3(0, 0, 0)))
    val resourceManager = new ResourceManager("src/resources/data/")

    val testPlayer = resourceManager.getPrefab("player", Vec3(0, 100, 0))
    val testBlock0 = resourceManager.getPrefab("tile0", Vec3(-8, 0, 0))
    val testBlock1 = resourceManager.getPrefab("tile0", Vec3(0, 0, 0))
    val testBlock2 = resourceManager.getPrefab("tile0", Vec3(8, 0, 0))

    testScene.addEntity(testPlayer)
    testScene.addEntity(testBlock0)
    testScene.addEntity(testBlock1)
    testScene.addEntity(testBlock2)

    val renderer = new Renderer(WIDTH, HEIGHT)

    glfwSetTime(0)
    var timer = 0.0
    var frameCount = 0
    var longestFrame = 0.0
    var frameAccumulator = 0.0
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer

      val delta = glfwGetTime()
      glfwSetTime(0)

      frameAccumulator += delta

      val FrameDuration = 1.0/60.0

      // fixed update
      while(frameAccumulator >= FrameDuration) {
        frameAccumulator -= FrameDuration
        val inputs = inputManager.getInputs
        testScene.setInputs(inputs._1, inputs._2, inputs._3)
        testScene.update(FrameDuration.toFloat)
      }

      renderer.renderScene(testScene, resourceManager)

      // Print frame rate
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