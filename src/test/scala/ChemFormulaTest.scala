import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ChemFormulaTest extends AnyFlatSpec with should.Matchers{
  "A formula" should "have the correct mass" in {
    ChemFormula("CH4").mass should be (16.0 +- 0.05)
    ChemFormula("O2").mass should be (32.0 +- 0.05)
    ChemFormula("H2SO4").mass should be (98.1 +- 0.05)
    ChemFormula("CH3COOH").mass should be (60.1 +- 0.05)
  }

  "An element which does not appear in the formula" should "have a pbm of 0" in {
    ChemFormula("CH4").percentageByMass(ChemElement("O")) should be (0)
    ChemFormula("CH3COOH").percentageByMass(ChemElement("P")) should be (0)
  }

  "An element present in a chemical formula" should "give the correct percentage by mass" in {
    ChemFormula("CH4").percentageByMass(ChemElement("C")) should be (74.9 +- 0.05)
    ChemFormula("CH3COOH").percentageByMass(ChemElement("H")) should be (6.67 +- 0.05)
  }

  "A formula for an element" should "be correctly identified as such" in {
    ChemFormula("H2").isElement should be (true)
  }

  "A compound formula" should "not be considered as an element" in {
    ChemFormula("H2O").isElement should be (false)
  }

  "A chemical formula" should "only contain the elements present" in {
    ChemFormula("H2SO4").contains(ChemElement("H")) should be (true)
    ChemFormula("H2SO4").contains(ChemElement("He")) should be (false)
    ChemFormula("H2SO4").contains(ChemElement("hydrogen")) should be (true)
    ChemFormula("H2SO4").contains(ChemElement("placeholder")) should be (false)
  }

  "A chemical formula" should "correctly count each element present" in {
    ChemFormula("H2SO4").count(ChemElement("H")) should be (2)
    ChemFormula("H2SO4").count(ChemElement("S")) should be (1)
  }

  "A chemical formula" should "count 0 of any non-present element" in {
    ChemFormula("H2SO4").count(ChemElement("He")) should be (0)
  }

}

