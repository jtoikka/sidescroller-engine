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
import factory._
import math.Vec3
import render.Renderer
import resource.ResourceManager
import system._
import input.InputManager
import event._
import resource.PrefabLoader


// The main class
object Main extends App with Listener {
  // Screen dimensions in pixels
  val WIDTH = 832 //854
  val HEIGHT = 480

  val errorCallback = errorCallbackPrint(System.err)

  val inputManager = new InputManager()
  val eventManager = new EventManager()

  // Add self to list of listeners
  eventManager.addListener(this)

  // Mouse positions
  var xPos = BufferUtils.createDoubleBuffer(1)
  var yPos = BufferUtils.createDoubleBuffer(1)

  val keyCallback = new GLFWKeyCallback() {
    override def invoke(
        window: Long, key: Int,
        scancode: Int, action: Int, mods: Int) {
      inputManager.keyAction(key, scancode, action, mods)
    }
  }

  val mouseCallback = new GLFWMouseButtonCallback() {
    override def invoke(
      window: Long, button: Int,
      action: Int, mods: Int) {
      inputManager.mouseAction(button, action, mods)
    }
  }
  
  // Window handle
  var window: Long = 0
  
  // The program starts running at this point
  def run(): Unit = {
    // println("Hello LWJGL " + Sys.getVersion + "!")
    try {
      init()
      loop()
      
      // Release window and window callbacks
      glfwDestroyWindow(window)
      keyCallback.release()
      mouseCallback.release()
    } finally {
      // Terminate GLFW and release the GLFWerrorfun
      glfwTerminate()
      errorCallback.release()
    }
  }
  
  // Initializes window settings and callbacks
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
    glfwSetMouseButtonCallback(window, mouseCallback)
    
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

    GLContext.createFromCurrent()

    val menuScene = SceneFactory.createMenu(resourceManager)
    val gameScene = SceneFactory.createGame(resourceManager)

    inactiveScenes("0") = gameScene
    activeScenes("menu") = menuScene
 
    // Set the clear color
    // glClearColor(230.0f/255.0f, 230.0f/255.0f, 230.0f/255.0f, 0.0f)

  }

  // Inactive scenes are frozen, whilst update methods are caled for active
  // scenes. The game also renders any active scenes.
  val inactiveScenes = scala.collection.mutable.Map[String, Scene]()
  val activeScenes = scala.collection.mutable.Map[String, Scene]()

  val resourceManager = new ResourceManager("src/resources/data/")

  // Handles any accumulated events.
  def handleEvents(): Unit = {
    for (event <- events) {
      event match {
        // Flips the position of two scenes (one from active, the other from inactive)
        case e: SceneChangeEvent => {
          activeScenes(e.activate) = inactiveScenes(e.activate)
          inactiveScenes.remove(e.activate)
          inactiveScenes(e.inactivate) = activeScenes(e.inactivate)
          activeScenes.remove(e.inactivate)
          println("handeled")
        }
        // Loads a scene, and moves the player from the old scene to the new scene.
        case e: SceneLoadEvent => {
          if (!activeScenes.contains(e.replace.toString)) {
            val toRemove = activeScenes(e.remove.toString)
            val player = toRemove.entities.find(ent => ent.tag == "player").get
            val playerPos = player.position
            if (playerPos.x < 10.0f * 16.0f) {
              player.position = Vec3(28.0f * 16.0f, playerPos.y, playerPos.z)
            } else {
              player.position = Vec3(2.0f * 16.0f, playerPos.y, playerPos.z)
            }
            val camera = toRemove.camera
            val systems = toRemove.systems
            val newScene = SceneFactory.loadRoom(resourceManager, systems, camera, e.replace, player)
            activeScenes(e.replace.toString) = newScene
            activeScenes.remove(e.remove.toString)
          }
        }
        // Spawns a new entity
        case e: EntitySpawnEvent => {
          val ent = resourceManager.getPrefab(e.prefab, e.position)
          e.modifications.foreach(mod => mod.applyTo(ent))
          e.scene.addEntity(ent)
        }
      }
    }
    events.clear()
  }

  // The game loop itself. Takes care of updating, rendering and polling inputs.
  private def loop() = {
    val renderer = new Renderer(WIDTH, HEIGHT)

    glfwSetTime(0)
    var timer = 0.0
    var frameCount = 0
    var longestFrame = 0.0
    var longestRender = 0.0
    var longestTotal = 0.0
    var frameAccumulator = 0.0
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      val frameStart = System.nanoTime() // For performance logging

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer

      val delta = glfwGetTime()
      glfwSetTime(0)

      // Stores how much time has accumulated, and determines how many updates
      // need to be run.
      frameAccumulator += delta

      // Drop frames if too many are accumulated
      val FrameLimit = 2
      // If more than 10 frames, drop all frames
      if (frameAccumulator > 1.0/60.0 * 10) {
        frameAccumulator = 0
      } else if (frameAccumulator > 1.0/60.0 * FrameLimit) {
        frameAccumulator = 1.0/60.0 * FrameLimit
      }

      val FrameDuration = 1.0/60.0

      // fixed update
      while(frameAccumulator >= FrameDuration) {
        val before = System.nanoTime() // For performance logging

        glfwGetCursorPos(window, xPos, yPos) // Check cursor position
        val x = xPos.get(0) 
        val y = HEIGHT - yPos.get(0)
        inputManager.setCursor(x, y)

        frameAccumulator -= FrameDuration

        // Get inputs and forward them to the active scene
        val inputs = inputManager.getInputs 
        activeScenes.values.foreach(
          _.setInputs(
            inputs._1, inputs._2, inputs._3,
            inputs._4, inputs._5, inputs._6,
            inputs._7)
        )
        // Update all active scenes
        activeScenes.values.foreach(
          _.update(FrameDuration.toFloat, eventManager)
        )
        // Handle received events
        handleEvents()

        val after = System.nanoTime() // for performance logging
        longestFrame = scala.math.max(longestFrame, (after - before) / 1000000.0) // for performance logging
      }
      val before = System.nanoTime()
      activeScenes.values.foreach(renderer.renderScene(_, resourceManager))
      val after = System.nanoTime()

      longestRender = scala.math.max(longestRender, (after - before) / 1000000.0) // for performance logging

      // Print frame rate
      timer += delta
      frameCount += 1
      // longestFrame = scala.math.max(delta, 0)

      val frameEnd = System.nanoTime() // for performance logging

      // Update the window
      glfwSwapBuffers(window)

      // Poll input
      glfwPollEvents()

      longestTotal = scala.math.max(longestTotal, (frameEnd - frameStart) / 1000000.0) // for performance logging

      // Print performance information once every second.
      if (timer > 1) {
        println("FPS: " + frameCount)
        timer -= 1
        frameCount = 0
        println("Longest frame: " + longestFrame + "ms")
        println("Longest render: " + longestRender + "ms")
        println("Longest total: " + longestTotal + "ms")
        longestFrame = 0
        longestRender = 0
        longestTotal = 0
      }
    }
  }
  
  run()
}