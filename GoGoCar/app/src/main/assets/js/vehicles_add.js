
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

//[ANDROID CALLBACK]
// Fail
// Stay in the current page
function addVehicleResult(code) {
    console.log("addVehicleResult: [ERROR] code:" + code);
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
