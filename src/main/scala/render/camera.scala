package render

import math.{Mat3, Mat4}

object Camera {

  def orthographicProjection3(
      left: Float, right: Float,
      bottom: Float, top: Float) = {
    Mat3(
      2.0f/(right - left), 0.0f, -(right+left)/(right-left),
      0.0f, 2.0f/(top - bottom), -(top+bottom)/(top-bottom),
      0.0f, 0.0f, 1.0f
    )
  }

  def orthographicProjection4(
  		left: Float, right: Float,
  		bottom: Float, top: Float,
  		zNear: Float, zFar: Float) = {
  	Mat4(
  		2.0f/(right - left), 0.0f, 0.0f, -(right+left)/(right-left),
  		0.0f, 2.0f/(top - bottom), 0.0f, -(top+bottom)/(top-bottom),
  		0.0f, 0.0f, -2.0f/(zFar - zNear), -(zFar+zNear)/(zFar-zNear),
  		0.0f, 0.0f, 0.0f, 1.0f)
  }

  def calcFrustumScale(fovDegrees: Float): Float = {
    val degToRadians = scala.math.Pi * 2.0f / 360.0f
    var fovRad = fovDegrees * degToRadians
    1.0f / scala.math.tan(fovRad / 2.0f).toFloat;
  }

  def perspectiveProjection(
    width: Float, height: Float, 
    fieldOfView: Float, 
    zNear: Float, zFar: Float): Mat4 = {
    
    val frustumScale = calcFrustumScale(fieldOfView);
    
    val m00 = frustumScale / (width / height)
    val m11 = frustumScale
    val m22 = (zFar + zNear) / (zNear - zFar)
    val m23 = -1.0f
    val m32 = (2 * zFar * zNear) / (zNear - zFar)
    
    Mat4(
    	m00, 0, 0, 0,
    	0, m11, 0, 0,
    	0, 0, m22, m23,
    	0, 0, m32, 0)
  }
}
