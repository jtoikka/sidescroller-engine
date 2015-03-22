package resource

import scala.io.Source
import entity.Entity
import entity._
import math._
import physics._
import state.StateManager
import behaviour._
// import behaviour.Behav

import spray.json._
import DefaultJsonProtocol._

object PrefabLoader extends DefaultJsonProtocol {
	implicit object Vec2JsonFormat extends JsonFormat[Vec2] {
		def write(v: Vec2) = {
			Vector(v.x, v.y).toJson
		}

		def read(value: JsValue) = {
			val vec = value.convertTo[Vector[Float]]
			Vec2(vec(0), vec(1))
		}
	}

	implicit object Vec3JsonFormat extends JsonFormat[Vec3] {
		def write(v: Vec3) = {
			Vector(v.x, v.y, v.z).toJson
		}

		def read(value: JsValue) = {
			val vec = value.convertTo[Vector[Float]]
			Vec3(vec(0), vec(1), vec(2))
		}
	}

	implicit object QuaternionJsonFormat extends JsonFormat[Quaternion] {
		def write(v: Quaternion) = {
			Vector(v.x, v.y, v.z, v.w).toJson
		}

		def read(value: JsValue) = {
			val vec = value.convertTo[Vector[Float]]
			Quaternion(vec(0), vec(1), vec(2), vec(3))
		}
	}

	implicit object BehaviourJsonFormat extends JsonFormat[Behaviour] {
		def write(b: Behaviour) = {
			b.toJson
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields("type", "parameters") match {
				case Seq(behaviourType, parameters) => {
					val id = behaviourType.convertTo[String]
					val params = parameters.convertTo[List[String]]
					BehaviourManager.createBehaviour(id, params)
				}
			}
		}
	}

	// implicit lazy val boxCollisionFormat = jsonFormat3(BoxCollision)

	implicit object BoxCollisionJsonFormat extends JsonFormat[BoxCollision] {
		def write(box: BoxCollision) = {
			JsObject(
				"origin" -> box.origin.toJson,
				"width" -> JsNumber(box.width),
				"height" -> JsNumber(box.height)
			)
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields(
				"origin", "width", "height") match {
				case Seq(origin, JsNumber(width), JsNumber(height)) => {
					new BoxCollision(
						origin.convertTo[Vec2], 
						width.toInt, 
						height.toInt)
				}
			}
		}
	}

	implicit object TriggerBoxCollisionJsonFormat extends JsonFormat[TriggerBoxCollision] {
		def write(box: TriggerBoxCollision) = {
			JsObject(
				"origin" -> box.origin.toJson,
				"width" -> JsNumber(box.width),
				"height" -> JsNumber(box.height),
				"tag" -> JsString(box.tag)
			)
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields(
				"origin", "width", "height", "tag") match {
				case Seq(origin, JsNumber(width), JsNumber(height), JsString(tag)) => {
					new TriggerBoxCollision(
						origin.convertTo[Vec2], 
						width.toInt, 
						height.toInt,
						tag)
				}
			}
		}
	}

	implicit object CollisionShapeJsonFormat extends JsonFormat[CollisionShape] {
		def write(shape: CollisionShape) = {
			shape match {
				case b: BoxCollision => b.toJson
			}
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields("type").head.convertTo[String] match {
				case "box" => value.convertTo[BoxCollision]
				case "triggerBox" => value.convertTo[TriggerBoxCollision]
				case  _ => throw new Exception("Invalid collision shape")
			}
		}
	}

