package insane

import CFG.ASTToCFGTransform

import scala.tools.nsc._
import scala.tools.nsc.plugins._

class AnalysisComponent(val global: Global, val pluginInstance: InsanePlugin)
  extends PluginComponent
  with ASTToCFGTransform
  with CodeExtraction
{
  import global._

  override val runsRightAfter: Option[String] = Some("refchecks")
  override val runsAfter: List[String] = List("refchecks")

  val phaseName = pluginInstance.name

  def newPhase(prev: Phase) = new AnalysisPhase(prev)

  class AnalysisPhase(prev: Phase) extends StdPhase(prev) {
    def apply(unit: CompilationUnit): Unit = {
      extractCFGs(unit)

      //extractConditions(unit)
    }
  }

  def fatalError(msg: String) = Predef.error(msg)

}
