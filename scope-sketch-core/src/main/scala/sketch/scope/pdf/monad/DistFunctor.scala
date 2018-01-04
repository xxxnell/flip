package sketch.scope.pdf.monad

import sketch.scope.conf.DistConf
import sketch.scope.measure.Measure
import sketch.scope.pdf.Dist

import scala.language.higherKinds

trait DistFunctor[D1[_]<:Dist[_], C<:DistConf] {

  def map[A, B](dist: D1[A], f: A => B, measureB: Measure[B], conf: C): D1[B]

}
