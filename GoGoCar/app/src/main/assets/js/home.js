// On load page:
if(androidConnected()) Android.requestData();
// For debug
else {
    vehicles = JSON.parse('[{"id":7,"name":"Renault Clio","licencePlate":"FR-456-RY","address":"12 rue du Pain","idOwner":6,"isAvailable":true,"isBooked":false,"idUser":0},{"id":8,"name":"Porsche 911","licencePlate":"TR-456-FH","address":"976 Avenue Jean","idOwner":6,"isAvailable":false,"isBooked":false,"idUser":0}]');

    vehicles.forEach((vehicle) => {
        addElement(vehicle);
    });

    const switchs = document.querySelectorAll(".switch_input");
    switchs.forEach(function (switch_input, index) {
        switch_input.addEventListener('change', (event) => {
            const box_nav = document.querySelectorAll(".box-nav");
            if (switch_input.checked) {
                console.log("Checked");
                box_nav.forEach((box) => {
                    console.log("log");
                    box.classList.add("disabled");
                });
            } else {
                console.log("Not checked");
                // User finish to drive
                box_nav.forEach((box) => {
                    console.log("log");
                    box.classList.remove("disabled");
                });
            }
        });
    });

    const button_cancel = document.querySelectorAll(".bt_cancel");
    button_cancel.forEach(function (cancel_input, index) {
        cancel_input.addEventListener('click', (event) => {
            // Open Popup: Are you sure?
            console.log("open");
            openPopupCancelJourney(vehicles[index]);
        });
    });
}

var switch_selected;
var isDriving = false;

// [ANDROID CALLBACK]
function setUserName(name){
    let nameElmt = document.getElementById("user_name");
    nameElmt.textContent = name;
}

// Get all car booked for the user
// [ANDROID CALLBACK]
function setVehicleBooked(_table_vehicle){
    var vehicles = JSON.parse(_table_vehicle);
    
    if (vehicles.length == 0){
        // Hide container
        //document.getElementById("no_vehicles").style.display = 'flex';
        console.log("no vehicle booked");
    } else {
        // Add booked vehicles to the list
        vehicles.forEach((vehicle) => {
            addElement(vehicle);
        });

        // Add interruption switch
        const switchs = document.querySelectorAll(".switch_input");
        switchs.forEach(function (switch_input, index) {
            switch_input.addEventListener('change', (event) => {
                if (switch_input.checked) {
                    console.log("Checked");
                    switch_selected = switchs[index];
                    // Verify if user can drive car
                    if(androidConnected()) Android.requestDrive(index);
                } else {
                    console.log("Not checked");
                    // User finish to drive
                    if(androidConnected()) Android.requestStopDrive();
                }
            });
        });

        // Add interruption cancel button
        const button_cancel = document.querySelectorAll(".bt_cancel");
        button_cancel.forEach(function (cancel_input, index) {
            cancel_input.addEventListener('click', (event) => {
                // Open Popup: Are you sure?
                console.log("open");
                openPopupCancelJourney(vehicles[index]);
            });
        });
    }
}


// Add element dynamically
function addElement(vehicle){
    const vehicle_info = [vehicle.name, vehicle.address, vehicle.licencePlate];

    // Create Container
    let li = document.createElement("li");
    li.classList.add("journey-container");

    // Infos
    let ul_infos = document.createElement("ul");
    li.appendChild(ul_infos);

    // Title
    let h2 = document.createElement("h2");
    h2.textContent = "Your trip with " + vehicle.name + "'s car";

    let li1 = document.createElement("li");
    li1.appendChild(h2);

    ul_infos.appendChild(li1);

    // Address
    let info1 = document.createElement("li");
    info1.setAttribute("class", "info");

    let i1 = document.createElement("i");
    i1.setAttribute("class", "fi fi-rr-map-marker");
    info1.appendChild(i1);

    let span1 = document.createElement("span");
    span1.textContent = vehicle.address;
    info1.appendChild(span1);

    ul_infos.appendChild(info1);

    // Id
    let info2 = document.createElement("li");
    info2.setAttribute("class", "info");

    let i2 = document.createElement("i");
    i2.setAttribute("class", "fi fi-rr-rectangle-barcode");
    info2.appendChild(i2);

    let span2 = document.createElement("span");
    span2.textContent = "Licence plate:"
    info2.appendChild(span2);

    let span3 = document.createElement("span");
    span3.setAttribute("class", "lp");
    span3.textContent = vehicle.licencePlate;
    info2.appendChild(span3);

    ul_infos.appendChild(info2);

    // Switch
    let div1 = document.createElement("div");
    div1.setAttribute("class", "drive_vehicle");
    li.appendChild(div1);

    let h3 = document.createElement("h3");
    h3.textContent = "Ready to drive?";
    div1.appendChild(h3);

    let div2 = document.createElement("div");
    div2.setAttribute("class", "switch_container");
    div1.appendChild(div2);

    let label = document.createElement("label");
    label.setAttribute("class", "switch");
    div2.appendChild(label);

    let input = document.createElement("input");
    input.setAttribute("type", "checkbox");
    input.setAttribute("class", "switch_input");
    label.appendChild(input);

    let span = document.createElement("span");
    span.setAttribute("class", "slider");
    label.appendChild(span);

    // Button
    let divBut = document.createElement("div");
    divBut.setAttribute("class", "buttons");
    li.appendChild(divBut);

    let but = document.createElement("button");
    but.setAttribute("class", "bt_cancel border-button");
    but.textContent = "Cancel";
    divBut.appendChild(but);

    //document.body.appendChild(li);
    const ul = document.getElementById("journey_list");
    ul.appendChild(li);
}


// Switch
// [ANDROID CALLBACK][main (bluetooth)]
function setSwitchState(state){
    if(state === "true"){
        switch_selected.checked = true;
    } else
        switch_selected.checked = false;
}

// [ANDROID CALLBACK]
/*
 *
 * allowedToDrive = 'true', 'error_code'
 */
function requestDriveCallback(allowedToDrive){
    const box_nav = document.querySelectorAll(".box-nav");

    if(allowedToDrive === "true"){
        // Open Popup : you can drive.
        if(androidConnected()) Android.showToast("You can drive :-)");

        // Stay in the home fragment 
        box_nav.forEach((box) => {
            box.classList.add("disabled");
        });
        isDriving = true;
    } else {
        switch_selected.checked = false;
        console.log("Error callback type: " + allowedToDrive);
        if (typeof allowedToDrive == "string") allowedToDrive = parseInt(allowedToDrive);

        switch (allowedToDrive) {
            case 4: //DRIVING_REQUEST_CAR_NOT_FOUND
                // Open Popup : you cannot drive.
                // You're not allowed to drive this car...
                if(androidConnected()) Android.showToast("You cannot drive, car not found.");
                break;
            case 6: //DRIVING_CONNECTION_DISCONNECTED (finish to drive)
                box_nav.forEach((box) => {
                    box.classList.remove("disabled");
                })
                isDriving = false;
                break;
            default:
                break;
        }
    }
}

// Navigation Boxes
const box_nav = document.querySelectorAll(".box-nav");
box_nav.forEach((box) => {
    box.addEventListener('click', (event) => {
        if (!isDriving && androidConnected()) Android.changePage(box.id.slice(8));
    })
});
