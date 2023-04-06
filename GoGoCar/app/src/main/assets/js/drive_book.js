// Radio group
const radio_buttons = document.querySelectorAll(".form-radio");
radio_buttons.forEach(function (radio_button, index) {
    radio_button.addEventListener('click', (event) => {
        radio_buttons.forEach( (button) => button.classList.remove("radio--active"));
        radio_button.classList.add("radio--active");
    });
});

// Global var: Selected Vehicle
var JSONvehicle; // defined by setVehicle

// Request selected vehicle
if(androidConnected()) Android.requestGetVehicle();
else setVehicle('{"name":"tet", "address":"rue", "licencePlate":"5454"}'); // debug PC

// [ANDROID CALLBACK]
function setVehicle(vehicle){
    JSONvehicle = JSON.parse(vehicle);
    const infos = [JSONvehicle.name, JSONvehicle.address, JSONvehicle.licencePlate];

    const formInfo = document.getElementById("info_form");
    const radio_buttons = formInfo.querySelectorAll("span");
    radio_buttons.forEach((button, index) => {
        button.innerText = infos[index];
    });
}

// Submit Form
const book_vehicle_button = document.getElementById("book_vehicle_button");
book_vehicle_button.addEventListener('click', function() {
    // Get values
    const form = document.getElementById("book_form");
    const formPickupDate = form.elements["pickup_date"].value;
    const formDropDate = form.elements["drop_date"].value;
    //const formPersons = form.elements["person"].checked;

    // Sent to android
    if(androidConnected()) Android.requestBookVehicle(JSONvehicle.id, formPickupDate, formDropDate, 2);
});

//[ANDROID CALLBACK]
// Fail
// Stay in the current page
function updateVehicleResult(code) {
    console.log("code:" + code);
}