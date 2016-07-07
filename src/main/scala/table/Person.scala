package table

import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.control.{TableCell, TableColumn}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle

class Person(firstName_ : String,
             lastName_ : String,
             favoriteColor_ : Color) {
  val firstName = new StringProperty(this, "firstName", firstName_)
  val lastName = new StringProperty(this, "lastName", lastName_)
  val favoriteColor = new ObjectProperty(this, "favoriteColor", favoriteColor_)


  new TableColumn[Person, String] {
    text = "First Name"
    // Cell value is loaded from a `Person` object
    cellValueFactory = { _.value.firstName }
  }

  new TableColumn[Person, Color] {
    text = "Favorite Color"
    // Cell value is loaded from a `Person` object
    cellValueFactory = { _.value.favoriteColor }
    // New circle is created when cell value changes
    cellFactory = { _ =>
      new TableCell[Person, Color] {
        item.onChange { (_, _, newColor) =>
          graphic = new Circle {
            fill = newColor;
            radius = 8
          }
        }
      }
    }
  }
}
