package resource

class ShaderProgram(
	val id: Int, 
  val attributes: Map[String, (Int, Int)], 
  val uniforms: Map[String, Int]) extends Resource {
	
}