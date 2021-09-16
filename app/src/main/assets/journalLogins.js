(function () {
    let res = "";
    const options = document.getElementsByTagName('option');
    for (let i = 0; i < options.length; i++) {
        res += options[i].value;
        res += ":";
        res += options[i].text;
        res += "|";
    }
    return res
})();