
const add_vehicle_button = document.getElementById("add_vehicle_button");

add_vehicle_button.addEventListener('click', function() {
    // Get values
    const formVehicle = document.getElementById("form-add-vehicle");
    const formVhModel = formVehicle.elements["vehicle_model"].value;
    const formVhLicencePlate = formVehicle.elements["vehicle_licence_plate"].value;
    const formVhAddress = formVehicle.elements["vehicle_address"].value;
    const formVhModuleCode = formVehicle.elements["vehicle_module_code"].value;
    const formVhIsAvailable = formVehicle.elements["switch"].checked;
    
    console.log("vehicle: " + formVhModel + ", " + formVhLicencePlate + ", " + formVhAddress + ", " + formVhModuleCode + ", " + formVhIsAvailable);
    // Sent to android
    if(androidConnected()) Android.requestAddVehicle(formVhModel, formVhLicencePlate, formVhAddress, formVhModuleCode, formVhIsAvailable);
});


const ADD_VEHICLE_NO_ADDRESS = 1,
    ADD_VEHICLE_CAR_UNKNOWN = 2,
    ADD_VEHICLE_MODULE_CODE_UNKNOWN = 3,
    ADD_VEHICLE_FAILED = 4;

const error_messages = {
    messages: [
        "",
        "Address incorrect",
        "Car unknown",
        "Module code unknown",
        "Register failed"
    ],
    getErrorText: function (errorCode) {
        return this.messages[errorCode];
    },
};

//[ANDROID CALLBACK]
// Fail
// Stay in the current page
function addVehicleResult(code) {
    // When function called by Android (cause is a string, not a number)
    if (typeof code == "string") code = parseInt(code);

    switch (code) {
        case ADD_VEHICLE_NO_ADDRESS:
            console.error("ADD_VEHICLE_NO_ADDRESS: " + error_messages.getErrorText(code));
            break;
        case ADD_VEHICLE_CAR_UNKNOWN:
            console.error("ADD_VEHICLE_CAR_UNKNOWN: " + error_messages.getErrorText(code));
            break;
        case ADD_VEHICLE_MODULE_CODE_UNKNOWN:
            console.error("ADD_VEHICLE_MODULE_CODE_UNKNOWN: " + error_messages.getErrorText(code));
            break;
        case ADD_VEHICLE_FAILED:
            console.error("ADD_VEHICLE_FAILED: " + error_messages.getErrorText(code));
            break;
    }
}

// ---- Input ----

const input_lp = document.getElementById("vehicle_licence_plate");

input_lp.addEventListener("input", () => {
    document.getElementById("err_lp").classList.remove("error-visible");
    input_lp.value = licencePlateFormater(input_lp.value);
});

input_lp.addEventListener("blur", () => {
    const value = input_lp.value;
    const regex = /^[A-Z]{2}-\d{3}-[A-Z]{2}$/;
    if (regex.test(value)) {
        console.log("Input is valid!");
    } else {
        console.log("Input is invalid!");
        document.getElementById("err_lp").classList.add("error-visible");
    }
});

const input_mc = document.getElementById("vehicle_module_code");

input_mc.addEventListener("input", () => {
    document.getElementById("err_mc").classList.remove("error-visible");
    input_mc.value = moduleCodeFormater(input_mc.value);
});

input_mc.addEventListener("blur", () => {
    const value = input_mc.value;
    const regex = /^#[0-9]{2}-[0-9]{2}-[0-9]{4}$/;
    if (regex.test(value)) {
        console.log("Input is valid!");
    } else {
        console.log("Input is invalid!");
        document.getElementById("err_mc").classList.add("error-visible");
    }
});
