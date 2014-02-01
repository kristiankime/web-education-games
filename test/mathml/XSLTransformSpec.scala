package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.trig._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class XSLTransformSpec extends Specification {

	"apply" should {

		"use the xsl to translate the xml" in {
			XSLTransform(xml, xsl).get must beEqualTo(res)
		}

		"fail if the xsl is not a valid xsl" in {
			XSLTransform(xml, "this is not a valid xsl").isFailure must beTrue
		}
		
		"fail if the xml is not valid xml" in {
			XSLTransform("not valid xml", xsl).isFailure must beTrue
		}
	}

	val xml =
		<language>
			<name>Kannada</name>
			<region>Karnataka</region>
			<users>38M</users>
			<family>Dravidian</family>
		</language>

	val xsl =
		"""<?xml version="1.0" encoding="ISO-8859-1"?>
		<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<xsl:template match="/">
				<html>
					<body>
						<h1>Indian Languages details</h1>
						<table border="1">
							<tr>
								<th>Language</th>
								<th>Family/Origin</th>
								<th>No. of speakers</th>
								<th>Region</th>
							</tr>
							<xsl:for-each select="language-list/language">
								<tr>
									<td><xsl:value-of select="name"/></td>
									<td><xsl:value-of select="family"/></td>
									<td><xsl:value-of select="users"/></td>
									<td><xsl:value-of select="region"/></td>
								</tr>
							</xsl:for-each>
						</table>
					</body>
				</html>
			</xsl:template>
		</xsl:stylesheet>"""

		// Sadly this is the correct formatting (there is no space before each line and between each item)
		val res = <html>
<body>
<h1>Indian Languages details</h1>
<table border="1">
<tr>
<th>Language</th><th>Family/Origin</th><th>No. of speakers</th><th>Region</th>
</tr>
</table>
</body>
</html>

}