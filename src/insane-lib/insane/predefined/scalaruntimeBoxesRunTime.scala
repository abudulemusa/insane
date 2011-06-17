package insane
package predefined

import annotations._

@AbstractsClass("scala.runtime.BoxesRunTime")
class scalaruntimeBoxesRunTime {
  @AbstractsMethod("scala.runtime.BoxesRunTime.equalsCharObject((x$1:java.lang.Character, x$2:java.lang.Object)Boolean)")
  def equalsCharObject(x1 : java.lang.Character, x2 : java.lang.Object) : Boolean = { true }

  @AbstractsMethod("scala.runtime.BoxesRunTime.equalsNumObject((x$1:java.lang.Number, x$2:java.lang.Object)Boolean)")
  def equalsNumObject(x1 : java.lang.Number, x2 : java.lang.Object) : Boolean = { true }

  @AbstractsMethod("scala.runtime.BoxesRunTime.hashFromNumber((x$1:java.lang.Number)Int)")
  def hashFromNumber(x1 : java.lang.Number) : Int = { 0 }

}
