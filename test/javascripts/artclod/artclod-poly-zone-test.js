
test("ARTC.mathJS.string2IntArray: can parse a single int", function() {
	deepEqual(ARTC.mathJS.string2IntArray("1"), { success: true, array: [1] });
});

test("ARTC.mathJS.string2IntArray: can parse a multiple ints", function() {
    deepEqual(ARTC.mathJS.string2IntArray("1, 2, 3"), { success: true, array: [1, 2, 3] });
});

test("ARTC.mathJS.string2IntArray: parsing an empty string returns empty roots", function() {
    deepEqual(ARTC.mathJS.string2IntArray(""), { success: true, array: [] });
});

test("ARTC.mathJS.string2IntArray: parsing decimals fails", function() {
    deepEqual(ARTC.mathJS.string2IntArray("2, 2.3"), { success: false, array: [] });
});

test("ARTC.mathJS.string2IntArray: parsing random string fails", function() {
    deepEqual(ARTC.mathJS.string2IntArray("asdfs, 2sdf 6.7"), { success: false, array: [] });
});


// ================
test("ARTC.mathJS.polyZones: no roots yields constant", function() {
	var zones = ARTC.mathJS.parsePolyZones("1", "")
    equal(zones.text, "1");
	equal(zones.func(10), 1);
	equal(zones.func(20), 1);
});

test("ARTC.mathJS.polyZones: one roots yields linear", function() {
	var zones = ARTC.mathJS.parsePolyZones("2", "3")
    equal(zones.text, "2 * (x - 3)");
	equal(zones.func(10), 14);
	equal(zones.func(20), 34);
});

test("ARTC.mathJS.polyZones: multiple roots", function() {
	var zones = ARTC.mathJS.parsePolyZones("1", "-3,0,1")
    equal(zones.text, "1 * (x - -3) * (x - 0) * (x - 1)");
	equal(zones.func(-1), 4);
	equal(zones.func(2), 10);
});

test("ARTC.mathJS.polyZones: gibberish scale yields failure", function() {
	var zones = ARTC.mathJS.parsePolyZones("asdf", "1,2")
    equal(zones.success, false);
});

test("ARTC.mathJS.polyZones: gibberish roots yields failure", function() {
	var zones = ARTC.mathJS.parsePolyZones("1", "sdaf")
    equal(zones.success, false);
});


// ================
test("ARTC.mathJS.polyInterval: matches numbers in parens", function() {
	var interval = ARTC.mathJS.polyInterval("(1,2)")
	deepEqual(interval, { success: true, lower: 1, upper: 2 } )
    equal(interval.success, true);
    equal(interval.lower, 1);
	equal(interval.upper, 2);
});

test("ARTC.mathJS.polyInterval: matches even with white space", function() {
	var interval = ARTC.mathJS.polyInterval(" ( 1 ,   2 )")
	deepEqual(interval, { success: true, lower: 1, upper: 2 } )
    equal(interval.success, true);
    equal(interval.lower, 1);
	equal(interval.upper, 2);
});

test("ARTC.mathJS.polyInterval: fails on gibberish", function() {
	var interval = ARTC.mathJS.polyInterval("(1,sd2)")
    equal(interval.success, false);
});

test("ARTC.mathJS.polyInterval: fails with extra parens", function() {
	var interval = ARTC.mathJS.polyInterval("(1,sd2))")
    equal(interval.success, false);
});


// ================
test("ARTC.mathJS.polyIntervals: matches numbers in parens", function() {
	var intervals = ARTC.mathJS.polyIntervals("(1,2), (3,4)")
	deepEqual(intervals.intervals, [{ success: true, lower: 1, upper: 2 }, { success: true, lower: 3, upper: 4 }] )
});

test("ARTC.mathJS.polyIntervals: whitespace is ignored", function() {
	var intervals = ARTC.mathJS.polyIntervals(" (1 ,2 ), (  3,4) ")
	deepEqual(intervals.intervals, [{ success: true, lower: 1, upper: 2 }, { success: true, lower: 3, upper: 4 }] )
});

test("ARTC.mathJS.polyIntervals: fails in gibberish", function() {
	var intervals = ARTC.mathJS.polyIntervals(" (1 ,2 )sadf, (  3,4) ")
	equal(intervals.success, false)
});

test("ARTC.mathJS.polyIntervals: fails for an extra paren", function() {
	var intervals = ARTC.mathJS.polyIntervals("(1,2)), (3,4)")
	equal(intervals.success, false)
});
