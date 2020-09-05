import ChemDisplayFuncs.wrapInHTML

import scala.swing._
import scala.swing.event._

protected class ChemGUIClass extends SimpleSwingApplication {

  private var userMistakes: String = ""

  /** The main menu */
  def top: MainFrame = new MainChemAppFrame {
    val EquationBalanceButton = new Button {
      text = "Equation Balancer"
    }

    val MassCalculatorButton = new Button {
      text = "Formula Mass Calculator"
    }

    val PBMButton = new Button {
      text = "Percentage by Mass Calculator"
    }

    contents = new FlowPanel {
      contents += EquationBalanceButton
      contents += MassCalculatorButton
      contents += PBMButton
    }

    listenTo(EquationBalanceButton)
    listenTo(MassCalculatorButton)
    listenTo(PBMButton)

    reactions += {
      case ButtonClicked(component) if component == EquationBalanceButton =>
        equationBalanceGui.visible = true
      case ButtonClicked(component) if component == MassCalculatorButton =>
        formulaMassGui.visible = true
      case ButtonClicked(component) if component == PBMButton =>
        pbmGUI.visible = true
    }

    override def closeOperation(): Unit = {
      visible = true
      quittingDialog
    }

    title = "LorneChem"
    size = new Dimension(800, 300)
  }

  /**A GUI for balancing chemical symbol equations*/
  def equationBalanceGui: ChemAppFrame = new ChemAppFrame {

    def beautifyBalancedEquation(reactants: List[ChemFormula], products: List[ChemFormula], coefficients: List[Int]): String = {
      val numReactants = reactants.size
      val n = coefficients.size
      var beautifulEquation = chemFormNumber(coefficients.head) + reactants.head
      for (i <- 1 until numReactants) {
        beautifulEquation += " + "
        beautifulEquation += chemFormNumber(coefficients(i)) + reactants(i)
      }
      beautifulEquation += " &rarr " // Arrow symbol in the middle
      beautifulEquation += chemFormNumber(coefficients(numReactants)) + products.head
      for (i <- numReactants + 1 until n) {
        beautifulEquation += " + "
        beautifulEquation += chemFormNumber(coefficients(i)) + products(i - numReactants)
      }
      beautifulEquation
    }

    def chemFormNumber(num: Int): String = {
      if (num == 1) ""
      else num.toString
    }

    val reactantsField = new TextField {
      columns = 20
    }
    val productsField = new TextField {
      columns = 20
    }

    val balanceButton = new Button {
      text = "Balance"
    }

    val instructions = new Label("Write all reactants separated by commas in the \"Reactants\" field, " +
      "and all products separated by commas in the \"Products\" field.")
    val arrowLabel = new Label("<html>&rarr;</html>")
    val reactantsLabel = new Label("Reactants:")
    val productsLabel = new Label("Products:")

    val balancedLabel = new SafeLabelFromButton {
      text = "Balanced Equation will appear here."

      override def promptButton: Button = balanceButton

      override def findErrors: Unit = {
        if (reactantsField.text == "") {
          ErrorSet.addBlankField("reactants")
        }
        if (productsField.text == "") {
          ErrorSet.addBlankField("products")
        }
        val potentialReactantList = ParseFormulaeList.parse(reactantsField.text)
        val potentialProductList = ParseFormulaeList.parse(productsField.text)
        val involvedReactants = potentialReactantList.diff(potentialProductList)
        val involvedProducts = potentialProductList.diff(potentialReactantList)
        if (!(reactantsField.text == "" || productsField.text == "")) {
          EquationBalancer.getAllElemsList(involvedReactants, involvedProducts)
        }
        if (involvedReactants.isEmpty && involvedProducts.isEmpty) {
          ErrorSet.addRedundantReaction()
        }
        if (!ErrorSet.hasMistakes) {
          val coefficients = EquationBalancer.balance(involvedReactants, involvedProducts)
          if (coefficients.forall(_.equals(0))) {
            ErrorSet.addImpossibleEquation()
          }
        }
      }

      override def successfulResponse: Unit = {
        val potentialReactants = ParseFormulaeList.parse(reactantsField.text)
        val potentialProducts = ParseFormulaeList.parse(productsField.text)
        val involvedReactants = potentialReactants.diff(potentialProducts)
        val involvedProducts = potentialProducts.diff(potentialReactants)
        val coefficients = EquationBalancer.balance(involvedReactants, involvedProducts)
        text = wrapInHTML(beautifyBalancedEquation(involvedReactants, involvedProducts, coefficients))
      }

    }

    val reactantPanel = new GridPanel(2, 1) {
      contents += reactantsLabel
      contents += reactantsField
    }
    val productPanel = new GridPanel(2, 1) {
      contents += productsLabel
      contents += productsField
    }
    val interactives = new FlowPanel {

      contents += reactantPanel
      contents += arrowLabel
      contents += productPanel
      contents += balanceButton
      contents += balancedLabel
    }

    contents = new GridPanel(2, 1) {
      contents += instructions
      contents += interactives
    }

    title = "Equation Balancer"
    size = new Dimension(800, 300)
  }

