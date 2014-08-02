package com.artclod.util

import scala.util.Random
import scala.util.Try
import concurrent._
import ExecutionContext.Implicits._

object TryUtil {
  val r = new Random(0)

  def retryOnFail[A](f: () => Try[A], maxRetries: Int = 5, maxWaitBetweenCalls: Int = 100) : Try[A] = {
    var result = f()
    var i = 0
    while(result.isFailure && i < maxRetries){
      // http://stackoverflow.com/questions/10317041/exception-handling-in-case-of-thread-sleep-and-wait-method-in-case-of-threads
      if(maxWaitBetweenCalls > 0) {
        future { blocking(Thread.sleep(r.nextInt(maxWaitBetweenCalls) + 1)); "done" }
        // Thread.sleep(r.nextInt(maxWaitTime) + 1)
      }
      result = f()
      i = i + 1
    }
    result
  }

  implicit class TryFunctionEnhanced[A](f : () => Try[A]) {
    def retryOnFail(maxRetries: Int = 5, maxWaitBetweenCalls: Int = 100) = TryUtil.retryOnFail(f, maxRetries, maxWaitBetweenCalls)
  }

}
