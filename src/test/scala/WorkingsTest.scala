import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class WorkingsTest extends AnyFlatSpec with should.Matchers {
  "The percentage by mass workings" should "be correct" in {
    Workings.percentageByMass(ChemElement("O"), ChemFormula("H2SO4")) should be ("O has relative atomic mass  15.999 " +
      "and H<sub>2</sub>SO<sub>4</sub> contains 4 atoms of O.<br>We multiply these together, to find that the total " +
      "mass of O in H<sub>2</sub>SO<sub>4</sub> is 63.996.<br>The formula mass of H<sub>2</sub>SO<sub>4</sub> is " +
      "98.072.<br>Dividing 63.996 by the formula mass shows that the proportion of O in H<sub>2</sub>SO<sub>4</sub> " +
      "by mass is 0.652540990292846.<br>Multiplying this by 100 gives a percentage by mass of 65.25409902928459%." +
      "<br>Finally, we round this to 3 significant figures, giving a final answer of<br>65.3%")
  }
}