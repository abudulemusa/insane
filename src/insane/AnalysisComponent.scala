package insane

import CFG.CFGGeneration
import alias._
import AST.CodeExtraction
import utils._
import types._
import hierarchy._
import utils.Reporters._
import storage._

import scala.tools.nsc.{Global, Phase}
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.util.SignalManager
import scala.util.control.Exception.ignoring

abstract class AnalysisComponent(pluginInstance: InsanePlugin, val reporter: Reporter, val settings: Settings)
  extends PluginComponent
  with Context
  with Functions
  with CFGGeneration
  with CodeExtraction
  with TypeAnalysis
  with ClassHierarchy
  with PointToAnalysis
  with ObjectSets
  with TypeHelpers
  with SerializationHelpers
  with Storage
{
  val global: Global

  import global._

  override val runsRightAfter: Option[String] = Some("mixin")
  override val runsAfter: List[String]        = List("mixin")

  val phaseName = pluginInstance.name+"-analysis"

  var subPhases: SubPhases =
    new CodeExtractionPhase   andThen
    new CFGGenerationPhase    andThen
    new ClassHierarchyPhase   andThen
    new TypeAnalysisPhase     andThen
    new PointToAnalysisPhase
    //new PurityAnalysisPhase

  class AnalysisPhase(prev: Phase) extends StdPhase(prev) {
    def apply(unit: CompilationUnit) { /* nothing */ }

    def runSubPhases() {
      for ((ph, i) <- subPhases.phases.zipWithIndex) {
        reporter.title((i+1)+": "+ph.name)
        ph.run
      }
    }

    def onExit() = {
      reporter.info("Bailing out...")
      reporter.printStoredMessages()
      sys.exit(1)
    }

    def onForcedExit() = {
      reporter.info("Bailing out...")
      sys.exit(1)
    }

    override def run() {
      ignoring(classOf[Exception]) {
        SignalManager("INT") = onExit()
      }

      val tStart = System.currentTimeMillis
      reporter.info("""    _                            """)
      reporter.info("""   (_)___  _________ _____  ___  """)
      reporter.info("""  / / __ \/ ___/ __ `/ __ \/ _ \ """)
      reporter.info(""" / / / / (__  ) /_/ / / / /  __/ """)
      reporter.info("""/_/_/ /_/____/\__,_/_/ /_/\___/  """)
      reporter.info("")
      reporter.info("Initializing...")

      initializeStorage

      reporter.info("Starting analysis...")
      runSubPhases()
      reporter.info("Finished ("+(System.currentTimeMillis-tStart)+"ms)")

      ignoring(classOf[Exception]) {
        SignalManager("INT") = onForcedExit()
      }

      reporter.printStoredMessages
    }
  }

  def newPhase(prev: Phase) = new AnalysisPhase(prev)

}
