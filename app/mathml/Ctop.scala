package mathml

object Ctop {
	// LATER can this be pulled from the public/javascript directory?
	val str = io.Source.fromInputStream(getClass.getResourceAsStream("ctop.xsl")).mkString
}