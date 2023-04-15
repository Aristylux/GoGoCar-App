let mc_len = 0;
let mc_dot1 = "",
    mc_dot2 = "";

function moduleCodeFormater(textValue) {
    const sanitizedValue = textValue.replace(/[^\d]/g, "").slice(0, 8);

    //console.log("moduleCodeFormater: " + textValue);
    //console.log("moduleCodeFormater: " + sanitizedValue + "(" + sanitizedValue.length + ")");

    // Detect is user delete text
    if (sanitizedValue.length > mc_len) {
        if (sanitizedValue.length >= 3) mc_dot1 = "-";
        if (sanitizedValue.length >= 5) mc_dot2 = "-";
    } else {
        if (sanitizedValue.length <= 2) mc_dot1 = "";
        if (sanitizedValue.length <= 4) mc_dot2 = "";
    }

    // Update
    mc_len = sanitizedValue.length;

    return (
        "#" +
        sanitizedValue.slice(0, 2) +
        mc_dot1 +
        sanitizedValue.slice(2, 4) +
        mc_dot2 +
        sanitizedValue.slice(4, 8)
    );
}

let lp_len = 0;
let lp_dot1 = "",
    lp_dot2 = "";

function licencePlateFormater(textValue) {
    const sanitizedValue = textValue.toUpperCase().replace(/[^A-Z0-9]/g, "");

    // Remove last letter
    let originalValue = sanitizedValue.slice(0, -1);

    // Get the last character entered by the user
    const lastChar = sanitizedValue.slice(-1);

    // Control letter and number
    switch (sanitizedValue.length) {
        case 1:
        case 2:
        case 6:
        case 7:
            // If it is a letter, print it
            if (lastChar.match(/[a-zA-Z]/)) originalValue = sanitizedValue;
            break;
        case 3:
        case 4:
        case 5:
            // If it is a number, print it
            if (lastChar.match(/[0-9]/)) originalValue = sanitizedValue;
            break;
    }

    // Detect is user delete text
    if (sanitizedValue.length > lp_len) {
        if (sanitizedValue.length >= 3) lp_dot1 = "-";
        if (sanitizedValue.length >= 6) lp_dot2 = "-";
    } else {
        if (sanitizedValue.length <= 2) lp_dot1 = "";
        if (sanitizedValue.length <= 5) lp_dot2 = "";
    }

    // Update
    lp_len = sanitizedValue.length;

    return (
        originalValue.slice(0, 2) +
        lp_dot1 +
        originalValue.slice(2, 5) +
        lp_dot2 +
        originalValue.slice(5, 9)
    );
}
