package sketch.scope.pdf

import cats.Monad
import sketch.scope.conf.DistConf
import sketch.scope.measure.Measure

import scala.language.higherKinds

/**
  * Distribution, or probability bistribution provides to get probability for given domain.
  * */
trait Dist[A] {

  def measure: Measure[A]

}

trait DistPropOps[D[_]<:Dist[_], C<:DistConf] extends DistPropLaws[D, C] {

  def probability[A](dist: D[A], from: A, to: A): Option[Double]

  def sample[A](dist: D[A]): (D[A], A)

}

trait DistPropLaws[D[_]<:Dist[_], C<:DistConf] { self: DistPropOps[D, C] =>

  def numericPdf[A](dist: D[A], a: A, delta: Prim): Option[Double] = {
    val measure = dist.measure.asInstanceOf[Measure[A]]
    val ap = measure.from(measure.to(a) + delta)

    probability(dist, a, ap).map(prob => prob / delta)
  }

  def pdf[A](dist: D[A], a: A, conf: C): Option[Double] = ???

  def cdf[A](sketch: D[A], a: A): Option[Double] = ???

  def samples[A](dist: D[A], n: Int): (D[A], List[A]) = {
    (0 until n).foldLeft[(D[A], List[A])]((dist, Nil)){ case ((utdDist1, acc), _) =>
      val (utdDist2, s) = sample(utdDist1)
      (utdDist2, s :: acc)
    }
  }

}

object Dist extends DistPropOps[Dist, DistConf] { self =>

  def delta[A](implicit measure: Measure[A]): Dist[A] = DeltaDist(measure, 0)

  def delta[A](center: A)(implicit measure: Measure[A]): Dist[A] = DeltaDist(measure, measure(center))

  def normal[A](mean: A, variance: Double)(implicit measure: Measure[A]): NormalDist[A] =
    NormalDist(mean, variance)

  def probability[A](dist: Dist[A], from: A, to: A): Option[Double] = dist match {
    case smooth: SmoothDist[A] => SmoothDist.probability(smooth, from, to)
    case sampled: SamplingDist[A] => SamplingDist.probability(sampled, from, to)
  }

  def sample[A](dist: Dist[A]): (Dist[A], A) = dist match {
    case smooth: SmoothDist[A] => SmoothDist.sample(smooth)
    case sampled: SamplingDist[A] => SamplingDist.sample(sampled)
  }

  override def pdf[A](dist: Dist[A], a: A, conf: DistConf): Option[Double] = dist match {
    case smooth: SmoothDist[A] => SmoothDist.pdf(smooth, a)
    case sampled: SamplingDist[A] => SamplingDist.pdf(sampled, a)
    case _ => super.pdf(dist, a, conf)
  }

}


