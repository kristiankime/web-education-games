if(!ARTC){
    var ARTC = {};
}

ARTC.xslTransform = function(xml, xsl) {
	var resultDocument = null;
	if (window.ActiveXObject) { // code for IE
		ex = xml.transformNode(xsl);
	} else if (document.implementation && document.implementation.createDocument) { // code for Chrome, Firefox, Opera, etc.
		xsltProcessor = new XSLTProcessor();
		xsltProcessor.importStylesheet(xsl);
		resultDocument = xsltProcessor.transformToFragment(xml, document);
	}
//	console.log("xml");
//	console.log(xml);
//	console.log("xsl");
//	console.log(xsl);
//	console.log("resultDocument");
//	console.log(resultDocument);
	return resultDocument;
};


ARTC.xslTransformLoadXSL = function(xml, xslUrl, callback) {
    $.ajax({
	    type: "GET",
	    url: xslUrl,
	    dataType: "xml",
	    success: function(xsl) {
				callback(ARTC.xslTransform(xml, xsl));
			}
	   });
}

ARTC.xslTransformLoad = function(xmlUrl, xslUrl, callback) {
    var xml = null;
    var xsl = null;
    
    var callbackCheck = function() {
	if(xml != null && xsl != null){
	    callback(ARTC.xslTransform(xml, xsl));
	}
    }
    
    $.ajax({
    	    type: "GET",
    	    url: xmlUrl,
    	    dataType: "xml",
    	    success: function(data) {
    			xml = data;
    			callbackCheck()
    			}
    	   });

    $.ajax({
	    type: "GET",
	    url: xslUrl,
	    dataType: "xml",
	    success: function(data) {
			xsl = data
			callbackCheck()
			}
	   });
    
}

ARTC.xmlToString = function(xml) {
     if (window.ActiveXObject) { //code for IE
	 return xml.xml;
     } else { // code for Chrome, Safari, Firefox, Opera, etc.
	 return (new XMLSerializer()).serializeToString(xml);
     }
}