if (!ARTC) {
    var ARTC = {};
}

if (!ARTC.mathJS) {
    ARTC.mathJS = {};
}

/*
 * Parse string to an int only if the entire string is an integer (modulo whitespace)
 */
ARTC.mathJS.parsePureInt = function(text, radix) {
    if(typeof text !== "string") { return NaN; }
    var trim = text.trim();
    var test = /^[+-]?[0-9]*$/.test(trim);
    if( ! test ) { return NaN; }
    return parseInt(text, radix);
}

/*
 * Parse comma separated integers
 */
ARTC.mathJS.string2IntArray = function(text) {
    if(typeof text !== "string") { return { success : false, array : [] }; }
    if(text === "") { return { success : true, array : [] }; }

    var split = text.split(",")
    var success = true;
    var roots = [];
    split.forEach(function (v) {
        var parse =  ARTC.mathJS.parsePureInt(v, 10)
        roots.push( parse );
        if(isNaN(parse)) { success = false; }
    });

    var array = (success ? roots : [])
    return { success : success, array : array };
}

/*
 *
 */
ARTC.mathJS.parsePolyZones = function(scaleText, rootsText) {
    var scale = ARTC.mathJS.parsePureInt(scaleText);
    if(isNaN(scale)) { return { success : false }; }
    var roots = ARTC.mathJS.string2IntArray(rootsText);
    if(!roots.success) { return { success : false}; }
    return ARTC.mathJS.polyZones(scale, roots);
}

/*
 *
 */
ARTC.mathJS.polyZones = function(scale, rootsObj) {
    if(isNaN(scale)) { return { success: false }; }

    var roots = rootsObj.array;
    if(roots.length == 0) {
        return {
            success: true,
            text: "" + scale,
            node: math.parse("" + scale),
            func: function(x) { return scale; }
        }
    }

    var node = scale.toString();
    roots.forEach(function(e){ node += " * (x - " + e + ")"; })

    var func = function(x) {
        var ret = scale;
        roots.forEach(function(e){ ret *= x - e; })
        return ret;
    }

    return {
        success: true,
        text: node,
        node: math.parse(node),
        func: func
    }
}