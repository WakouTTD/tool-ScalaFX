package tool.models

/**
 * Created by tateda on 15/05/16.
 */
case class KickAssConfig (
  val params:Seq[param]
)

case class param(
  val paramName:String,
  val filePath:String
)