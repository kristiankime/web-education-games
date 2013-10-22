// Returns the first Jax element with the given id, mostly to save typing
function jax(id) {
    return MathJax.Hub.getAllJax(id)[0];
}

// Update a Jax element with a string in TeX format
// Modified from http://docs.mathjax.org/en/latest/typeset.html
function updateJaxTeX(jax, TeX) {
    MathJax.Hub.queue.Push([ "Text", jax, "\\displaystyle{" + TeX + "}" ]);
}

// Update an html with a string in content MathML format
function updateContentMathML(id, mathMLStr) {
    var mathML = ARTC.txt2MathML.parse(mathMLStr);
    var elem = document.getElementById(id);
    elem.innerHTML = '<script type="math\/mml"> <math> ' + mathML + ' <\/math> <\/script>';
    MathJax.Hub.queue.Push(MathJax.Hub.Queue([ "Typeset", MathJax.Hub, id ]));
}

// Returns the MathML associated with a Jax element
// Modified from https://groups.google.com/forum/#!msg/mathjax-users/unL8IjcrTto/DjHpH4BbPRcJ
function toMathML(jax, callback) {
    var mml;
    try {
	// Try to produce the MathML. If an asynchronous action occurs, a reset error is thrown. Otherwise we got the MathML and call the user's callback passing the MathML.
	mml = jax.root.toMathML("");
    } catch (err) {
	if (!err.restart) {
	    // This means an actual error occurred, nothing we can do but re-throw.
	    throw err;
	}
	// This means files are still loading. In this case we call this routine again after waiting for the the asynchronous action to finish.
	return MathJax.Callback.After([ toMathML, jax, callback ], err.restart);
    }
    // Pass the MathML to the user's callback
    MathJax.Callback(callback)(mml);
}