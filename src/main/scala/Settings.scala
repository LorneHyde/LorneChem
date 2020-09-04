import java.io.{File, PrintWriter}

import scala.io.Source

/** An object containing all of the user-defined settings parameters for the program. */
object Settings {

  //Read in from file
  private val bufferedSource = Source.fromFile("settings.txt")
  private val fileLinesIterator = bufferedSource.getLines
  private var sigFigString: String = fileLinesIterator.nextOption.get
  bufferedSource.close


  //Getters

  /** @return the significant figures setting as a string.
   * This will include the number written out as a word.
   * For example, "3 (three)"
   * */
  def getSigFigString: String = sigFigString

  /** Returns the significant figures setting as an integer. */
  def sigFig: Int = sigFigString.charAt(0).asDigit

  // Other functions

  /** Saves the settings to file. This should be called when the program is closed.*/
  def saveToFile: Unit = {
    val pw = new PrintWriter(new File("settings.txt" ))
    pw.write(sigFigString)
    pw.close
  }

  /** Restores the settings to their default values. */
  def restoreDefaults: Unit = {
    sigFigString = "3 (three)"
  }

  /** Updates the settings object, with the new parameters given */
  def update(newSigFigString: String = "3 (three)"): Unit = {
    sigFigString = newSigFigString
  }
}
