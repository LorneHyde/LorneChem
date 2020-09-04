import org.scalatest.flatspec._
import org.scalatest.matchers._

class ParserTest extends AnyFlatSpec with should.Matchers{
  "A molecular formula" should "correctly count each element" in {
    ParseToMap.parseInput("CH4") should equal (Map(ChemElement("C") -> 1, ChemElement("H") -> 4))
    ParseToMap.parseInput("O2") should equal (Map(ChemElement("O") -> 2))
    ParseToMap.parseInput("He") should equal (Map(ChemElement("He") -> 1))
    ParseToMap.parseInput("H2SO4") should equal {
      Map(ChemElement("H") -> 2, ChemElement("S") -> 1, ChemElement("O") -> 4)
    }
    ParseToMap.parseInput("CaCO3") should equal {
      Map(ChemElement("Ca") -> 1, ChemElement("C") -> 1, ChemElement("O") -> 3)
    }
    ParseToMap.parseInput("Ca(OH)2") should equal {
      Map(ChemElement("Ca") -> 1, ChemElement("H") -> 2, ChemElement("O") -> 2)
    }
  }

  "A structural formula" should "correctly count each element" in {
    ParseToMap.parseInput("CH3COOH") should equal {
      Map(ChemElement("C") -> 2, ChemElement("H") -> 4, ChemElement("O") -> 2)
    }
  }

  "A molecular formula" should "display correct HTML" in {
    ParseToHTML.parseInput("CH4") should be ("CH<sub>4</sub>")
    ParseToHTML.parseInput( "O2") should be ("O<sub>2</sub>")
    ParseToHTML.parseInput("H2SO4") should be ("H<sub>2</sub>SO<sub>4</sub>")
    ParseToHTML.parseInput("CaCO3") should be ("CaCO<sub>3</sub>")
    ParseToHTML.parseInput("Ca(OH)2") should be ("Ca(OH)<sub>2</sub>")
    ParseToHTML.parseInput("He") should be ("He")
  }

  "A structural formula" should "Display correct HTML" in {
    ParseToHTML.parseInput("CH3COOH") should be ("CH<sub>3</sub>COOH")
  }
}

