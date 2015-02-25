package resource

import scala.io.Source
import entity.Entity
// import argonaut._, Argonaut._
import entity._
import math._
import scala.collection.mutable.ArrayBuffer

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

	implicit val spatialFormat = jsonFormat2(SpatialComponent)
	implicit val spriteFormat = jsonFormat3(SpriteComponent)
	implicit val modelFormat = jsonFormat2(ModelComponent)

	implicit object ComponentJsonFormat extends RootJsonFormat[Component] {
		def write(component: Component) = {
			component match {
				case s: SpatialComponent => s.toJson
				case s: SpriteComponent => s.toJson
				case m: ModelComponent => m.toJson
			}
		}

		def read(value: JsValue) = {
			value.asJsObject.getFields("type").head.convertTo[String] match {
				case "spatial" => value.convertTo[SpatialComponent]
				case "sprite" => value.convertTo[SpriteComponent]
				case "model" => value.convertTo[ModelComponent]
				case  _ => throw new Exception("Invalid component")
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

// object PrefabLoader {
// 	implicit lazy val QuaternionEncode: EncodeJson[Quaternion] =
// 		EncodeJson(q => {
// 			List(q.x, q.y, q.z, q.w).asJson
// 		})

// 	implicit lazy val QuaternionDecode: DecodeJson[Quaternion] =
// 		DecodeJson(cursor => for {
// 			x <- (cursor).as[Float]
// 			y <- (cursor.-<-:(1)).as[Float]
// 			z <- (cursor.-<-:(1)).as[Float]
// 			w <- (cursor.-<-:(1)).as[Float]
// 		} yield Quaternion(x, y, z, w))

// 	implicit lazy val Vec3Encode: EncodeJson[Vec3] =
// 		EncodeJson(v => {
// 			List(v.x, v.y, v.z).asJson
// 		})

// 	implicit lazy val Vec3Decode: DecodeJson[Vec3] =
// 		DecodeJson(cursor => for {
// 			x <- (cursor).as[Float]
// 			y <- (cursor.-<-:(1)).as[Float]
// 			z <- (cursor.-<-:(1)).as[Float]
// 		} yield Vec3(x, y, z))

// 	// implicit lazy val ComponentDecode: DecodeJson[Component] =
// 	// 	DecodeJson(cursor => for {
// 	// 		t <- (cursor --\ "type").as[String]
// 	// 		component <- {
// 	// 			val focus = cursor.focus
// 	// 			t match {
// 	// 				case "spatial" => focus.
// 	// 			}
// 	// 		}
// 	// 	})

// 	implicit lazy val EntityDecode: DecodeJson[Entity] =
// 		DecodeJson(cursor => for {
// 			// components <- (cursor --\ "components").as[ComponentList]
// 			// children <- (cursor --\ "children").as[List[Entity]]
// 			position <- (cursor --\ "position").as[Vec3]
// 			tag <- (cursor --\ "tag").as[String]
// 		} yield new Entity(Vector(), Vector(), position, tag))

// 	case class ComponentList(
// 		spatial: Option[SpatialComponent], 
// 		sprite: Option[SpriteComponent]) {
// 		def toVector = {
// 			Vector(spatial, sprite).filter(_.isDefined).map(_.get)
// 		}
// 	}

// 	implicit lazy val EntityLoadCodec: CodecJson[ComponentList] =
// 		casecodec2(ComponentList.apply, ComponentList.unapply)("spatial", "sprite")

// 	implicit lazy val SpatialComponentCodec: CodecJson[SpatialComponent] = 
// 		casecodec2(SpatialComponent.apply, SpatialComponent.unapply)("rotation", "scale")

// 	implicit lazy val SpriteComponentCodec: CodecJson[SpriteComponent] = 
// 		casecodec3(
// 			SpriteComponent.apply, 
// 			SpriteComponent.unapply)("sprite", "spriteSheet", "layer")

// 	def load(filePath: String): Option[Entity] = {
// 		val input = Source.fromFile(filePath).mkString
// 		println(input)
// 		input.decodeOption[Entity]
// 	}
// }