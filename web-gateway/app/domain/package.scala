import java.util.UUID

package object domain {
  trait IdObject {
    val value: UUID
  }

  trait Entity[I <: IdObject] {
    val id: I
    final override def equals(obj: Any): Boolean = obj match {
      case other: Entity[I] => other.canEqual(this) && this.id == other.id
      case _                => false
    }
    final override def hashCode: Int = id.hashCode
    def canEqual(other: Any): Boolean = other.getClass == this.getClass
  }

  trait Repository[M[+ _], ID <: IdObject, E <: Entity[ID]] {
    def store(entity: E): M[E]
    def resolve: M[Seq[E]]
    def resolveBy(id: ID): M[E]
    def deleteBy(id: ID): M[Unit]
  }
}
