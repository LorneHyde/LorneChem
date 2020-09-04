/**
 * @param formulaString may represent a structural formula or molecular formula, provided as a string
 *                      For example, "CH3COOH" and "C2H4O2" are both valid.
 */
case class ChemFormula(formulaString: String) {
  /** Returns an iterable containing all elements found in the formula. */
  private val elementMap = ParseToMap.parseInput(formulaString)
  val constituentElems: Iterable[ChemElement] = elementMap.keys
  /** Returns the (not rounded) formula mass. */
  val mass: Double = {
    var runningSum: Double = 0
    for ((k, v) <- elementMap) {
      runningSum += k.mass * v
    }
    runningSum
  }
  val isElement: Boolean = elementMap.size == 1


  /** Returns the (unrounded) percentage by mass of the given element parameter. */
  def percentageByMass(element: ChemElement): Double = {
    val pbm = if (contains(element)) {
      100 * count(element) * element.mass / mass
    }
    else {
      0
    }
    pbm
  }

  def contains(element: ChemElement): Boolean = {
    elementMap.contains(element)
  }

  /** Returns the number of times the parameter element appears in the formula. */
  def count(element: ChemElement): Int = {
    if (elementMap.contains(element)) {
      elementMap(element)
    }
    else 0
  }

  override def toString: String = ParseToHTML.parseInput(formulaString)
}
