package entity

/**
  * A class for keeping track of how much time has passed.
  *
  * @param minTime The minimum amount of time that must have passed before the
  * 							 timer can be reset.
  * @param maxTime Once the timer reaches maxTime, it is reset.
  */
case class Timer(minTime: Float = 0.0f, maxTime: Float) {
	private var running = false
	private var timeSeconds = 0.0f
	private var resetOnceMin = false
	
	def time = timeSeconds
	def isRunning = running

/**
  * Add time to the timer.
  */
	def += (t: Float) = {
		timeSeconds += t
		if (timeSeconds > maxTime) {
			reset()
		}
		if (resetOnceMin && time >= minTime) {
			reset()
		}
	}

/**
  * Start the timer.
  */
	def start() = {
		running = true
		resetOnceMin = false
	}

/**
  * Stop the timer and set it back to zero.
  */
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