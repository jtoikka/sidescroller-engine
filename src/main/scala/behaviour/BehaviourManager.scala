package behaviour

object BehaviourManager {
	def createBehaviour(id: String, params: List[Float]) = {
		id match {
			case "jump" => new JumpBehaviour(params)
			case _ => throw new Exception("Invalid behaviour: " + id)
		}
	}
}