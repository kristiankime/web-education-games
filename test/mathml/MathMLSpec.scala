package mathml

import scala.xml.XML
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

import org.specs2.matcher.Matcher
import scala.xml.Text


// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLSpec extends Specification {

	"transform" should {
		
		 """be able to parse simple plus (<apply> <plus/> <cn> 5 </cn> <cn> 5 </cn> </apply>) to MathML instance check""" in {
			 val mathML = MathML(<apply> <plus/> <cn> 5 </cn> <cn> 5 </cn> </apply>)
			 
			 mathML must beAnInstanceOf[Apply]
			 mathML.child(0) must beAnInstanceOf[Plus]
			 mathML.child(1) must beAnInstanceOf[Cn]
			 mathML.child(2) must beAnInstanceOf[Cn]
		 }
		 
		 """be able to parse simple plus (<apply> <plus/> <cn>5</cn> <cn>5</cn> </apply>) to MathML""" in {
			 val mathML = MathML(<apply> <plus/> <cn>5</cn> <cn>5</cn> </apply>)
			 mathML must beEqualTo(Apply(Plus(), Cn("5"), Cn("5")))
		 }
	}


}

object MathMLSpec {
		
	def main(args: Array[String]) {

//		val mathMLString = """<math xmlns="http://www.w3.org/1998/Math/MathML" display="block">
//  <mstyle>
//    <mrow class="MJX-TeXAtom-ORD">
//      <mfrac>
//        <mi>x</mi>
//        <mn>2</mn>
//      </mfrac>
//    </mrow>
//  </mstyle>
//</math>""";

		val mathMLString = """<apply> <plus/> <cn> 5 </cn> <cn> 5 </cn> </apply>"""
		val mathMLXML = XML.loadString(mathMLString);

		System.err.println(mathMLXML);
		
		
		val mathML = <apply> <plus/> <cn> 5 </cn> <cn> 5 </cn> </apply>
		System.err.println(mathML)
	}
}