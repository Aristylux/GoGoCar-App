// Exit form button
const button_exit = document.getElementById("exit_vehicle_button");
button_exit.addEventListener('click', function () {
    if (androidConnected()) Android.requestClosePanel();
});

var JSONvehicle;
// Request Get informations
if(androidConnected()) Android.requestGetVehicle();

// [ANDROID CALLBACK]
function setVehicle(vehicle){
    console.log("set: " + vehicle);
    JSONvehicle = JSON.parse(vehicle.replace(/\$/g, "'"));

    const formVehicle = document.getElementById("form-edit-vehicle");
    formVehicle.elements["vehicle_model"].value = JSONvehicle.name;
    formVehicle.elements["vehicle_licence_plate"].value = JSONvehicle.licencePlate;
    formVehicle.elements["vehicle_address"].value = JSONvehicle.address;
    formVehicle.elements["vehicle_module_code"].value = JSONvehicle.codeModule;
    formVehicle.elements["switch"].checked = JSONvehicle.isAvailable;
}

const edit_vehicle_button = document.getElementById("edit_vehicle_button");
edit_vehicle_button.addEventListener('click', function() {
    // Get values
    const formVehicle = document.getElementById("form-edit-vehicle");
    const formVhModel = formVehicle.elements["vehicle_model"].value;
    const formVhLicencePlate = formVehicle.elements["vehicle_licence_plate"].value;
    const formVhAddress = formVehicle.elements["vehicle_address"].value;
    const formVhModuleCode = formVehicle.elements["vehicle_module_code"].value;
    const formVhIsAvailable = formVehicle.elements["switch"].checked;
    
    console.log("vehicle: " + formVhModel + ", " + formVhLicencePlate + ", " + formVhAddress + ", " + formVhModuleCode + ", " + formVhIsAvailable);

    // Sent to android
    if(androidConnected()) Android.requestUpdateVehicle(JSONvehicle.id, formVhModel, formVhLicencePlate, formVhAddress.replace(/\'/g, "$"), JSONvehicle.addressId, formVhModuleCode, formVhIsAvailable);
});

const EDIT_VEHICLE_MODULE_CODE_INCORRECT = 1,
    EDIT_VEHICLE_MODULE_CODE_USED = 2,
    EDIT_VEHICLE_FAILED = 3;

const error_messages = {
    messages: [
        "",
        "Module code incorrect",
        "Module code unavailable",
        "Register failed"
    ],
    getErrorText: function (errorCode) {
        return this.messages[errorCode];
    },
};

//[ANDROID CALLBACK]
// Fail
// Stay in the current page
function updateVehicleResult(code) {
    // When function called by Android (cause is a string, not a number)
    if (typeof code == "string") code = parseInt(code);

    switch (code) {
        case EDIT_VEHICLE_MODULE_CODE_INCORRECT:
            console.error("EDIT_VEHICLE_MODULE_CODE_INCORRECT: " + error_messages.getErrorText(code));
            break;
        case EDIT_VEHICLE_MODULE_CODE_USED:
            console.error("EDIT_VEHICLE_MODULE_CODE_USED: " + error_messages.getErrorText(code));
            break;
        case EDIT_VEHICLE_FAILED:
            console.error("EDIT_VEHICLE_FAILED: " + error_messages.getErrorText(code));
            break;
    }
}

// ---- Input ----

const input_lp = document.getElementById("vehicle_licence_plate");

input_lp.addEventListener('input', () => {
    document.getElementById("err_lp").classList.remove("error-visible");
    input_lp.value = licencePlateFormater(input_lp.value);
});

input_lp.addEventListener('blur', () => {
    const value = input_lp.value;
    const regex = /^[A-Z]{2}-\d{3}-[A-Z]{2}$/;
    if (!regex.test(value)) {
        document.getElementById("err_lp").classList.add("error-visible");
    }
});

const input_mc = document.getElementById("vehicle_module_code");

input_mc.addEventListener('input', () => {
    console.log("input: " + input_mc.value)

    document.getElementById("err_mc").classList.remove("error-visible");
    input_mc.value = moduleCodeFormater(input_mc.value);
});

input_mc.addEventListener('blur', () => {
    console.log("blur: " + input_mc.value)

    const value = input_mc.value;
    const regex = /^#[0-9]{2}-[0-9]{2}-[0-9]{4}$/;
    if (!regex.test(value)) {
        document.getElementById("err_mc").classList.add("error-visible");
    }
});
