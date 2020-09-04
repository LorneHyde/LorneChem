import net.liftweb.json._

import scala.collection.mutable
import scala.sys.process._

object EquationBalancer {

  /** Returns one list of coefficients for each of the chemical formulae in the reactants then products respectively. */
  def balance(reactants: List[ChemFormula], products: List[ChemFormula]): List[Int] = {
    //val pythonProg = "python C:\\Users\\lorni\\IdeaProjects\\RealChem\\src\\main\\resources\\nullspace.py"
    val pythonProg = "python nullspace.py"
    val presentElements = getAllElemsList(reactants, products)
    val inputMatrix = getJsonMatrix(reactants, products, presentElements)
    val result = (pythonProg + " \"" + inputMatrix + "\"").!!
    val jsonResult = parse(result)
    jsonResult.children.map(jsonToInt)
  }

  private def getJsonMatrix(reactants: List[ChemFormula], products: List[ChemFormula], equationElements: Set[ChemElement]) = {
    var diophantineSystem = "["
    var currentDiophantine = ""
    for (i <- equationElements) {
      currentDiophantine = "["
      for (r <- reactants) {
        currentDiophantine += nextNumberForDiophantine(i, r, is_reactant = false)
      }
      for (p <- products) {
        currentDiophantine += nextNumberForDiophantine(i, p, is_reactant = true)
      }
      currentDiophantine = endJsonList(currentDiophantine)
      diophantineSystem += currentDiophantine + ","
    }
    diophantineSystem = endJsonList(diophantineSystem)
    diophantineSystem //remove this return, and correctly replace it by calling python program.
  }

  private def nextNumberForDiophantine(elem: ChemElement, formula: ChemFormula, is_reactant: Boolean): String = {
    var next: String = ""
    if (formula.contains(elem) && is_reactant) {
      next += formula.count(elem)
    }
    else if (formula.contains(elem)) {
      next += "-" + formula.count(elem)
    }
    else {
      next += "0"
    }
    next += ","
    next
  }

  private def endJsonList(startList: String): String = {
    startList.dropRight(1) + "]"
  }

  /** Returns a list of all elements in the reactants.
   * If the elements in the reactants don't match those in the products, appropriate error messages are added to ErrorSet. */
  def getAllElemsList(reactants: List[ChemFormula], products: List[ChemFormula]): Set[ChemElement] = {
    val reactantElements = mutable.Set[ChemElement]()
    val productElements = mutable.Set[ChemElement]()
    for (i <- reactants; j <- i.constituentElems) {
      reactantElements += j
    }
    for (i <- products; j <- i.constituentElems) {
      productElements += j
    }

    val elemsToIgnore = Set[ChemElement](ChemElement.placeHolderElement)
    val finalReactants = reactantElements.toSet.diff(elemsToIgnore)
    val finalProducts = productElements.toSet.diff(elemsToIgnore)
    if (!finalReactants.subsetOf(finalProducts)) {
      ErrorSet.addSpareReactantMistake(finalReactants, finalProducts)
    }
    if (!finalReactants.subsetOf(finalProducts)) {
      ErrorSet.addSpareReactantMistake(finalReactants, finalProducts)
    }
    reactantElements.toSet
  }

  /** Returns the input parameter as an integer.
   *
   * @throws IllegalArgumentException if the input parameter is not a JInt. */
  def jsonToInt(jsonInt: JValue): Int = {
    jsonInt match {
      case JInt(x) => x.toInt
      case _ => throw new IllegalArgumentException(jsonInt + "is not an integer type.")
    }
  }
}
