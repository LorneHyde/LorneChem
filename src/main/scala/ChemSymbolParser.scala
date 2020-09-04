import scalaz.Scalaz._

import scala.util.parsing.combinator._

/** A trait for parser classes that only have one public parser, which is intended to be pass/fail (rather than parsing only part of the input). */
trait TotalParser[T] extends RegexParsers {
  def mainParser: Parser[T]

  /** Parses the entire input string, and returns the fully parsed output as a non-parser type. */
  def parseInput(input: String): T = {
    this.parseAll(mainParser, input).get
  }
}

/** Provides parsers for an element and a number respectively. */
trait ChemSymbolParser extends RegexParsers {
  final protected val elemString: Parser[String] = """[A-Z][a-z]{0,2}""".r
  final protected val numString: Parser[String] = """[0-9]+""".r
}

/** Provides a parser to add appropriate HTML subscript tags for subscript within a chemical formula string */
object ParseToHTML extends ChemSymbolParser with TotalParser[String] {
  def mainParser: Parser[String] = {
    require(!ErrorSet.hasMistakes)
    rep(optBracketedSegment) ^^ (a => "" + a.mkString + "")
  }

  private def prettyNum: Parser[String] = numString ^^ (a => "<sub>" + a + "</sub>")

  private def basicFormulaSegment: Parser[String] = elemString ~ opt(prettyNum) ^^ {
    case a ~ None => a
    case a ~ Some(b) => a + b
  }

  private def basicPrettyFormula: Parser[String] = rep(basicFormulaSegment) ^^ (a => "" + a.mkString + "")

  private def bracketedSegment: Parser[String] = "(" ~ basicPrettyFormula ~ ")" ~ opt(prettyNum) ^^ {
    case openbracket ~ a ~ closebracket ~ None => openbracket + a + closebracket
    case openbracket ~ a ~ closebracket ~ Some(b) => openbracket + a + closebracket + b
  }

  private def optBracketedSegment: Parser[String] = basicFormulaSegment | bracketedSegment
}

/** Provides a parser to convert a chemical formula string into a map showing the number of each constituent element in the formula. */
object ParseToMap extends ChemSymbolParser with TotalParser[Map[ChemElement, Int]] {
  private val element: Parser[ChemElement] = elemString ^^ {
    safeElement
  }
  private val subscrNum: Parser[Int] = numString ^^ (_.toInt)

  def mainParser: Parser[Map[ChemElement, Int]] = rep(optBrackets) ^^ (a => sumOfMaps(a))

  override def parseInput(input: String): Map[ChemElement, Int] = {
    try {
      val answer = super.parseInput(input)
      ErrorSet.mergePotentialMistakes()
      answer
    }
    catch {
      case re: RuntimeException if re.getMessage == "No result when parsing failed" =>
        ErrorSet.addInvalidFormula(input)
        Map(ChemElement.placeHolderElement -> 1)
    }
    finally {
      ErrorSet.removeUnnecessaryWarnings()
    }
  }

  private def safeElement(input: String) = {
    if (ChemElement.isValidElem(input)) {
      ChemElement(input)
    }
    else {
      ErrorSet.addPotentialInvalidElem(input)
      ChemElement.placeHolderElement
    }
  }

  private def optSubscrNum: Parser[Int] = opt(subscrNum) ^^ {
    case None => 1 // No number means 1 of that element
    case Some(x) => x
  }

  private def elemCount: Parser[Map[ChemElement, Int]] = element ~ optSubscrNum ^^ {
    case a ~ b =>
      Map[ChemElement, Int](a -> b)
  }

  private def sumOfMaps(maplist: List[Map[ChemElement, Int]]): Map[ChemElement, Int] = {
    maplist match {
      case List() => Map[ChemElement, Int]()
      case a :: bs => a |+| sumOfMaps(bs)
    }
  }

  private def basicFormulaCount: Parser[Map[ChemElement, Int]] = elemCount ~ rep(elemCount) ^^ {
    case a ~ List() => a
    case a ~ bs => sumOfMaps(a :: bs)
  }

  private def bracketElemCount: Parser[Map[ChemElement, Int]] = "(" ~ basicFormulaCount ~ ")" ~ opt(subscrNum) ^^ {
    case _ ~ a ~ _ ~ None => a
    case _ ~ a ~ _ ~ Some(b) => a.view.mapValues(x => x * b).toMap
  }

  private def optBrackets: Parser[Map[ChemElement, Int]] = elemCount | bracketElemCount
}

/** Parses a string of formulae separated by commas or plus signs, to a list of formulae. */
object ParseFormulaeList {
  def parse(formulaeString: String): List[ChemFormula] = {
    val strippedString = removeLeadingOrTrailingSeparators(formulaeString.replaceAll("\\s", ""))
    val stringFormulae = if (strippedString.contains(',')) {
      strippedString.split(",")
    }
    else {
      strippedString.split("\\+")
    }
    stringFormulae.map(ChemFormula).toList
  }

  private def removeLeadingOrTrailingSeparators(strippedString: String): String = {
    var newString = strippedString
    if (newString.nonEmpty) {
      while (newString.last == ',' || newString.last == '+') {
        newString = newString.init
      }
      while (newString.head == ',' || newString.head == '+') {
        newString = newString.tail
      }
    }
    newString
  }
}

//TODO: write test for ZnCO3 -> ZnO + Zn2C
