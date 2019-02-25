import scala.collection.generic.CanBuildFrom

package object controllers {
  implicit def implyConvertedTraversable[A, B, C[X] <: Traversable[X]](
    as: C[A])(implicit conversion: A => B, cbf: CanBuildFrom[C[A], B, C[B]]): C[B] = {
    val builder = cbf(as)
    builder.sizeHint(as)
    builder ++= as.map(conversion)
    builder.result()
  }
}
