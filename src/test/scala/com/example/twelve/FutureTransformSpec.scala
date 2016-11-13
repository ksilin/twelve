package com.example.twelve

import org.scalatest.{AsyncFreeSpec, MustMatchers}

import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class FutureTransformSpec extends AsyncFreeSpec with MustMatchers {

  "playing a bit with Mr Klangs blog" - {

    // https://github.com/viktorklang/blog/blob/master/Futures-in-Scala-2.12-part-3.md
    // https://github.com/viktorklang/blog/blob/master/Futures-in-Scala-2.12-part-4.md

    "transform - the old way " in {

      val i = 1

      val f: Future[Int] = Future.successful(i)

      // like a Try.fold ot Xor.bimap
      //    transform[S](s: T => S, f: Throwable => Throwable)
      val resF: Future[String] =
        f.transform(i => s"received an int: $i", ex => ex)
      resF map { res =>
        res must contain(i.toString)
      }

      val t: Try[Int] = Try {
        throw new IllegalStateException("kaboom!"); 1
      }
      val ft: Future[Try[Int]] = Future(t)

      val r: Future[Try[String]] = ft.transform(tt =>
                                                  tt map { i =>
                                                    s"got $i"
                                                },
                                                identity)
      r map { t =>
        t mustBe (an[Failure[IllegalArgumentException]])
      }
    }

    "transform - the new way" in {
      // the new signature
      // def transform[S](f: Try[T] => Try[S])(implicit executor: ExecutionContext): Future[S]

      // allows for simpler map and recover impls

      val i              = 3
      val f: Future[Int] = Future.successful(i)

      val resF: Future[String] =
        f.transform { t: Try[Int] =>
          t map { x =>
            s"received an int: $x"
          }
        }
      resF map { res =>
        res must contain(i.toString)
      }
    }

    "simple lifting to Try with new transform" in {
      val i                    = 3
      val f: Future[Int]       = Future.successful(i)
      val tF: Future[Try[Int]] = f transform (Try(_))

      tF map { r =>
        r mustBe Success(i)
      }
    }

    "transformWith - flatMap and recoverWith in one package" in {

      val i              = 7
      val f: Future[Int] = Future.successful(i)
      f map { _ mustBe i }

      // transformWith[S](f: Try[T] => Future[S])
      // use the solution with the least power which will suffice for the task at hand
      // so prefer to use the flatMaps and the recoverWiths primarily, opting for the transformWith as required
      // flatMap and recoverWith are implemented in terms of transformWith
      val transformed: Future[String] = f.transformWith{ t: Try[Int] => t match {
        case Success(i) => Future.successful(s"got $i")
        case Failure(ex) => Future.failed(new Exception("oi"))
      }}

      transformed map (s => s mustBe s"got $i")
    }

  }

}
