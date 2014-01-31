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
	return resultDocument;
};

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