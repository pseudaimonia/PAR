'use strict';

describe("InputDatalistResponseBoxDisplay", function () {
    it("buildOptionElement", function () {
        let element = buildOptionElement("nope");
        expect(element.tagName.toLowerCase()).toBe("option");
        expect(element.getAttribute("value")).toBe("nope");
    });

    it("buildDatalistElement", function () {
        let element = buildDatalistElement("test1", ["1", "2", "3"]);
        expect(element.tagName.toLowerCase()).toBe("datalist");
        expect(element.id.toLowerCase()).toBe(("test1Datalist").toLowerCase());
        expect(element.options.length).toBe(3);
        expect(element.options.item(0).value).toBe("1");
        expect(element.options.item(element.options.length - 1).value).toBe('3');
    });

    it("addToTypesIncorrect", function () {
        let testList = [];
        addToTypesIncorrect("correct", "plane", testList);
        expect(testList.length).toBe(0);
        addToTypesIncorrect("correct", "ZONE", testList);
        expect(testList.length).toBe(0);
        addToTypesIncorrect("correct", "", testList);
        expect(testList.length).toBe(0);

        addToTypesIncorrect("incorrect", "plane", testList);
        expect(testList.length).toBe(1);
        addToTypesIncorrect("incorrect", "ZONE", testList);
        expect(testList.length).toBe(2);
        addToTypesIncorrect("incorrect", "", testList);
        expect(testList.length).toBe(3);

        addToTypesIncorrect("incorrect", "plane", testList);
        expect(testList.length).toBe(3);
        addToTypesIncorrect("incorrect", "ZONE", testList);
        expect(testList.length).toBe(3);
        addToTypesIncorrect("incorrect", "", testList);
        expect(testList.length).toBe(3);

        addToTypesIncorrect("unsure", "plane1", testList);
        expect(testList.length).toBe(4);
        addToTypesIncorrect("unsure", "ZONE1", testList);
        expect(testList.length).toBe(5);
        addToTypesIncorrect("unsure", "1", testList);
        expect(testList.length).toBe(6);

        addToTypesIncorrect("", "plane2", testList);
        expect(testList.length).toBe(6);
        addToTypesIncorrect("", "ZONE2", testList);
        expect(testList.length).toBe(6);
        addToTypesIncorrect("", "2", testList);
        expect(testList.length).toBe(6);
    });

    it("displayCheckedResponse", function () {
        let textEntryResponseBox = new InputDatalistResponseBoxDisplay("test1", ["high", "middle", "low"], "low");
        document.getElementById("testArea").appendChild(textEntryResponseBox.element);
        document.getElementById("testArea").style.display = "none";

        expect(displayCheckedResponse("correct", "Long", true)).toBe('<font color=\"green\">Your answer is: Correct</font>');
        expect(displayCheckedResponse("correct", "Long", false)).toBe('<font color=\"green\">Your answer is: Correct</font>');

        expect(displayCheckedResponse("unsure", "Long", true)).toBe("<font color=\"#663399\">The correct answer is Long</font>");
        expect(displayCheckedResponse("unsure", "Long", false)).toBe("");

        expect(displayCheckedResponse("", "Long", true)).toBe("");
        expect(displayCheckedResponse("", "Long", false)).toBe("");

        expect(displayCheckedResponse("incorrect", "Long", true)).toBe('<font color=\"red\">Your answer is: Incorrect</font>');
        expect(displayCheckedResponse("incorrect", "Long", false)).toBe('<font color=\"red\">Your answer is: Incorrect</font>');
    });

    it("checkAnyResponse", function () {
        let textEntryResponseBox = new InputDatalistResponseBoxDisplay("test1", ["high", "middle", "low"], "low");

        expect(checkAnyResponse("a", "a")).toBe(ResponseResult.correct);
        expect(checkAnyResponse(" a ", "a")).toBe(ResponseResult.correct);
        expect(checkAnyResponse(" A ", "a")).toBe(ResponseResult.correct);
        expect(checkAnyResponse("a", " a ")).toBe(ResponseResult.correct);
        expect(checkAnyResponse("a", " A ")).toBe(ResponseResult.correct);
        expect(checkAnyResponse("aA ", " Aa")).toBe(ResponseResult.correct);
        expect(checkAnyResponse("aB", " Ab ")).toBe(ResponseResult.correct);

        expect(checkAnyResponse("a", "b")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("a", "ab")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("ab", "ba")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("ab", "a b")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("aB", "Ba")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("a", "b  ")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("a", "ab  ")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("ab", "ba")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("aB", "Ba")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("a", "b  ")).toBe(ResponseResult.incorrect);
        expect(checkAnyResponse("a", "ab  ")).toBe(ResponseResult.incorrect);

        expect(checkAnyResponse("anything", ResponseResult.unsure)).toBe(ResponseResult.unsure);
        expect(checkAnyResponse("anything", " " + ResponseResult.unsure.toUpperCase() + " ")).toBe(ResponseResult.unsure);
        //unsure shouldn't be authored as the correct answer, but if it is, it should return correct when entered
        expect(checkAnyResponse(ResponseResult.unsure, ResponseResult.unsure)).toBe(ResponseResult.correct);

        expect(checkAnyResponse("anything", ResponseResult.blank)).toBe(ResponseResult.blank);
        expect(checkAnyResponse("anything", " ")).toBe(ResponseResult.blank);
        expect(checkAnyResponse("anything", "  ")).toBe(ResponseResult.blank);
        expect(checkAnyResponse("anything", " \t")).toBe(ResponseResult.blank);
        expect(checkAnyResponse("anything", " \n")).toBe(ResponseResult.blank);
        //blank shouldn't be authored as the correct answer, but if it is, it should return correct when entered
        expect(checkAnyResponse(ResponseResult.blank, ResponseResult.blank)).toBe(ResponseResult.correct);
    });

    it("checkThisResponse", function () {
        let textEntryResponseBox = new InputDatalistResponseBoxDisplay("test1", ["high", "middle", "low"], "low");
        document.getElementById("testArea").appendChild(textEntryResponseBox.element);
        document.getElementById("testArea").style.display = "none";
        textEntryResponseBox.inputTextbox.value = "low";
        let testList = new NewResponse("test1");
        let test= testList.addToQuestionResponses("id");
        let unsureShowsCorrect = false;

        expect(textEntryResponseBox.checkCurrentResponse(test, unsureShowsCorrect, testList)).toBe(ResponseResult.correct, unsureShowsCorrect);
        textEntryResponseBox.inputTextbox.value = "low ";
        expect(textEntryResponseBox.checkCurrentResponse(test, unsureShowsCorrect, testList)).toBe(ResponseResult.correct, unsureShowsCorrect);
        textEntryResponseBox.inputTextbox.value = "LOW";
        expect(textEntryResponseBox.checkCurrentResponse(test, unsureShowsCorrect, testList)).toBe(ResponseResult.correct, unsureShowsCorrect);
        textEntryResponseBox.inputTextbox.value = "HIGH";
        expect(textEntryResponseBox.checkCurrentResponse(test, unsureShowsCorrect, testList)).toBe(ResponseResult.incorrect, unsureShowsCorrect);
        textEntryResponseBox.inputTextbox.value = "high";
        expect(textEntryResponseBox.checkCurrentResponse(test, unsureShowsCorrect, testList)).toBe(ResponseResult.incorrect, unsureShowsCorrect);
        textEntryResponseBox.inputTextbox.value = "something";
        expect(textEntryResponseBox.checkCurrentResponse(test, unsureShowsCorrect, testList)).toBe(ResponseResult.incorrect, unsureShowsCorrect);
    });
    it("disableElement", function () {
        let testElement = document.createElement('button');
        testElement.textContent = "test button";
        expect(testElement.disabled).toBe(false);
        expect(disableElement(testElement)).toBe(true);
    });
    it("inputBoxAutoSize", function () {
        let listOfStr0 = ["one", "three", "four", "asuperlongwordthatgoespasttwentyttt"];
        let listOfStr1 = ["one", "the", "for"];
        let listOfStr2 = ["one", "three", " "];
        let listOfStr3 = ["o", "tw", "thr"];
        let listOfStr4 = ["o"];
        let listOfStr5 = ["lllllllloooooooonnng boi tada", "transverse", "zagga"];
        let listOfStr6 = ["lllllllloooooooonnng boi tada", "transverse", "Both the zip (and the -zorp) act in such a way a _WIDE_ boy cannot possibly; well; understand!"];
        expect(inputBoxAutoSize(listOfStr0)).toBe(30);
        expect(inputBoxAutoSize(listOfStr1)).toBe(20);
        expect(inputBoxAutoSize(listOfStr2)).toBe(20);
        expect(inputBoxAutoSize(listOfStr3)).toBe(20);
        expect(inputBoxAutoSize(listOfStr4)).toBe(20);
        expect(inputBoxAutoSize(listOfStr5)).toBe(25);
        expect(inputBoxAutoSize(listOfStr6)).toBe(79);
    })
});