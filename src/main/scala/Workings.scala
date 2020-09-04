/** Contains functions to display "workings" for the user, to help them understand how given calculations were
 * carried out. */
object Workings {
  /** Returns a string explaining how the percentage by mass was calculated for the given element and compound. */
  def percentageByMass(element: ChemElement, compound: ChemFormula): String = {
    if (compound.contains(element)) {
      if (compound.isElement) {
        compound + " consists entirely of " + element + " atoms, so the percentage by mass must be 100%."
      }
      else {
        val elMass = element.mass
        val coMass = compound.mass
        val count = compound.count(element)
        val atomWord = if (count == 1) "atom" else "atoms"
        val massCount = elMass * count
        val proportion = massCount / coMass
        val unroundedPercentage = proportion * 100
        val sf = Settings.sigFig
        val roundedPercentage = ChemDisplayFuncs.roundSF(unroundedPercentage, sf)

        ("%s has relative atomic mass  %s and %s contains %d %s of %s.<br>" +
          "We multiply these together, to find that the total mass of %s in %s is %s.<br>" +
          "The formula mass of %s is %s.<br>" +
          "Dividing %s by the formula mass shows that the proportion of %s in %s by mass is %s.<br>" +
          "Multiplying this by 100 gives a percentage by mass of %s%%.<br>" +
          "Finally, we round this to %d significant figures, giving a final answer of<br>%s%%").format(
          element, elMass.toString, compound, count, atomWord, element, element, compound, massCount.toString, compound,
          removeTrailingZeroes(coMass), massCount.toString, element, compound, proportion.toString,
          unroundedPercentage.toString, sf, roundedPercentage
        )
      }
    }
    else {
      "%s does not contain %s so the percentage by mass must be 0%%.".format(compound, element)
    }
  }

  private def removeTrailingZeroes(num: Double): String = {
    ChemDisplayFuncs.roundSF(num, 7).reverse.dropWhile(_ == '0').reverse
  }
}
