if (!ARTC) {
    var ARTC = {};
}

if(!ARTC.mathJax) {
    ARTC.mathJax = {};
}

/*
 * Wrapper functions for "math" values that are put into ARTC.mathJax.update
 */
ARTC.mathJax.tex = function(tex){ return '$$' + tex + '$$'; };
ARTC.mathJax.asccii = function(asciimath){ return '`' + asciimath + '`'; };
ARTC.mathJax.mathml = function(mathML){ return '<script type="math\/mml"> ' + mathML + ' <\/script>'; };


ARTC.mathJax.update = (function() {
    // Duplicate of underscore code so as not to import an entire library for one function.
    var isFunction = function(obj) { return !!(obj && obj.constructor && obj.call && obj.apply); };

    /*
     * Update an HTML element with a string in content MathML format and then have MathJax render it.
     *
     * NOTE: This function will overwrite the inner HTML of an element.
     *
     * This functional is designed not to throw any errors but to call a callback function
     * with a status object indicating success or failure. If no callback is given errors are ignored.
     *
     * Arguments
     *   id - The id of the element to update
     *   math - The Math to put into the element. this will replace the innerHTML of the element so it must
     *          be something that MathJax can process.
     *          For convenience ARTC.mathJax.tex will wrap TeX properly,
     *          ARTC.mathJax.asccii will wrap AsciiMath
     *          ARTC.mathJax.mathml will wrap MathML (Presentation or Content)
     *   callback - function that will be called after the updated completes, it is called with finished object
     *
     * The finished object has three attributes
     *
     * success - a boolean indicating if everything went well
     * reason - a string which can be one of:
     *          "success" -> Indicating success, this is the only string where the success attribute will be true
     *          "id falsey" -> The id was falsey
     *          "math falsey" -> The math was falsey
     *          "invalid id" -> Could not find an element associated with the given id
     * details - an empty string on success or an error object given more details about the failure
     */
    var update = function (id, math, callback) {
        var safeCallback = function () {  /* noop */; };
        if (isFunction(callback)) {
            safeCallback = callback;
        }

        if (!id) {
            safeCallback({ success: false, reason: "id falsey", details: "id was " + id });
            return;
        }

        if (!math) {
            safeCallback({ success: false, reason: "math falsey", details: "math was " + math });
            return;
        }

        var elem = document.getElementById(id);
        if (elem === null) {
            safeCallback({ success: false, reason: "invalid id", details: "could not find element for id " + id });
            return;
        }

        elem.innerHTML = math

        MathJax.Hub.queue.Push(
            MathJax.Hub.Queue([ "Typeset", MathJax.Hub, id ]),
            function () { safeCallback({ success: true, reason: "success", details: "" }); }
        );
    }

    return update;
}());
