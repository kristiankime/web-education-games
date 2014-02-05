if (!ARTC) {
    var ARTC = {};
}

ARTC.Status = function(success, reason, details, value) {
    this.success = success;
    this.reason = reason;
    this.details = details;
    this.value = value;
}

ARTC.statusTxt2MathML = function(mathMLStr) {
    var success = true;
    var reason = "";
    var details = null;
    var value = null;

    try {
	value = ARTC.txt2MathML.parse(mathMLStr);
    } catch (e) {
	success = false;
	reason = "parse error";
	details = e;
    }

    return new ARTC.Status(success, reason, details, value);
};

/*
 * Duplicate of underscore code so as not to import an entire library for one function.
 */
ARTC.isFunction = function(obj) {
    return !!(obj && obj.constructor && obj.call && obj.apply);
};

/*
 * Update an HTML element with a string in MathML format and then have MathJax render it.
 * 
 * NOTE: This function will overwrite the inner HTML of the specified element.
 * 
 * This function is designed not to throw any errors but to call a callback function with a status object indicating success or failure. If no callback is given errors are ignored.
 * 
 * The error object has three attributes
 * 
 * success - a boolean indicating if everything went well reason - a string which can be one of: "success" -> Indicating success, this is the only string where the success attribute will be true "id falsey" -> The id was falsey "mathMLStr falsey" -> The mathMLStr was falsey "invalid id" -> Could not find an element associated with the given id details - an empty string on success or an error object given more details about the failure
 */
ARTC.updateInnerMathML = function(id, mathML, callback) {
    var safeCallback = function() {/* noop */
	;
    };
    if (ARTC.isFunction(callback)) {
	safeCallback = callback;
    }

    if (!id) {
	safeCallback({
	    success : false,
	    reason : "id falsey",
	    details : id
	});
	return;
    } else if (!mathML) {
	safeCallback({
	    success : false,
	    reason : "mathMLStr falsey",
	    details : mathMLStr
	});
	return;
    }

    var elem = document.getElementById(id);
    if (elem === null) {
	safeCallback({
	    success : false,
	    reason : "invalid id",
	    details : id
	});
	return;
    }

    elem.innerHTML = '<script type="math\/mml"> ' + mathML + ' <\/script>';

    MathJax.Hub.queue.Push(MathJax.Hub.Queue([ "Typeset", MathJax.Hub, id ]), function() {
	safeCallback({
	    success : true,
	    reason : "success",
	    details : ""
	});
    });
};

ARTC.content2Presentation = function(xsltPath) {
    var a = $('span[mathml="content"]');
    var len = count = a.length;
    for (var index = 0; index < len; ++index) {

        (function(){
            var elem = a[index];
            var mathML = $.parseXML(elem.getAttribute("data-mathml"));
             
            ARTC.xslTransformLoadXSL(mathML, xsltPath, function(pmml) {
                elem.innerHTML = ARTC.xmlToString(pmml);
                
                if (--count == 0) {
                console.log("updating");
                MathJax.Hub.Queue([ "Typeset", MathJax.Hub ]);
                }
            });
            
        })();
	
    }
};
