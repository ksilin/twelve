package com.example.twelve

import org.scalatest.{ FreeSpec, MustMatchers }

class Si2712Spec extends FreeSpec with MustMatchers {

  // recap the si2771 with DJSpiewak: https://gist.github.com/djspiewak/7a81a395c461fd3a09a6941d4cd040f2

  "what was the issue again?" in {

    // TODO - still does not work

    // The compiler tries to instantiate F[_] = Function1[_, _], but it doesn't work because the number of parameters doesn't line up, and it fails the build
    def foo[F[_], A](fa: F[A]): String = fa.toString

    val intToInt: Function1[Int, Int] = { x: Int =>
      x * 2
    }

    "foo(intToInt)" mustNot typeCheck
    "foo {x: Int =>x * 2}" mustNot typeCheck
  }

}
