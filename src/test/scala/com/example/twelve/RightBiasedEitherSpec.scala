package com.example.twelve

import org.scalatest.{ FreeSpec, MustMatchers }

class RightBiasedEitherSpec extends FreeSpec with MustMatchers {

  "the new Either impl" - {

    "should map" in { // also flatMap, contains, toOption

      val resultOrError: String Either Int = Right(42)
      // instead of resultOrError.right.map - no need to project
      resultOrError map { r =>
        s"got $r"
      } mustBe Right("got 42")

      resultOrError.contains(42) mustBe true

      val results: List[Either[String, Int]] = List(Right(1), Left("some error"), Right(2), Left("some other error"))

      // to collect errors, we can use swap instead of matching and exploit the right-bias
      val swapped = (results map (_.swap))

      // cannot flatten ouright: swapped.flatten =>
      // Error:(20, 47) not enough arguments for method flatten:
      // (implicit asTraversable: scala.util.Either[Int,String] => scala.collection.GenTraversableOnce[B])List[B]
      // Unspecified value parameter asTraversable.
      val errorOptions: List[Option[String]] = results map (_.swap.toOption)
      val es: List[String] = errorOptions.flatten
      println(es)
    }

  }

}