  /** A gui to calculate formula mass */
  def formulaMassGui: ChemAppFrame = new ChemAppFrame {

    contents = new FlowPanel {
      val formula = new TextField {
        columns = 10
      }
      val calculateButton = new Button {
        text = "Calculate"
      }

      val massLabel = new SafeLabelFromButton {
        override def promptButton = calculateButton

        text = "Mass will appear here."

        override def findErrors: Unit = {
          if (formula.text == "") {
            ErrorSet.addBlankField("formula")
          }
          ParseToMap.parseInput(formula.text)
        }

        override def successfulResponse: Unit = {
          val mass: Double = ChemFormula(formula.text).mass
          text = wrapInHTML(ChemFormula(formula.text) + " has formula mass " + ChemDisplayFuncs.roundSF(mass, Settings.sigFig))
        }
      }
      contents += formula
      contents += calculateButton
      contents += massLabel
    }

    title = "Formula Mass Calculator"
    size = new Dimension(400, 200) //TODO: Find some way to give dynamic sizing, or perhaps a scroll bar
  }

  /** A gui to calculate percentage by mass */
  def pbmGUI: ChemAppFrame = new ChemAppFrame {
    contents = new FlowPanel {
      val compoundField = new TextField {
        columns = 10
      }
      val elementField = new TextField {
        columns = 10
      }
      val calculateButton = new Button {
        text = "Calculate"
      }

      val workingsCheckBox = new CheckBox("Show workings on next calculation?")

      val elementPanel = new GridPanel(2, 1) {
        contents += new Label("Element")
        contents += elementField
      }

      val compoundPanel = new GridPanel(2, 1) {
        contents += new Label("Compound")
        contents += compoundField
      }

      val percentageLabel = new SafeLabelFromButtonOptWorkings {
        override def workingsCheck = workingsCheckBox

        override def promptButton = calculateButton

        text = "Press the button to calculate the percentage by mass."

        override def findErrors: Unit = {
          if (compoundField.text == "") {
            ErrorSet.addBlankField("compound")
          }
          if (elementField.text == "") {
            ErrorSet.addBlankField("element")
          }
          else if (!ChemElement.isValidElem(elementField.text)) {
            ErrorSet.addInvalidElement(elementField.text)
          }
          ParseToMap.parseInput(compoundField.text)
        }

        override def withWorkings: String = {
          val element = ChemElement(elementField.text)
          val compound = ChemFormula(compoundField.text)
          wrapInHTML(Workings.percentageByMass(element, compound))
        }
        override def withoutWorkings: String = {
          val element = ChemElement(elementField.text)
          val compound = ChemFormula(compoundField.text)
          val percentageByMass: Double = compound.percentageByMass(element)
          val roundedPBM: String = ChemDisplayFuncs.roundSF(percentageByMass, Settings.sigFig)
          wrapInHTML("The percentage by mass of %s in %s is %s%%".format(element.toString, compound, roundedPBM))
        }
      }
      contents += elementPanel
      contents += compoundPanel
      contents += workingsCheckBox
      contents += calculateButton
      contents += percentageLabel
    }

    title = "Percentage by Mass Calculator"
  }

  def settingsGUI: Frame = new Frame {
    val SFLabel = new Label("Significant figures in answers")
    var SFDropDown = new ComboBox[String](Seq(Settings.getSigFigString, "1 (one)", "2 (two)", "3 (three)", "4 (four)"))

    val saveButton = new Button("Apply all and close")
    val cancelButton = new Button("Cancel")
    val restoreButton = new Button("Restore Defaults")

    val SFPanel = new GridPanel(2, 1) {
      contents += SFLabel
      contents += SFDropDown
    }

    def allSettings = new FlowPanel {
      contents += SFPanel
    }

    def buttons = new GridPanel(1, 3) {
      contents += saveButton
      contents += cancelButton
      contents += restoreButton
    }

    contents = new BorderPanel {
      add(allSettings, BorderPanel.Position.Center)
      add(buttons, BorderPanel.Position.South)
    }

    listenTo(saveButton)
    listenTo(cancelButton)
    listenTo(restoreButton)
    reactions += {
      case ButtonClicked(component) if component == saveButton =>
        Settings.update(newSigFigString = SFDropDown.item)
        close()
      case ButtonClicked(component: Component) if component == cancelButton =>
        close()
      case ButtonClicked(component: Component) if component == restoreButton =>
        Settings.restoreDefaults
        SFDropDown = new ComboBox[String](Seq(Settings.getSigFigString, "1 (one)", "2 (two)", "3 (three)", "4 (four)"))
        close()
    }
    title = "Settings"
  }


