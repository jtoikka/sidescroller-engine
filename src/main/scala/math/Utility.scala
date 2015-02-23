package math

object Utility {
	def translationMatrix(translation: Vec3): Mat4 = {
		Mat4(
			1, 0, 0, translation.x,
			0, 1, 0, translation.y,
			0, 0, 1, translation.z,
			0, 0, 0, 1
		)
	}

	def scaleMatrix(scale: Vec3): Mat4 = {
		Mat4(
			scale.x, 0, 0, 0,
			0, scale.y, 0, 0,
			0, 0, scale.z, 0,
			0, 0, 0, 1
		)
	}
}