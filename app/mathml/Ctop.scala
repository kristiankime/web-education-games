package mathml

import play.api.Play
import play.api.Play.current

object Ctop {

	val str = io.Source.fromInputStream(Play.resourceAsStream("public/javascripts/ctop.xsl").get).mkString

}