  /** The basis behind all GUIs in this application. */
  trait ChemAppFrame extends Frame {

    def mistakesDialog {
      beep
      userMistakes = ErrorSet.mistakes.mkString("<br>")
      Dialog.showMessage(contents.head, wrapInHTML("Your input has the following errors:<br>" + userMistakes))
    }

    def defaultMenuBar: MenuBar = new MenuBar {

      contents += new Menu("File") {
        contents += new MenuItem(Action("Close all windows") {
          quittingDialog
        })
        contents += new MenuItem(Action("Settings") {
          settingsGUI.visible = true
        })
      }

      contents += new Menu("Applications") {
        contents += new MenuItem(Action("Formula Mass Calculator") {
          formulaMassGui.visible = true
        })
        contents += new MenuItem(Action("Equation Balancer") {
          equationBalanceGui.visible = true
        })
        contents += new MenuItem(Action("Percentage by Mass Calculator") {
          pbmGUI.visible = true
        })
      }

    }

    def quittingDialog {
      beep
      val res = Dialog.showConfirmation(contents.head,
        "Do you really want to quit?",
        optionType = Dialog.Options.YesNo,
        title = "")
      if (res == Dialog.Result.Ok) {
        Settings.saveToFile
        sys.exit(0)
      }

    }

    /** Makes a noise to alert user of error. */
    def beep: Unit = java.awt.Toolkit.getDefaultToolkit.beep()

    menuBar = defaultMenuBar

    /** A label that should only be updated if there are no user input mistakes (otherwise, a mistakes dialog will be shown). */
    trait SafeLabel extends Label {
      /** If there are user-input mistakes, notifies the user. Otherwise, updates the label. */
      def respondSafely {
        ErrorSet.reset
        findErrors
        if (ErrorSet.hasMistakes) {
          mistakesDialog
        }
        else {
          successfulResponse
        }
      }

      /** Runs code to identify mistakes with input, and adds these to ErrorSet. */
      def findErrors: Unit

      /** The code that should be run to update the label if there are no errors. */
      def successfulResponse: Unit
    }

    /** A SafeLabel that responds to a button press. */
    trait SafeLabelFromButton extends SafeLabel {
      /** The SafeLabel will be updated when this button is clicked. */
      def promptButton: Button

      listenTo(promptButton)
      reactions += {
        case ButtonClicked(component) if component == promptButton =>
          respondSafely
      }
    }

    /** A label that should only be updated if there are no user input mistakes (otherwise, a mistakes dialog will be shown).
     * If the given tickbox is checked, an explanation will be shown alongside the answer. Otherwise the answer will be
     * shown with no explanation. */
    trait SafeLabelOptWorkings extends Label {

      /** This checkbox should be ticked if we want the workings to be shown, and left unticked if only
       * the answer should be shown. */
      def workingsCheck: CheckBox

      /** If there are user-input mistakes, notifies the user. Otherwise, updates the label. */
      def respondSafely {
        ErrorSet.reset
        findErrors
        if (ErrorSet.hasMistakes) {
          mistakesDialog
        }
        else {
          if (workingsCheck.selected) text = withWorkings
          else text = withoutWorkings
        }
      }

      /** Runs code to identify mistakes with input, and adds these to ErrorSet. */
      def findErrors: Unit

      /** Returns the answer for the user, with explanation of how it was achieved. */
      def withWorkings: String

      /** Returns the answer for the user, with no explanation of how it was achieved. */
      def withoutWorkings: String
    }

    /** A SafeLabelOptWorkings that responds to a button press. */
    trait SafeLabelFromButtonOptWorkings extends SafeLabelOptWorkings {
      /** The SafeLabel will be updated when this button is clicked. */
      def promptButton: Button

      listenTo(promptButton)
      reactions += {
        case ButtonClicked(component) if component == promptButton =>
          respondSafely
      }
    }

  }

  private class MainChemAppFrame extends MainFrame with ChemAppFrame


}

object ChemGUI extends ChemGUIClass {
  override def main(args: Array[String]) {
    val ui = new ChemGUIClass
    ui.top.visible = true
  }
}