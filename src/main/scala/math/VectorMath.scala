package math

object VectorMath {
	implicit class VectorMath2(f: Float) {
		def * (v: Vec2) = v * f
	}

	implicit class VectorMath3(f: Float) {
		def * (v: Vec3) = v * f
	}
}