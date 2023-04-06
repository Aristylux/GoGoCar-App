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
    JSONvehicle = JSON.parse(vehicle);

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
    if(androidConnected()) Android.requestUpdateVehicle(JSONvehicle.id, formVhModel, formVhLicencePlate, formVhAddress, formVhModuleCode, formVhIsAvailable);
});

//[ANDROID CALLBACK]
// Fail
// Stay in the current page
function updateVehicleResult(code) {
    console.log("code:" + code);
}