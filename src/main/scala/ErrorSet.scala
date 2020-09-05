import scala.collection.mutable

/** The set of user input mistakes found so far. */
object ErrorSet {
  private var mistakesSoFar: collection.mutable.Set[String] = mutable.Set()
  private var potentialMistakes: collection.mutable.Set[String] = mutable.Set()

  /** Adds an error message for an invalid element to a temporary set, which may later be merged into the error set
   * if it is found to be a genuine mistake.
   * @param invalid_string the invalid string that may be a typo of a chemical element in the periodic table */
  def addPotentialInvalidElem(invalid_string: String): Unit = {
    potentialMistakes.add("\"" + invalid_string + "\" is not a valid element of the periodic table.")
  }

  /** Merges the error messages from the temporary set of potential mistakes into the error set of definite
   * mistakes. Should only be called if there is no other explanation for the supposed mistakes in the temporary set */
  def mergePotentialMistakes(): Unit = {
    mistakesSoFar = mistakesSoFar.union(potentialMistakes)
    potentialMistakes = mutable.Set()
  }

  /** Empties the temporary set of potential mistakes. Should only be called if another explanation is found for these
   * potential mistakes such that reporting them would be redundant. */
  def removeUnnecessaryWarnings(): Unit = {
    potentialMistakes = mutable.Set()
  }

  /** Deletes all elements of the set. */
  def reset(): Unit = {
    mistakesSoFar = mutable.Set()
  }

  def hasMistakes: Boolean = mistakes.nonEmpty

  def mistakes: List[String] = mistakesSoFar.toList

  def addInvalidFormula(invalid_formula: String): Unit = {
    mistakesSoFar += ("\"" + invalid_formula + "\" is not a valid chemical formula.")
  }

  def addSpareReactantMistake(reactants: Set[ChemElement], products: Set[ChemElement]): Unit = {
    mistakesSoFar += elemDifferenceList(reactants, products) + " in the reactants but not the products."
  }

  private def elemDifferenceList(setWithExcess: Set[ChemElement], otherSet: Set[ChemElement]): String = {
    val excess = setWithExcess.diff(otherSet)
    require(excess.nonEmpty)
    if (excess.size == 1) {
      "Element " + excess.head + " is "
    }
    else {
      "Elements " + grammaticalString(excess) + " are"
    }
  }

  private def grammaticalString(elemSet: Set[ChemElement]): String = {
    elemSet.init.mkString(", ") + ", and " + elemSet.last
  }

  def addSpareProductMistake(reactants: Set[ChemElement], products: Set[ChemElement]): Unit = {
    mistakesSoFar += elemDifferenceList(products, reactants) + " in the products but not the reactants."
  }

  def addBlankField(fieldName: String): Unit = {
    mistakesSoFar += "The \"" + fieldName + "\" field is blank."
  }

  def addInvalidElement(invalid_element: String): Unit = {
    mistakesSoFar += "\"" + invalid_element + "\" is not a valid element of the periodic table."
  }

  def addImpossibleEquation(): Unit = {
    mistakesSoFar += "The equation given is mathematically impossible, and cannot be balanced."
  }

  def addRedundantReaction(): Unit = {
    mistakesSoFar += "The products are the same as the reactants, so no reaction actually occurs."
  }
}
