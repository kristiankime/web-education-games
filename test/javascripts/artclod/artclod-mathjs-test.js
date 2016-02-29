
test("ARTC.mathJS.prepFuncPow: no match return initial string", function() {
    equal(ARTC.mathJS.prepFuncPow("sin(x)", ["cos"]), "sin(x)");
});

test("ARTC.mathJS.prepFuncPow: only matches integer powers", function() {
    equal(ARTC.mathJS.prepFuncPow("cos^3.1(x)", ["cos"]), "cos^3.1(x)");
});

test("ARTC.mathJS.prepFuncPow: match puts power outside function call", function() {
    equal(ARTC.mathJS.prepFuncPow("cos^2(x)", ["cos"]), "(cos(x)^2)");
});

test("ARTC.mathJS.prepFuncPow: match puts power outside function call, extra string at front", function() {
    equal(ARTC.mathJS.prepFuncPow("3+cos^2(x)", ["cos"]), "3+(cos(x)^2)");
});

test("ARTC.mathJS.prepFuncPow: match puts power outside function call, extra string at end", function() {
    equal(ARTC.mathJS.prepFuncPow("cos^2(x)+3", ["cos"]), "(cos(x)^2)+3");
});

test("ARTC.mathJS.prepFuncPow: match works on all listed functions", function() {
    equal(ARTC.mathJS.prepFuncPow("cos^2(x)+sin^3(x+2)", ["cos", "sin"]), "(cos(x)^2)+(sin(x+2)^3)");
});

test("ARTC.mathJS.prepFuncPow: match works on nested functions", function() {
    equal(ARTC.mathJS.prepFuncPow("cos^2(sin^3(x+2))", ["cos", "sin"]), "(cos((sin(x+2)^3))^2)");
});
