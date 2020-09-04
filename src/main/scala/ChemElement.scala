import ChemElement.ChemElementWithData
import net.liftweb.json._

import scala.collection.mutable
import scala.io.Source

/**
 * @param elemString specifies the element, either by its full name or chemical symbol.
*/
case class ChemElement(private val elemString: String) {
  private val correspondingData = {
    if (ChemElement.symbolMap.contains(elemString)) {
      ChemElement.symbolMap(elemString)
    }
    else if (ChemElement.nameMap.contains(elemString)) {
      ChemElement.nameMap(elemString)
    }
    else if (ChemElement.nameMap.contains(ChemElement.properName(elemString))) {
      ChemElement.nameMap(ChemElement.properName(elemString))
    }
    else if (elemString == "placeholder") {
      ChemElementWithData(
        "placeholder", "placeholder", 0, 0, "placeholder", 0, "placeholder", 0, 0, "placeholder", 0, 0, "placeholder",
        "placeholder", "placeholder", "placeholder", "placeholder", 0, 0, List(0), "placeholder", "placeholder", 0,
        "placeholder", List(0)
      )
    }
    else {
      throw new RuntimeException(elemString + " is not a valid chemical element.")
    }
  }

  val name: String = correspondingData.name
  val symbol: String = correspondingData.symbol
  val atomicNumber: Int = correspondingData.number

  /** Returns the atomic mass. */
  val mass: Double = correspondingData.atomic_mass

  /** Returns the chemical symbol. */
  override def toString: String = symbol

  override def equals(that: Any): Boolean = that match {
    case e: ChemElement => correspondingData == e.correspondingData
    case _ => false
  }
}

object ChemElement {
  def properName(lowerName: String): String = lowerName.head.toUpper + lowerName.tail
  private val symbolMap = ChemElement.ChemElementWithData.symbolMap
  private val nameMap = ChemElement.ChemElementWithData.nameMap
  val placeHolderElement: ChemElement = ChemElement("placeholder")
  def isValidElem(input: String): Boolean = symbolMap.contains(input) || nameMap.contains(input) ||
    nameMap.contains(properName(input)) || input == "placeholder"

  private case class ChemElementWithData (
                                           name: String,
                                           appearance: String,
                                           atomic_mass: Double,
                                           boil: Double,
                                           category: String,
                                           density: Double,
                                           discovered_by: String,
                                           melt: Double,
                                           molar_heat: Double,
                                           named_by: String,
                                           number: Integer,
                                           period: Integer,
                                           phase: String,
                                           source: String,
                                           spectral_img: String,
                                           summary: String,
                                           symbol: String,
                                           xpos: Double,
                                           ypos: Double,
                                           shells: List[Int],
                                           electron_configuration: String,
                                           electron_configuration_semantic: String,
                                           electron_affinity: Double, // If data unavailable, assigned to zero
                                           electronegativity_pauling: String,
                                           ionization_energies: List[Double]
                                         )

  private object ChemElementWithData {
    implicit val formats: DefaultFormats.type = net.liftweb.json.DefaultFormats
    private val bufferedSource = Source.fromFile("ElementData.json")
    private val jsonElemContents: String = bufferedSource.getLines.mkString
    bufferedSource.close

    private val parsedjson = (parse(jsonElemContents) \\ "elements").children
    private val mutableSymbolMap = mutable.Map[String, ChemElementWithData]()
    private val mutableNameMap = mutable.Map[String, ChemElementWithData]()

    for (j <- parsedjson) {
      for (i <- j.children) {
        val m = i.extract[ChemElementWithData]
        mutableSymbolMap += (m.symbol -> m)
        mutableNameMap += (m.name -> m)
      }
    }

    val symbolMap: Map[String, ChemElementWithData] = mutableSymbolMap.toMap
    val nameMap: Map[String, ChemElementWithData] = mutableNameMap.toMap
  }
}

