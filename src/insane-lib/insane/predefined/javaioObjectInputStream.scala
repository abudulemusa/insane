package insane
package predefined

import annotations._

@AbstractsClass("java.io.ObjectInputStream")
class javaioObjectInputStream {
  @AbstractsMethod("java.io.ObjectInputStream.close(()Unit)")
  def close() : Unit = { () }

  @AbstractsMethod("java.io.ObjectInputStream.defaultReadObject(()Unit)")
  def defaultReadObject() : Unit = { () }

  @AbstractsMethod("java.io.ObjectInputStream.<init>((x$1:java.io.InputStream)java.io.ObjectInputStream)")
  def PLOPINIT(x1 : java.io.InputStream) : java.io.ObjectInputStream = { new java.io.ObjectInputStream() }

  @AbstractsMethod("java.io.ObjectInputStream.readBoolean(()Boolean)")
  def readBoolean() : Boolean = { true }

  @AbstractsMethod("java.io.ObjectInputStream.readInt(()Int)")
  def readInt() : Int = { 0 }

  @AbstractsMethod("java.io.ObjectInputStream.readObject(()java.lang.Object)")
  def readObject() : java.lang.Object = { new java.lang.Object() }

}
