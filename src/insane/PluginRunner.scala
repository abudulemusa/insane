package insane

import scala.tools.nsc.{Global,Settings,Phase}
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.transform.LazyVals
import scala.tools.nsc.transform.Erasure
import scala.tools.nsc.plugins.PluginComponent

/** This class is a compiler that will be used for running the plugin in
 * standalone mode. Original version courtesy of D. Zufferey. */
class PluginRunner(settings : Settings) extends Global(settings, new ConsoleReporter(settings)) {

  val insanePlugin = new InsanePlugin(this)

  object earlyLazyVals extends {
    final val FLAGS_PER_WORD = 32
    val global: PluginRunner.this.type = PluginRunner.this
    val runsAfter = List[String]("fake-erasure")
    val runsRightAfter = None
  } with LazyVals

  object fakeErasure extends {
    val global: PluginRunner.this.type = PluginRunner.this
    override val runsAfter = List[String]("explicitouter")
    override val runsRightAfter = None
    val phaseName = "fake-erasure"
  } with PluginComponent {

    def newPhase(prev: Phase): Phase = new StdPhase(prev) {
      override def erasedTypes: Boolean = true
      def apply(unit: CompilationUnit) { /* nothing */ }
    }
  }

  override protected def computeInternalPhases() {
    val phases = List(
      syntaxAnalyzer          -> "parse source into ASTs, perform simple desugaring",
      analyzer.namerFactory   -> "resolve names, attach symbols to named trees",
      analyzer.packageObjects -> "load package objects",
      analyzer.typerFactory   -> "the meat and potatoes: type the trees",
      superAccessors          -> "add super accessors in traits and nested classes",
      pickler                 -> "serialize symbol tables",
      refchecks               -> "reference/override checking, translate nested objects",
      uncurry                 -> "uncurry, translate function values to anonymous classes",
      tailCalls               -> "replace tail calls by jumps",
      specializeTypes         -> "@specialized-driven class and method specialization",
      explicitOuter           -> "this refs to outer pointers, translate patterns",
      fakeErasure             -> "simulate erasure, it's a bluff",
      earlyLazyVals           -> "allocate bitmaps, translate lazy vals into lazified defs",
      lambdaLift              -> "move nested functions to top level",
      constructors            -> "move field definitions into constructors"
  //    mixer                   -> "mixin composition"
    ).map(_._1) ::: insanePlugin.components

    for (phase <- phases) {
      phasesSet += phase
    }
  }
}
