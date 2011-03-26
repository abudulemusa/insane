package insane
package utils

class Settings {
  var verbosity                 = Verbosity.Normal

  def displayFixPoint           = verbosity > Verbosity.Verbose
  def displayProgress           = verbosity > Verbosity.Normal


  var displayclassanalyses = Seq[String]() 

  def displayClassAnalysis(toMatch: String) = {
    displayclassanalyses.exists(strMatch(toMatch, _))
  }

  var dumpcfg              = Seq[String]() 

  def dumpCFG(toMatch: String) = {
    dumpcfg.exists(strMatch(toMatch, _))
  }

  var dumpcg               = Seq[String]() 

  def dumpCG(toMatch: String) = {
    dumpcg.exists(strMatch(toMatch, _))
  }

  var dumpClassDescendents = false


  def strMatch(haystack: String, needle: String): Boolean = {
    (haystack contains needle.replace("_", "")) || (needle == "_")
  }

  def ifVerbosity(verb: Verbosity.Value)(body: => Unit) {
    if (verbosity >= verb) body
  }

  def ifVerbose(body: => Unit)    = ifVerbosity(Verbosity.Verbose)(body)
  def ifPleonastic(body: => Unit) = ifVerbosity(Verbosity.Pleonastic)(body)
}

object Verbosity extends Enumeration {
  val Quiet      = Value("Quiet",      1)
  val Normal     = Value("Normal",     2)
  val Verbose    = Value("Verbose",    3)
  val Pleonastic = Value("Pleonastic", 4)

  class VerbVal(name: String, val level: Int) extends Val(nextId, name) with Ordered[Value] {
    def compare(that: VerbVal) = level compare that.level
  }

  def Value(name: String, level: Int) = new VerbVal(name, level)
}

          