	implicit object PhysicsComponentJsonFormat extends JsonFormat[PhysicsComponent] {
		def write(comp: PhysicsComponent) = {
			JsObject(
				"velocity" -> comp.velocity.toJson,
				"acceleration" -> comp.acceleration.toJson,
				"mass" -> JsNumber(comp.mass),
				"bounciness" -> JsNumber(comp.bounciness),
				"friction" -> JsNumber(comp.friction),
				"maxHorizontal" -> JsNumber(comp.maxHorizontal),
				"maxVertical" -> JsNumber(comp.maxVertical),
				"static" -> JsBoolean(comp.static))
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields(
				"velocity", 
				"acceleration", 
				"mass", 
				"bounciness", 
				"friction", 
				"maxHorizontal",
				"maxVertical",
				"static") match {
				case Seq(
					velocity, acceleration, 
					JsNumber(mass), JsNumber(bounciness), 
					JsNumber(friction), 
					JsNumber(maxHorizontal), JsNumber(maxVertical),
					JsBoolean(static)) => {
					PhysicsComponent(
						velocity.convertTo[Vec3],
						acceleration.convertTo[Vec3],
						mass.toFloat, 
						bounciness.toFloat, 
						friction.toFloat, 
						maxHorizontal.toFloat,
						maxVertical.toFloat, 
						1.0f, static)
				}
			}
		}
	}

	implicit lazy val spatialFormat = jsonFormat2(SpatialComponent)
	implicit lazy val spriteFormat = jsonFormat3(SpriteComponent)
	implicit lazy val modelFormat = jsonFormat2(ModelComponent)
	implicit lazy val cameraFormat = jsonFormat5(CameraComponent)
	implicit lazy val collisionFormat = jsonFormat4(CollisionComponent)
	// implicit lazy val physicsFormat = jsonFormat6(PhysicsComponent)
	implicit lazy val animationFormat = jsonFormat2(AnimationComponent)
	implicit lazy val inputFormat = jsonFormat1(InputComponent)
	implicit lazy val behaviourFormat = jsonFormat1(BehaviourComponent)

	implicit object StateJsonFormat extends JsonFormat[StateComponent] {
		def write(stateComponent: StateComponent) = {
			stateComponent.toJson
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields("stateMachine") match {
				case Seq(stateMachineId) => {
					val id = stateMachineId.convertTo[String]
					val state = StateManager.stateMachines(id).getDefault
					StateComponent(state)
				}
				case _ => {
					throw new Exception("Invalid state component")
				}
			}
		}
	}
	// implicit object BehaviourJsonFormat extends JsonFormat[Behaviour] {

	// }

	implicit object ComponentJsonFormat extends RootJsonFormat[Component] {
		def write(component: Component) = {
			component.toJson
			// component match {
			// 	case c: SpatialComponent => c.toJson
			// 	case c: SpriteComponent => c.toJson
			// 	case c: ModelComponent => c.toJson
			// 	case c: CameraComponent => c.toJson
			// 	case c: CollisionComponent => c.toJson
			// 	case p: PhysicsComponent
			// }
		}

		def read(value: JsValue) = {
			val t = value.asJsObject.getFields("type").head.convertTo[String]
			t match {
				case "spatial" => value.convertTo[SpatialComponent]
				case "sprite" => value.convertTo[SpriteComponent]
				case "model" => value.convertTo[ModelComponent]
				case "camera" => value.convertTo[CameraComponent]
				case "collision" => value.convertTo[CollisionComponent]
				case "physics" => value.convertTo[PhysicsComponent]
				case "animation" => value.convertTo[AnimationComponent]
				case "input" => value.convertTo[InputComponent]
				case "behaviour" => value.convertTo[BehaviourComponent]
				case "state" => value.convertTo[StateComponent]
				case  _ => throw new Exception("Invalid component: " + t)
			}
		}
	}

	implicit object EntityJsonFormat extends RootJsonFormat[Entity] {
		def write(entity: Entity) = {
			JsObject(
				"position" -> entity.position.toJson,
				"tag" -> JsString(entity.tag),
				"children" -> JsArray(entity.children.map(_.toJson).toVector),
				"components" -> JsArray(entity.components.map(_.toJson).toVector)
			)
		}
		
		def read(value: JsValue) = {
			value.asJsObject.getFields(
				"position", "tag", "components", "children") match {
				case Seq(position, JsString(tag), components, children) => {
					Entity(
						components.convertTo[Vector[Component]], 
						children.convertTo[Vector[Entity]], 
						position.convertTo[Vec3], 
						tag)
				}
			}
		}
	}

	implicit def load(filePath: String): Entity = {
		val input = Source.fromFile(filePath).mkString
		val json = input.parseJson
		json.convertTo[Entity]
	}
}