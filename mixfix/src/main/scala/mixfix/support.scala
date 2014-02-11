package mixfix

object support {
  
  trait MixfixFunction2 {
    protected type T1
    protected type C1 = (T1)

    protected type T2
    protected type C2 = (T1, T2)
    
    protected abstract class c2$[R](c1: C1) {
      protected val (t1) = c1
      protected def result(c2: C2): R
      protected def apply(t2: T2): R = result((t1, t2))
    }
  
    protected abstract class c1$t2(c1: C1) extends c2$[C2](c1) {
      override protected def result(c2: C2): C2 = c2
    }
    
  }
  
  trait MixfixFunction3 extends MixfixFunction2 {
    protected type T3
    protected type C3 = (T1, T2, T3)

    protected abstract class c3$[R](c2: C2) {
      protected val (t1, t2) = c2
      protected def result(c3: C3): R
      protected def apply(t3: T3): R = result((t1, t2, t3))
    }
  
    protected abstract class c2$t3(c2: (T1, T2)) extends c3$[C3](c2) {
      override protected def result(c3: C3): C3 = c3
    }
  }
}