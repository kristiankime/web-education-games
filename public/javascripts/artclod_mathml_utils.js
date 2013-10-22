if (!ARTC) {
    var ARTC = {};
}


/*
 * Duplicate of underscore code as not to import an entire library for one function.
 */
ARTC.isFunction = function(obj) {
    return !!(obj && obj.constructor && obj.call && obj.apply);
};

/*
 * Update an HTML element with a string in content MathML format and then have MathJax render it.
 * 
 * NOTE: This function will overwrite the inner HTML of an element.
 * 
 * This functional is designed not to throw any errors but to call a callback function
 * with a status object indicating success or failure. If no callback is given errors are ignored.
 * 
 * The error object has three attributes
 * 
 * success - a boolean indicating if everything went well
 * reason - a string which can be one of:
 *          "success" -> Indicating success, this is the only string where the success attribute will be true
 *          "id falsey" -> The id was falsey
 *          "mathMLStr falsey" -> The mathMLStr was falsey
 *          "parse error" -> The mathMLStr could not be parsed by ARTC.txt2MathML.parse 
 *          "invalid id" -> Could not find an element associated with the given id 
 * details - an empty string on success or an error object given more details about the failure
 */ 
ARTC.updateContentMathML = function(id, mathMLStr, callback) {
    var safeCallback = function() {/* noop */;};
    if(ARTC.isFunction(callback)){
	safeCallback = callback;
    }
    
    if (!id) {
	safeCallback({ success : false, reason : "id falsey", details : id });
	return;
    } else if (!mathMLStr) {
	safeCallback({ success : false, reason : "mathMLStr falsey", details : mathMLStr });
	return;
    }

    var mathML = null;
    try {
	mathML = ARTC.txt2MathML.parse(mathMLStr);
    } catch (e) {
	safeCallback({ success : false, reason : "parse error", details : e });
	return;
    }

    var elem = document.getElementById(id);
    if (elem === null) {
	safeCallback({ success : false, reason : "invalid id", details : id });
	return;
    }

    elem.innerHTML = '<script type="math\/mml"> <math> ' + mathML + ' <\/math> <\/script>';

    MathJax.Hub.queue.Push(
	    MathJax.Hub.Queue([ "Typeset", MathJax.Hub, id ]),
	    function(){ safeCallback({ success : true, reason : "success", details : "" }); }
	    );
};
