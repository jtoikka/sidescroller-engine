package resource

import argonaut._, Argonaut._

import scala.io.Source

import math.Vec2

class SpriteSheet(
	val texture: String, 
	val width: Int, val height: Int, 
	val sprites: Map[String, Sprite]) extends Resource {
  
  def apply(spriteId: String): Sprite = {
  	if (sprites.contains(spriteId)) {
  		sprites(spriteId)
  	} else {
  		throw new Exception("Sprite " + spriteId + " not found.")
  	}
  }
}

/* Resource loading --------------------------------------------------------- */
case class Sprite(x: Int, y: Int, w: Int, h: Int) {}

object Sprite {
	implicit def SpriteCodecJson: CodecJson[Sprite] = 
		casecodec4(Sprite.apply, Sprite.unapply)("x", "y", "w", "h")
}

case class SourceSize(w: Int, h: Int) {}

object SourceSize {
	implicit def SourceSizeCodecJson: CodecJson[SourceSize] = 
		casecodec2(SourceSize.apply, SourceSize.unapply)("w", "h")
}

case class Pivot(x: Int, y: Int) {}

object Pivot {
	implicit def PivotCodecJson: CodecJson[Pivot] = 
		casecodec2(Pivot.apply, Pivot.unapply)("x", "y")
}

case class SpriteSource(
	fileName: String, 
	frame: Sprite, 
	rotated: Boolean,
	trimmed: Boolean,
	spriteSourceSize: Sprite,
	sourceSize: SourceSize,
	pivot: Pivot
	)

object SpriteSource {
	implicit def SpriteSourceCodecJson: CodecJson[SpriteSource] =
		casecodec7(SpriteSource.apply, SpriteSource.unapply)(
			"filename", "frame", 
			"rotated", "trimmed", 
			"spriteSourceSize", "sourceSize",
			"pivot")
}
case class MetaInfo(
	app: String, 
	version: String, 
	image: String, 
	format: String, 
	size: SourceSize, 
	scale: String, 
	smartupdate: String)

object MetaInfo {
	implicit def MetaInfoCodecJson: CodecJson[MetaInfo] =
		casecodec7(MetaInfo.apply, MetaInfo.unapply)(
			"app", "version", "image", "format", "size", "scale", "smartupdate"
		)
}

case class FrameList(frames: List[SpriteSource], meta: MetaInfo) {}

object FrameList {
	implicit def FrameListCodecJson: CodecJson[FrameList] =
		casecodec2(FrameList.apply, FrameList.unapply)("frames", "meta")
}

object SpriteSheet {
	def apply(filePath: String) = {
		val input = Source.fromFile(filePath).mkString
		val source = input.decodeOption[FrameList]

		val frames = source.get.frames
		val meta = source.get.meta

		new SpriteSheet (meta.image.split('.')(0), meta.size.w, meta.size.h,
			frames.map(sprite => {
				(sprite.fileName.split('.')(0), sprite.frame)
			}).toMap
		)
	}
}