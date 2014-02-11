package mixfix

import scala.reflect.macros.Context
import scala.collection.convert.decorateAll._

object Mixfix {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    val disabled = false
    val stub = false
    
    def generate(name: TermName, valDefs: List[ValDef], result: Tree, body: Tree): Tree = {
      if (stub) {
        q"""
          object $name {
            def mixfixEnabled = true
            def apply(..$valDefs): $result = $body
          }
        """
      } else {
        val hasHeader = !name.decoded.startsWith("_")
        val types: List[Tree] = valDefs.map { _.tpt }
        val fnTypes = types :+ result
        val mfnName = newTypeName(s"MixfixFunction${types.length}")
        val mfn = tq"_root_.mixfix.support.$mfnName"
        val fnName = newTypeName(s"scala.Function${types.length}")
        val fn = tq"$fnName[..$fnTypes]"
        val ts = types.zip(1 to types.length).map { case (t, i) ⇒
          q"override protected type ${newTypeName(s"T$i")} = $t" 
        }
        val r = q"protected type R = $result"

        def si(i: Int) = newTypeName(s"$$$i")
        def ci(i: Int) = newTermName(s"c$i")
        def Ci(i: Int) = newTypeName(s"C$i")
        def cisti(i: Int) = newTypeName(s"c${i}$$t${i+1}")
        def ciCi(i: Int): ValDef = q"val ${ci(i)}: ${Ci(i)}"
        def cis(i: Int) = newTypeName(s"c${i}$$")
        
        def generateImplicit(i: Int, term: String) = {
          q"""
            implicit class ${si(i)}(${ciCi(i)}) extends ${cisti(i)}(${ci(i)}) {
              def ${newTermName(s"$term").encodedName.toTermName} = apply _
            }
          """
        }
        
        def generateLastImplicit(i: Int, term: String) = {
          q"""
            implicit class ${si(i)}(${ciCi(i)}) extends ${cis(i+1)}[R](${ci(i)}) {
              protected override def result(${ciCi(i+1)}) = ($$apply).tupled(${ci(i+1)})
              def ${newTermName(s"$term").encodedName.toTermName} = apply _
            }
          """
        }
        
        def generateHeader(term: String) = {
          q"""
            def ${newTermName(s"$term").encodedName.toTermName}(t1: T1) = (t1) 
          """
        }
        
        val allTerms: List[String] =  name.decoded.split("_").filterNot(_.isEmpty).toList
        val terms = if (hasHeader) allTerms.tail else allTerms
        val headerTermOpt = if (hasHeader) allTerms.headOption else None
        def header = headerTermOpt match {
          case None ⇒ q""
          case Some(headerTerm) ⇒ generateHeader(headerTerm)
        } 
        
        val implicits = 
          terms.dropRight(1).zip(1 to terms.length-1).map { case (term, i) ⇒ generateImplicit(i, term) } :+
          generateLastImplicit(terms.length, terms.last)
        
        
        q"""
          object $name extends $mfn {
            ..$ts
            $r
            def mixfixEnabled = true
            def apply(..$valDefs): R = $body
            private def $$apply = apply _
            
            $header
            ..$implicits
          }
        """
      }
    }
    
    val result: Tree = {
      annottees.map(_.tree).toList match {
        case (in @ q"def $name(..$valDefs): $result = $body") :: Nil ⇒
          if (disabled) in else {
            generate(name, valDefs, result, body)
          }
        case bad :: Nil ⇒
          val pos = c.enclosingPosition
          c.error(pos, "invalid mixfix definition: " + bad)
          bad
      }
    }
    
    c.Expr[Any](result)
  }
}
