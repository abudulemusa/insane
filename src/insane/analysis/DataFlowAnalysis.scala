package insane
package analysis

import CFG._
import utils.{Settings, Verbosity}

class DataFlowAnalysis[E <: DataFlowEnvAbs[E, S], S] (bottomEnv : E, baseEnv : E, settings: Settings) {
  type Vertex = VertexImp[S]

  var facts : Map[Vertex, E] = Map[Vertex,E]().withDefaultValue(bottomEnv)

  def pass(cfg: LabeledDirectedGraphImp[S], transferFun: TransferFunctionAbs[E,S]) = {
    for (v <- cfg.V) {
      for (e <- cfg.inEdges(v)) {
        // We ignore unreachable code
        if (facts(e.v1) != bottomEnv) {
          transferFun(e.lab, facts(e.v1))
        }
      }
    }
  }

  def detectUnreachable(cfg: LabeledDirectedGraphImp[S], transferFun: TransferFunctionAbs[E,S]): List[S] = {
    var res : List[S] = Nil;

    for (v <- cfg.V if v != cfg.entry) {
      if (cfg.inEdges(v).forall(e => (facts(e.v1) != bottomEnv) &&
                                     (transferFun(e.lab, facts(e.v1)) == bottomEnv))) {

        for (e <- cfg.outEdges(v)) {
          res = e.lab :: res
        }
      }
    }

    res
  }

  def computeFixpoint(cfg: LabeledDirectedGraphImp[S], transferFun: TransferFunctionAbs[E,S]) : Unit = {
    var pass = 0;

    if (settings.displayProgress) {
      println("    * Analyzing CFG ("+cfg.V.size+" vertices, "+cfg.E.size+" edges)")
    }

    facts = facts.updated(cfg.entry, baseEnv)

    var workList  = Set[Vertex]();

    for (e <- cfg.outEdges(cfg.entry)) {
      workList += e.v2
    }

    while (workList.size > 0) {
      pass += 1

      if (settings.displayProgress) {
        println("    * Pass "+pass+" ("+workList.size+" nodes in worklist)...")
      }

      val v = workList.head
      workList -= v

      val oldFact : E = facts(v)
      var newFact : Option[E] = None

      for (e <- cfg.inEdges(v) if facts(e.v1) != bottomEnv) {
        val propagated = transferFun(e.lab, facts(e.v1));

        if (propagated != bottomEnv) {
          newFact = newFact match {
            case Some(nf) => Some(nf union propagated)
            case None => Some(propagated)
          }
        }
      }

      val nf = newFact.getOrElse(oldFact.copy);

      if (nf != oldFact) {
        facts = facts.updated(v, nf)

        for (e <- cfg.outEdges(v)) {
          workList += e.v2;
        }
      }

    }
  }

  def dumpFacts = {
    for ((v,e) <- facts.toList.sortWith{(x,y) => x._1.name < y._1.name}) {
      println("  "+v+" => "+e)
    }
  }

  def getResult : Map[Vertex,E] = facts
}
