if (!ARTC) {
    var ARTC = {};
}

ARTC.opentipMathJS = function (elementId, parser) {
    var symbols = _.map(parser.symbols, function(value, key, list) { return key }).reduce(function(a,b) { return a + ", " + b });
    var operators = _.map(parser.operators, function(value, key, list) { return key } ).reduce(function(a,b) { return a + ", " + b });
    var functions = _.map(parser.functions, function(value, key, list) { return key.split("#")[0] } ).reduce(function(a,b) { return a + ", " + b });

    new Opentip(elementId, "The system understands the following symbols: " + symbols + "<br>" +
    "You can use the following operators: " + operators + "<br>" +
    "The following are the available functions can be used by : " + functions);
}
