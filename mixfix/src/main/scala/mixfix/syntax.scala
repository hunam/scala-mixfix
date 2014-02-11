package mixfix

import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

class syntax extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro Mixfix.impl
}