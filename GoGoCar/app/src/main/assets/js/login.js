if (androidConnected()) Android.changeBackground("#4070f4FF");

console.log(window.getComputedStyle(document.getElementsByTagName("body")[0], null).getPropertyValue("background-color"));

const container = document.querySelector(".container"),
    pwShowHide = document.querySelectorAll(".showHidePw"),
    pwFields = document.querySelectorAll(".password");

// show/hide password
pwShowHide.forEach((eyeIcon) => {
    eyeIcon.addEventListener("click", function () {
        pwFields.forEach((pwField) => {
            if (pwField.type === "password") {
                pwField.type = "text";
                pwShowHide.forEach((icon) => {
                    icon.classList.replace("fi-rr-eye-crossed", "fi-rr-eye");
                });
            } else {
                pwField.type = "password";
                pwShowHide.forEach((icon) => {
                    icon.classList.replace("fi-rr-eye", "fi-rr-eye-crossed");
                });
            }
        });
    });
});

// Change sign method
const signUp = document.querySelector(".signup-link"),
    signIn = document.querySelector(".signin-link");

signUp.addEventListener("click", function () {
    container.classList.add("active");
});

signIn.addEventListener("click", function () {
    container.classList.remove("active");
});

// -----------

const loginForm = document.getElementById("login_form");
const loginSubmitButton = document.getElementById("but_login");

const loginElementEmail = loginForm.elements["login_email"];
const loginElementPassword = loginForm.elements["login_password"];

// For Login
// If ok : Android change page
// If not ok : Android form error "email or password incorrect"
loginSubmitButton.addEventListener("click", function () {
    // Collect data (email, password)
    const email = loginElementEmail.value;
    const password = loginElementPassword.value;

    // Sent to android
    if (androidConnected()) Android.AuthenticationLogin(email, password);
});

loginForm.addEventListener("input", (event) => {
    loginElementEmail.parentNode.classList.remove("error");
    loginElementPassword.parentNode.classList.remove("error");
});

// Error callback
function errorAuthenticationLogin() {
    console.error("Login failed");
    loginElementPassword.parentNode.querySelector("small").innerText =
        "Email or password incorrect";
    loginElementPassword.parentNode.classList.add("error");
    loginElementEmail.parentNode.classList.add("error");
}

// ---------------

const NAME_ERROR_CODE = 1,
    EMAIL_ERROR_CODE = 2,
    EMAIL_ERROR_EXIST_CODE = 3,
    PHONE_ERROR_CODE = 4,
    PHONE_ERROR_EXIST_CODE = 5,
    PASSWORD_EMPTY_ERROR_CODE = 6,
    PASSWORD_ERROR_CODE = 7;

const error_messages = {
    messages: [
        "",
        "First Name & Last Name needed",
        "Incorrect email",
        "Email already used",
        "Incorrect phone",
        "Phone already used",
        "Empty password",
        "Password are not identical",
    ],
    getErrorText: function (errorCode) {
        return this.messages[errorCode];
    },
};

const EMAIL_SUCCESS_CODE = 1,
    PHONE_SUCCESS_CODE = 2;

let email_correct = false,
    phone_correct = false;

const registerForm = document.getElementById("register_form");
const registerSubmitButton = document.getElementById("but_register");

const registerElementName = registerForm.elements["register_name"];
const registerElementEmail = registerForm.elements["register_email"];
const registerElementPhone = registerForm.elements["register_phonenumber"];
const registerElementPassword = registerForm.elements["register_password"];
const registerElementPasswordConf =
    registerForm.elements["register_password_confirmation"];

// For register: during typing
// verify password are the same
for (let i = 0; i < registerForm.elements.length; i++) {
    registerForm.elements[i].addEventListener("input", (event) => {
        if (registerForm.elements[i].id !== registerElementPasswordConf.id) {
            registerForm.elements[i].parentNode.classList.remove("error");
        } else {
            if (
                registerElementPassword.value.indexOf(
                    registerElementPasswordConf.value
                ) != 0
            ) {
                errorAuthenticationRegistration(PASSWORD_ERROR_CODE);
            } else {
                registerForm.elements[i].parentNode.classList.remove("error");
            }
        }
    });
}

// When user leave name input
// delete if exist last ' '

// When user leave email input
// Verify mail adress doesn't exist into our database (ask Android)
// Request email (if email already exist: error)
registerElementEmail.addEventListener("change", function () {
    console.log("email change");

    const emailRegex =
        /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    const email = registerElementEmail.value;

    if (!emailRegex.test(email)) {
        errorAuthenticationRegistration(EMAIL_ERROR_CODE);
    } else {
        if (androidConnected())
            Android.verifyEmail(
                email,
                EMAIL_SUCCESS_CODE,
                EMAIL_ERROR_EXIST_CODE
            );
    }
});

