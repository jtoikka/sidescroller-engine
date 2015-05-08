package resource

import argonaut._, Argonaut._

import scala.io.Source

import math._

case class LevelEntities(seeds: Vector[(String, Vec2)]) extends Resource


object LevelEntities {
	def apply(filePath: String) = {
		val input = Source.fromFile(filePath).mkString
		val source = input.decodeOption[List[Seed]]

		new LevelEntities(source.get.map(seed => {
			(seed.t, Vec2(seed.x, seed.y))
		}).toVector)
	}
}

case class Seed(t: String, x: Int, y: Int)

object Seed {
	implicit def SeedCodecJson: CodecJson[Seed] =
	 casecodec3(Seed.apply, Seed.unapply)("type", "x", "y")
}