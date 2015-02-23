package resource

import scala.io.Source
import org.lwjgl.opengl._
import org.lwjgl.opengl.GL20._

class CompileException(log: String) extends Exception {}

object ShaderManager {
  def createProgram(
      vertexSrc: String, 
      fragSrc: String): ShaderProgram = {
    val vertLines = Source.fromFile(vertexSrc).getLines().toVector
    val fragLines = Source.fromFile(fragSrc).getLines().toVector
    
    val attribLines = vertLines.filter(line => {
      val firstWord = line.split(" ")(0)
      firstWord == "attribute" || firstWord == "in"
    })
    val uniformLines = 
      vertLines.filter(_.split(" ")(0) == "uniform") ++ 
      fragLines.filter(_.split(" ")(0) == "uniform")
    val splitAttributes = attribLines.map(_.split(" "))
    val attributes = splitAttributes.map {words =>
      val name = words(2).dropRight(1)
      // val size = words(1) match {
      //   case "float" => 1
      //   case "vec2" => 2
      //   case "vec3" => 3
      //   case _ => throw new CompileException("Invalid attribute: " + words(1))
      // }
      // (name, size)
      name
    }
    val uniforms = uniformLines.map(_.split(" ")(2).dropRight(1))

    val vertSource = vertLines.mkString("\n")
    println(vertSource)
    val fragSource = fragLines.mkString("\n")
    
    val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    glShaderSource(vertexShader, vertSource)
    glCompileShader(vertexShader)
    
    val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fragmentShader, fragSource)
    glCompileShader(fragmentShader)
    
    val program = glCreateProgram()
    glAttachShader(program, vertexShader)
    glAttachShader(program, fragmentShader)
    glLinkProgram(program)
    
    checkShaderCompilation(vertexShader, GL_VERTEX_SHADER)
    checkShaderCompilation(fragmentShader, GL_FRAGMENT_SHADER)
    
    glDeleteShader(vertexShader)
    glDeleteShader(fragmentShader)
    
    glUseProgram(program)

    val attribMap: Map[String, Int] = attributes.map(attrib => {
      val attribLocation = glGetAttribLocation(program, attrib)
      glEnableVertexAttribArray(attribLocation)
      (attrib, attribLocation)
    }).toMap

    val unifMap: Map[String, Int] = uniforms.map(unif => {
      val unifLocation = glGetUniformLocation(program, unif)
      (unif, unifLocation)
    }).toMap
    
    glUseProgram(0)
    new ShaderProgram(program, attribMap, unifMap)
  }
  
  // def attachTexture(programName: String, textureName: String, texIndex: Int) = {
  //   val program = programs(programName)
  //   glUseProgram(program.id)
  //   val texUnif = glGetUniformLocation(program.id, textureName)
  //   glUniform1i(texUnif, texIndex)
  //   glUseProgram(0)
  // } 

  private def checkShaderCompilation(shader: Int, shaderType: Int): Boolean = {
    val comp = glGetShaderi(shader, GL_COMPILE_STATUS)
		val len = glGetShaderi(shader, GL_INFO_LOG_LENGTH)
		val typeText = shaderType match {
		  case GL_VERTEX_SHADER => "VERTEX_SHADER"
		  case GL_FRAGMENT_SHADER => "FRAGMENT_SHADER"
		  case default => "shader"
		}
		
		var log = ""
		
		val error = glGetShaderInfoLog(shader, len)
		if (error != null && error.length() != 0) {
		  log += typeText + " compile log:\n" + error + "\n"
		}
		if (comp == GL11.GL_FALSE) {
      println(log)
		  throw new CompileException(
		      if (log.length() != 0) log else "Could not compile " + typeText)
		  false
		} else {
		  true
		}
  }
}