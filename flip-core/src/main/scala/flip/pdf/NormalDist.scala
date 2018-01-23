package flip.pdf

import flip.conf.SmoothDistConf
import flip.measure.Measure
import flip.rand._
import org.apache.commons.math3.distribution.NormalDistribution

/**
  * Normal distribution.
  * */
trait NormalDist[A] extends NumericDist[A] {
  val mean: Prim
  val variance: Prim
}

trait NormalDistOps extends NumericDistOps[NormalDist] {

  override def pdf[A](dist: NormalDist[A], a: A): Option[Prim] = {
    val numericDist = new NormalDistribution(dist.mean, dist.variance)
    val prim = dist.measure.to(a)

    Some(numericDist.density(prim))
  }

  def cdf[A](dist: NormalDist[A], a: A): Double = {
    val p = dist.measure.to(a)
    val numericDist = new NormalDistribution(dist.mean, dist.variance)

    numericDist.cumulativeProbability(p)
  }

  def icdf[A](dist: NormalDist[A], p: Double): A = {
    val numericDist = new NormalDistribution(dist.mean, dist.variance)

    dist.measure.from(numericDist.inverseCumulativeProbability(p))
  }

}

object NormalDist extends NormalDistOps {

  private case class NormalDistImpl[A](measure: Measure[A],
                                       conf: SmoothDistConf,
                                       mean: Prim,
                                       variance: Prim,
                                       rng: IRng) extends NormalDist[A]

  def apply[A](measure: Measure[A], conf: SmoothDistConf, mean: Prim, variance: Prim): NormalDist[A] =
    bare(measure, conf, mean, variance, IRng(mean.toInt + variance.toInt))

  def apply[A](mean: A, variance: Prim)(implicit measure: Measure[A], conf: SmoothDistConf): NormalDist[A] =
    apply(measure, conf, measure.to(mean), variance)

  def bare[A](measure: Measure[A], conf: SmoothDistConf, mean: Prim, variance: Prim, rng: IRng): NormalDist[A] =
    NormalDistImpl(measure, conf, mean, variance, rng)

  def modifyRng[A](dist: NormalDist[A], f: IRng => IRng): NormalDist[A] =
    bare(dist.measure, dist.conf, dist.mean, dist.variance, f(dist.rng))

}
