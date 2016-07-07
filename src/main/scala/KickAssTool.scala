import java.util

import tool.models.{param, KickAssConfig}
import org.apache.commons.io.FileUtils

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, _}
import scalafx.scene.layout.{BorderPane, HBox, Pane, VBox}
import scalafx.scene.{Node, Scene}
import scala.sys.process.Process

object KickAssTool extends JFXApp {

  private val borderStyle = "" +
    "-fx-background-color: white;" +
    "-fx-border-color: black;" +
    "-fx-border-width: 1;" +
    "-fx-border-radius: 6;" +
    "-fx-padding: 6;"

  private val firstHeight = 80
  private val formatHeight = 50

  stage = new PrimaryStage {
    outer =>

    val panelsPane = new Pane() {
      val loginPanel = createLoginPanel()
      loginPanel.relocate(0, 0)

      //TODO confファイルを読み込んで下記の２つのパラメータを渡すようにしたい
      // ①ボタン名
      // ②実行するshell名
      val kickAssConfigs = readConf()

      def createPanels(configs: Seq[KickAssConfig]): Seq[Node] = {
        configs.map(v => v.params.map(x => createCustomTaskPanel(x.paramName,x.filePath))).flatten
      }

      val buttonPanels = createPanels(kickAssConfigs)
      (0 until buttonPanels.length).map(v => buttonPanels(v).relocate(0,( (v*formatHeight) + firstHeight) ))

      content = Seq(Seq(loginPanel),buttonPanels).flatten
      alignmentInParent = Pos.TOP_LEFT
    }

    title = "KickAssTool"
    scene = new Scene(350, 500) {
      root = new BorderPane() {
        padding = Insets(10)
        center = panelsPane
        bottom = new Button {
          text = "Click me to close the dialog"
          onAction = handle {
            outer.close()
            System.exit(0)
          }
        }
      }
    }
  }

  /**
   * 設定読み込み
   * @return
   */
  private def readConf(): Seq[KickAssConfig] = {
    val filesHere = (new java.io.File("./conf/")).listFiles
    filesHere.map(v =>  println("設定ファイル名:" + v))

    val someFilesLines = filesHere.map(f => FileUtils.readLines(f))
    javaList2AcalaSeq(someFilesLines).map(v => setTask(v))
  }


  def convert(listStr: util.List[String]): Seq[String] = {
    var hoge :String = ""
    for (j <- 0 until listStr.size()) {
      val tmpStr = listStr.get(j)
      if (tmpStr != null && tmpStr != "" && tmpStr.length > 0) {
        if(j == 0){
          hoge = tmpStr
        }else{
          hoge = hoge + "," + tmpStr
        }
      }
    }
    hoge.split(",")
  }

  /**
   * java から scala の世界へ
   * @param javaList
   * @return
   */
  def javaList2AcalaSeq(javaList :Array[java.util.List[String]]):Seq[String] = {
    (0 until javaList.length).map(i => convert(javaList(i))).flatten
  }

  def setTask(line :String): KickAssConfig = {
    val tmp = line.split('=')
    KickAssConfig(
      params = Seq(param(
        paramName = tmp(0),
        filePath = tmp(1)
      ))
    )
  }

  private def createLoginPanel(): Node = {
    val toggleGroup1 = new ToggleGroup()

    val textField = new TextField() {
      prefColumnCount = 10
      promptText = "Your name"
      tooltip = Tooltip("Your name")
    }

    val passwordField = new PasswordField() {
      prefColumnCount = 10
      promptText = "Your password"
      tooltip = Tooltip("Your password")
    }

    val choiceBox = new ChoiceBox[String](
      ObservableBuffer("Mac", "windows")) {
      tooltip = Tooltip("Your machine")
      selectionModel().select(0)
    }

    new HBox(6) {
      content = Seq(
        new VBox(2) {
          content = Seq(
            new RadioButton("Full") {
              toggleGroup = toggleGroup1
              selected = true
            },
            new RadioButton("Custom") {
              toggleGroup = toggleGroup1
              selected = false
            }
          )
        },
        new VBox(2) {
          content = Seq(textField, passwordField)
        },
        choiceBox
      )
      alignment = Pos.BOTTOM_LEFT
      style = borderStyle
    }
  }

  private def createCustomTaskPanel(taskName:String,path:String): Node = new HBox(6) {
    val acceptanceLabel = new Label(taskName)
    content = Seq(
      new Button("Accept") {
        onAction = handle {
          acceptanceLabel.text = "wait"
          acceptanceLabel.text = {
            println(Process("chmod +x " + path) !)
            println(Process(path) !)
            "finish!"
          }
        }
      },
      acceptanceLabel
    )
    alignment = Pos.CENTER_LEFT
    style = borderStyle
  }
}