// When user leave phonenumber input
// Verify phonenumber doesn't exist into our database (ask Android)
// Request phonenumber (if phonenumber already exist: error)
registerElementPhone.addEventListener("change", function () {
    console.log("phone number change");
    const phone = registerElementPhone.value;
    // Format 06 XX XX XX XX
    if (!testPhoneNumberFormat(phone)) {
        errorAuthenticationRegistration(PHONE_ERROR_CODE);
    } else {
        // Update view with new format
        let formatedPhone = formatPhone(phone);
        registerElementPhone.value = formatedPhone;
        // Verify phone number doesn't exist into our database
        if (androidConnected())
            Android.verifyPhone(
                formatedPhone,
                PHONE_SUCCESS_CODE,
                PHONE_ERROR_EXIST_CODE
            );
    }
});

// [ANDROID] Success callback
function success(code_success) {
    if (code_success == EMAIL_SUCCESS_CODE) {
        // Email ok
        email_correct = true;
        return;
    }
    if (code_success == PHONE_SUCCESS_CODE) {
        // Phone ok
        phone_correct = true;
        return;
    }
}

// Detect if phone number is correct
function testPhoneNumberFormat(phonenumber) {
    phonenumber = phonenumber.replace(/ /g, "");

    // If region code is correct
    let regionCode = phonenumber.substring(0, 2);
    if (!testRegionCode(regionCode)) return false;

    // 0X12345678. lenght = 10
    if (phonenumber.length != 10) return false;

    return true;
}

// if regionCode is 06 or 07 it's ok
function testRegionCode(regionCode) {
    if (regionCode === "06") return true;
    if (regionCode === "07") return true;
    return false;
}

/*
 * formatPhone:
 * Format phone number
 * @retval phone number formatted
 */
function formatPhone(phone) {
    // delete all space: ' '
    phone = phone.replace(/ /g, "");

    // add format (add space every 2 digit)
    phone = phone.replace(/(.{2})/g, "$1 ");

    // Delete last char (space:' ') and return formated phone
    return phone.substring(0, phone.length - 1);
}

// if clicked on button Register
// When all is ok : send data to Android (name, email, phonenumber, password)
registerSubmitButton.addEventListener("click", function () {
    console.log("Register click");
    let register_success = true;

    const name = registerElementName.value,
        email = registerElementEmail.value,
        phone = registerElementPhone.value,
        password = registerElementPassword.value,
        password_c = registerElementPasswordConf.value;

    // If name contain 2word (first name and last name)
    if (!checkNames(name) || name == "") {
        register_success = false;
        errorAuthenticationRegistration(NAME_ERROR_CODE);
    }

    // If email_correct != true
    if (!email_correct || email == "") {
        register_success = false;
        errorAuthenticationRegistration(EMAIL_ERROR_CODE);
    }

    // If phone_correct != true
    if (!phone_correct || phone == "") {
        register_success = false;
        errorAuthenticationRegistration(PHONE_ERROR_CODE);
    }

    // If password != password_c
    if (password == "" || password_c == "") {
        register_success = false;
        errorAuthenticationRegistration(PASSWORD_EMPTY_ERROR_CODE);
    } else if (password.normalize() !== password_c.normalize()) {
        register_success = false;
        errorAuthenticationRegistration(PASSWORD_ERROR_CODE);
    }

    if (androidConnected() && register_success)
        Android.AuthenticationRegister(name, email, phone, password);
});

function checkNames(fullName) {
    console.log("check names: " + fullName);
    // Extract names
    let names = fullName.split(" ");

    // Verify names are 2
    if (names.length != 2) return false;

    if (names[0] == "" || names[1] == "") return false;

    return true;
}

// Error callback
function errorAuthenticationRegistration(cause) {
    // When function called by Android (cause is a string, not a number)
    if (typeof cause == "string") cause = parseInt(cause);
    else console.log("from web");

    console.info("Register failed: " + cause);
    let selectElement;
    switch (cause) {
        case NAME_ERROR_CODE:
            selectElement = registerElementName.parentNode;
            break;

        case EMAIL_ERROR_CODE:
            selectElement = registerElementEmail.parentNode;
            break;

        case EMAIL_ERROR_EXIST_CODE:
            selectElement = registerElementEmail.parentNode;
            break;

        case PHONE_ERROR_CODE:
            selectElement = registerElementPhone.parentNode;
            break;

        case PHONE_ERROR_EXIST_CODE:
            selectElement = registerElementPhone.parentNode;
            break;

        case PASSWORD_EMPTY_ERROR_CODE:
            registerElementPassword.parentNode.classList.add("error");
            selectElement = registerElementPasswordConf.parentNode;
            break;

        case PASSWORD_ERROR_CODE:
            selectElement = registerElementPasswordConf.parentNode;
            break;

        default:
            if (androidConnected())
                Android.showToast("Registration error: error unknown");
            break;
    }

    selectElement.querySelector("small").innerText =
        error_messages.getErrorText(cause);
    selectElement.classList.add("error");
}
