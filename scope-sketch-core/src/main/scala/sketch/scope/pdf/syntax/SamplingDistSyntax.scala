package sketch.scope.pdf.syntax

import sketch.scope.conf.SamplingDistConf
import sketch.scope.measure.Measure
import sketch.scope.pdf.monad.SamplingDistMonad
import sketch.scope.pdf.{Dist, SamplingDist}
import sketch.scope.plot.DensityPlot

trait SamplingDistSyntax
  extends SamplingDistPropSyntax
    with SamplingDistMonadSyntax

trait SamplingDistPropSyntax {

  implicit class SamplingDistPropSyntaxImpl[A](dist: SamplingDist[A]) {
    def sample: (SamplingDist[A], A) = SamplingDist.sample(dist)
    def pdf(a: A): Option[Double] = SamplingDist.interpolationPdf(dist, a)
    def samples(n: Int): (SamplingDist[A], List[A]) = SamplingDist.samples(dist, n)
    def sampling: Option[DensityPlot] = SamplingDist.sampling(dist)
    def densityPlot: Option[DensityPlot] = SamplingDist.sampling(dist)
  }

}

trait SamplingDistMonadSyntax {

  lazy val distMonad: SamplingDistMonad[SamplingDist, Dist, SamplingDist, SamplingDistConf] = SamplingDistMonad()

  implicit class SamplingDistMonadSyntaxImpl[A](dist: SamplingDist[A]) {
    def map[B](f: A => B)(implicit measureB: Measure[B], conf: SamplingDistConf): SamplingDist[B] =
      distMonad.map(dist, f, measureB, conf)
    def flatMap[B](f: A => Dist[B])(implicit measureB: Measure[B], conf: SamplingDistConf): SamplingDist[B] =
      distMonad.bind(dist, f, measureB, conf)
  }

}
