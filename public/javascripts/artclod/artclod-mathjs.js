if(!ARTC){
    var ARTC = {};
}

if(!ARTC.mathJS){
    ARTC.mathJS = {};
}

/*
 * ARTC.buildMathJSParser is a builder for parsers.
 * The main goal of these parsers turn a MathJS style string into Content MathML.
 *
 * ARTC.buildMathJSParser requires an "function", "operator" and "symbol" map.
 * These maps specify which subset of MathJS is legal in the parser and what content MathML the Mathjs should map to.
 * See ARTC.mathJSDefaults for example of these maps.
 *
 * The parser returns an object with the following format:
 * {
 *   success: A boolean indicating if everything went well,
 *   content: a string with the content MathML if success is true or "" otherwise,
 *   mathJSNode: the MathJS node version of the input string on success or an empty object otherwise,
 *   error: an empty object on success or the error on failure
 * }
 */
ARTC.mathJS.buildParser = (function(){
    // Copied from artclod_arg_map.js so as to not require an import
    var argMapCreate = function(map) {
        return function (key, num_args) {
            if (typeof num_args !== 'undefined') {
                var argsKey = key + "#" + num_args
                if (map.hasOwnProperty(argsKey)) {
                    return map[argsKey];
                }
            }
            return map[key];
        };
    }

    // Put <apply> tag around elements
    var applyWrap = function(operator, elements, parseNode) {
        var ret = "<apply> " + operator + " ";
        var len = elements.length;
        for (var i = 0; i < len; i++) {
            var n = elements[i];
            ret += parseNode(n) + " ";
        }
        ret += "</apply>";
        return ret;
    }

    // The main function
    return function(functions, operators, symbols){
        // If we don't have values passed in use defaults
        var functionsSafe = ARTC.mathJS.parserDefaults.functions;
        if(functions){ functionsSafe = functions; }

        var operatorsSafe = ARTC.mathJS.parserDefaults.operators;
        if(operators){ operatorsSafe = operators; }

        var symbolsSafe = ARTC.mathJS.parserDefaults.symbols;
        if(symbols){ symbolsSafe = symbols; }

        // ==============  Function Handling ==============
        var fncMap = argMapCreate(functionsSafe);

        var functionNodeFunction = function(node){
            var fnc = fncMap(node.name, node.args.length)
            if(!fnc) { throw { message: "Error finding function for FunctionNode with name " + node.name + " and # args = " + node.args.length } }
            return fnc(node, parseNode);
        }

        // ==============  Operator Handling ==============
        var opMap = argMapCreate(operatorsSafe);

        var operatorNodeFunction = function(node){
            var op = opMap(node.op)
            if(!op) { throw { message: "Error finding operator for OperatorNode with op " + node.op } }
            return applyWrap(op, node.args, parseNode);
        }

        // ============== Symbol Handling ===========
        var symMap = argMapCreate(symbolsSafe.map);

        var symbolNodeFunction = function(node){
            var sym = symMap(node.name)
            if(sym) { return sym }
            if(!sym && !symbols.allowAny) { throw { message: "Error in SymbolNode, any not allowed, and nothing specified for " + node.name } }
            return "<ci> " + node.name + " </ci>";
        }

        // ============== Full Parsing ===========
        var parseNode = function(node) {
            switch (node.type) {
                case 'OperatorNode': return operatorNodeFunction(node);
                case 'ConstantNode': return "<cn> " + node.value + " </cn>"; // TODO parse down to cn type here
                case 'SymbolNode':   return symbolNodeFunction(node);
                case 'FunctionNode': return functionNodeFunction(node);
                default:             throw { message: "Error, unknown node type " + node };
            }
        }

        return function(string) {
            try {
                var mathJSNode = math.parse(string);

                return {
                    success: true,
                    content: '<math xmlns="http://www.w3.org/1998/Math/MathML"> ' + parseNode(mathJSNode) + ' </math>',
                    mathJSNode: mathJSNode,
                    error: {}
                };
            } catch (e) {
                return {
                    success: false,
                    content: "",
                    mathJSNode: {},
                    error: e
                }
            }
        }
    }
}());

/*
 * Helpful defaults for use with ARTC.buildMathJSParser
 */
ARTC.mathJS.parserDefaults = {
    // All functions here take (node, parseNode)
    functions: {
        "cos#1"     : function(n, pN){ return "<apply> <cos/> " + pN(n.args[0]) + " </apply>"; },
        "sin#1"     : function(n, pN){ return "<apply> <sin/> " + pN(n.args[0]) + " </apply>"; },
        "tan#1"     : function(n, pN){ return "<apply> <tan/> " + pN(n.args[0]) + " </apply>"; },
        "sec#1"     : function(n, pN){ return "<apply> <sec/> " + pN(n.args[0]) + " </apply>"; },
        "csc#1"     : function(n, pN){ return "<apply> <csc/> " + pN(n.args[0]) + " </apply>"; },
        "cot#1"     : function(n, pN){ return "<apply> <cot/> " + pN(n.args[0]) + " </apply>"; },
        "sqrt#1"    : function(n, pN){ return "<apply> <root/> " + pN(n.args[0]) + " </apply>"; },
        "nthRoot#2" : function(n, pN){ return "<apply> <root/> <degree> " + pN(n.args[1]) + " </degree> " + pN(n.args[0]) + " </apply>"; },
        "ln#1"      : function(n, pN){ return "<apply> <ln/> " + pN(n.args[0]) + " </apply>"; },
        "log#1"     : function(n, pN){ return "<apply> <log/> " + pN(n.args[0]) + " </apply>"; },
        "log#2"     : function(n, pN){ return "<apply> <log/> <logbase> " + pN(n.args[1]) + " </logbase> " + pN(n.args[0]) + " </apply>"; },
        "pow#2"     : function(n, pN){ return "<apply> <power/> " + pN(n.args[0]) + " " + pN(n.args[1]) + " </apply>"; },
        "exp#1"     : function(n, pN){ return "<apply> <power/> <exponentiale/> " + pN(n.args[0]) + " </apply>"; }
    },
    operators : {
        "+" : "<plus/>",
        "-" : "<minus/>",
        "*" : "<times/>",
        "/" : "<divide/>",
        "^" : "<power/>"
    },
    symbols : {
        map: {
            "pi": "<pi/>",
            "e": "<exponentiale/>",
            "x": "<ci> x </ci>"
        },
        allowAny : false
    }
};


ARTC.mathJS.defaultParser = ARTC.mathJS.buildParser();