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
// with thanks to http://www.ictforu.com/index.php/Core-Java/java-xslt.html
@RunWith(classOf[JUnitRunner])
class Content2PresentationSpec extends Specification {

	"apply" should {

		"parse 2 + 2" in {
			val content = MathML(<math xmlns="http://www.w3.org/1998/Math/MathML"> <apply> <plus/> <cn> 2 </cn> <cn> 2 </cn> </apply> </math>).get
			val presentation = <math xmlns="http://www.w3.org/1998/Math/MathML"><m:mrow xmlns:m="http://www.w3.org/1998/Math/MathML"><m:mn>2</m:mn><m:mo>+</m:mo><m:mn>2</m:mn></m:mrow></math>
			Content2Presentation(content) must beEqualTo(presentation)
		}

	}

}