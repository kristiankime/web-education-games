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
import models.DBTest.inMemH2

import xml.transform.{ RuleTransformer, RewriteRule }
import xml.{ NodeSeq, Node, Elem }
import xml.Utility.trim

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
// with thanks to http://www.ictforu.com/index.php/Core-Java/java-xslt.html
@RunWith(classOf[JUnitRunner])
class Content2PresentationSpec extends Specification {
	val removeEmptyRule = new RuleTransformer(new RemoveEmptyTagsRule)

	"RemoveEmptyTagsRule" should {
		"leave non empty nodes alone" in {
			val before = <xml><foo>bar</foo><nonEmpty>data</nonEmpty></xml>
			val after = <xml><foo>bar</foo><nonEmpty>data</nonEmpty></xml>

			removeEmptyRule.transform(before) must beEqualTo(after)
		}

		"remove <empty/> node" in {
			val before = <xml><empty/><nonEmpty>data</nonEmpty></xml>
			val after = <xml><nonEmpty>data</nonEmpty></xml>

			removeEmptyRule.transform(before) must beEqualTo(after)
		}

		"leave a node with an attribute" in {
			val before = <xml><nonEmpty name="legend"></nonEmpty><empty/></xml>
			val after = <xml><nonEmpty name="legend"></nonEmpty></xml>

			removeEmptyRule.transform(before) must beEqualTo(after)
		}

		"remove <Empty><empty/></Empty> aka nested empty nodes" in {
			val before = <xml><Empty><empty/></Empty><nonEmpty>data</nonEmpty></xml>
			val after = <xml><nonEmpty>data</nonEmpty></xml>

			removeEmptyRule.transform(before) must beEqualTo(after)
		}

		"remove nodes with spaces" in {
			val before = <xml><empty>
     </empty><nonEmpty>data</nonEmpty></xml>
			val after = <xml><nonEmpty>data</nonEmpty></xml>

			removeEmptyRule.transform(before) must beEqualTo(after)
		}

		"remove nodes with �" in {
			val before = <math><m:msup><m:mrow><m:mi>sin</m:mi><m:mo>���</m:mo><m:mo>(</m:mo><m:msup><m:mrow><m:mi>x</m:mi></m:mrow><m:mn>3</m:mn></m:msup><m:mo>)</m:mo></m:mrow><m:mn>3</m:mn></m:msup></math>
			val after = <math><m:msup><m:mrow><m:mi>sin</m:mi><m:mo>(</m:mo><m:msup><m:mrow><m:mi>x</m:mi></m:mrow><m:mn>3</m:mn></m:msup><m:mo>)</m:mo></m:mrow><m:mn>3</m:mn></m:msup></math>

			removeEmptyRule.transform(before) must beEqualTo(after)
		}
	}

}