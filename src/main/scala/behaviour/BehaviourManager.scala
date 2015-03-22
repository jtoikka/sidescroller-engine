package behaviour

object BehaviourManager {
	def createBehaviour(id: String, params: List[String]) = {
		id match {
			case "jump" => new JumpBehaviour(params)
			case "button" => new ButtonBehaviour(params)
			case _ => throw new Exception("Invalid behaviour: " + id)
		}
	}

	def copy(b: Behaviour) = {
		b match {
			case j: JumpBehaviour => new JumpBehaviour(j.args)
			case b: ButtonBehaviour => new ButtonBehaviour(b.args)
		}
	}
}