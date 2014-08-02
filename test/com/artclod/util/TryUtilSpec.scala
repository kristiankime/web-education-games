package com.artclod.util

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import com.artclod.util.TryUtil._

import scala.util.{Failure, Success, Try}

@RunWith(classOf[JUnitRunner])
class TryUtilSpec extends Specification {

	"retryOnFail" should {

		"succeed if the function always succeeds" in {
      Succeed() retryOnFail() must beSuccessfulTry
		}

    "fail if the function always fails" in {
      Fail() retryOnFail() must beFailedTry
    }

    "succeed if the function succeeds eventually" in {
      SucceedAfter(5) retryOnFail(10) must beSuccessfulTry
    }

    "fail if the function would succeed eventually but we don't try enough times" in {
      SucceedAfter(10) retryOnFail(5) must beFailedTry
    }

	}

}

case class SucceedAfter(failNum: Int) extends (() => Try[String]) {
  var count = 0

  def apply : Try[String] = {
    count = count + 1
    if(count > failNum) Success("success")
    else Failure(new IllegalStateException())
  }
}

case class Fail() extends (() => Try[String]) {
  def apply : Try[String] =Failure(new IllegalStateException())
}

case class Succeed() extends (() => Try[String]) {
  def apply : Try[String] = Success("success")
}