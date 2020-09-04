import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import EquationBalancer.balance

class EquationBalanceTest extends AnyFlatSpec with should.Matchers {
  "A simple equation" should "get correctly balanced" in {
    balance(List(ChemFormula("CaCO3")), List(ChemFormula("CaO"), ChemFormula("CO2"))) should be(List(1, 1, 1))
    balance(List(ChemFormula("C5H12"), ChemFormula("O2")), List(ChemFormula("CO2"), ChemFormula("H2O"))) should be(List(1, 8, 5, 6))
    balance(List(ChemFormula("Al2(CO3)3"), ChemFormula("H3PO4")), List(ChemFormula("AlPO4"), ChemFormula("CO2"), ChemFormula("H2O"))) should be (List(1,2,2,3,3))
  }

  "An equation involving display formula" should "get correctly balanced" in {
    balance(List(ChemFormula("CH3COOH"), ChemFormula("Na2CO3")), List(ChemFormula("CH3COONa"), ChemFormula("CO2"), ChemFormula("H2O"))) should be(List(2,1,2,1,1))
  }
}
