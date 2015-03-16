package entity

case class Timer(minTime: Float, maxTime: Float) {
	private var running = false
	private var timeSeconds = 0.0f
	private var resetOnceMin = false
	
	def time = timeSeconds
	def isRunning = running

	def += (t: Float) = {
		timeSeconds += t
		if (timeSeconds > maxTime) {
			reset()
		}
		if (resetOnceMin && time >= minTime) {
			reset()
		}
	}

	def start() = {
		running = true
		resetOnceMin = false
	}

	def reset() = {
		if (time >= minTime) {
			running = false
			timeSeconds = 0.0f
			resetOnceMin = false
		} else {
			resetOnceMin = true
		}
	}
}