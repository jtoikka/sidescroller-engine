package behaviour

object BehaviourManager {
	def createBehaviour(id: String, params: List[String]) = {
		id match {
			case "jump" => new JumpBehaviour(params)
			case "button" => new ButtonBehaviour(params)
			case "turret" => new TurretBehaviour(params)
			case "destroyOnCollision" => new DestroyOnCollision(params)
			case "shoot" => new ShootBehaviour(params)
			case "levelTrigger" => new LevelTriggerBehaviour(params)
			case _ => throw new Exception("Invalid behaviour: " + id)
		}
	}

	def copy(behaviour: Behaviour) = {
		behaviour match {
			case b: JumpBehaviour => new JumpBehaviour(b.args)
			case b: ButtonBehaviour => new ButtonBehaviour(b.args)
			case b: TurretBehaviour => new TurretBehaviour(b.args)
			case b: DestroyOnCollision => new DestroyOnCollision(b.args)
			case b: ShootBehaviour => new ShootBehaviour(b.args)
			case b: LevelTriggerBehaviour => new LevelTriggerBehaviour(b.args)
		}
	}
}