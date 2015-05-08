package resource

import argonaut._, Argonaut._

import scala.io.Source

/**
  * Contains a map of animation cycles.
  */
class Animation(cycles: Map[String, List[Frame]]) extends Resource {
	def apply(name: String) = cycles(name)
}

object Animation {
/**
  * Loads an animation from a json file.
  */
	def apply(filePath: String) = {
		val input = Source.fromFile(filePath).mkString
		val source = input.decodeOption[List[Cycle]]

		new Animation(source.get.map(cycle => {
			(cycle.name, cycle.frames)
		}).toMap)
	}
}

/* Json protocols ----------------------------------------------------------- */
case class Frame(name: String, duration: Float)

object Frame {
	implicit def FrameCodecJson: CodecJson[Frame] = 
		casecodec2(Frame.apply, Frame.unapply)("name", "duration")
}

case class Cycle(name: String, frames: List[Frame])

object Cycle {
	implicit def CycleCodecJson: CodecJson[Cycle] =
		casecodec2(Cycle.apply, Cycle.unapply)("name", "frames")
}
/* -------------------------------------------------------------------------- */