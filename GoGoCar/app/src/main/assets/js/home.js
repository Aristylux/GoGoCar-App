// Global Variables
var switch_selected;
var isDriving = false;

// On load page:
if(androidConnected()) Android.requestData();
// For debug
else {
    vehicles = JSON.parse('[{"id":7,"name":"Renault Clio","licencePlate":"FR-456-RY","address":"12 rue du Pain","idOwner":6,"isAvailable":true,"isBooked":false,"idUser":0},{"id":8,"name":"Porsche 911","licencePlate":"TR-456-FH","address":"976 Avenue Jean","idOwner":6,"isAvailable":false,"isBooked":false,"idUser":0}]');

    vehicles.forEach((vehicle) => {
        //addElement(vehicle);
        createJourneyContainer(vehicle);
    });

    const switchs = document.querySelectorAll(".switch_input");
    switchs.forEach(function (switch_input, index) {
        switch_input.addEventListener('change', (event) => {
            const box_nav = document.querySelectorAll(".box-nav");

            const container = switch_input.parentElement.parentElement.parentElement.parentElement.parentElement;
            const cancel_container = container.getElementsByClassName('off-road-container')[0];
            const onroad_container = container.getElementsByClassName('on-road-container')[0];

            if (switch_input.checked) {
                console.log("Checked");
                // Disable box
                box_nav.forEach((box) => {
                    console.log("log");
                    box.classList.add("disabled");
                });

                // Hide cancel journey container
                switch_selected = switchs[index];
                console.log(switch_selected);
                
                cancel_container.classList.add('hidden');

                onroad_container.classList.remove('hidden');
                onroad_container.classList.add('display');
                onroad_container.style.display = 'block';
            } else {
                console.log("Not checked");
                // User finish to drive
                box_nav.forEach((box) => {
                    console.log("log");
                    box.classList.remove("disabled");
                });

                // Display cancel journey container
                cancel_container.classList.remove('hidden');

                // Hide on road container
                onroad_container.classList.add('hidden');
                onroad_container.classList.remove('display');
                
                setTimeout(() => {
                    onroad_container.style.display = 'none';
                }, 490);
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
        console.log("no vehicle booked");
    } else {
        // Add booked vehicles to the list
        vehicles.forEach((vehicle) => {
            createJourneyContainer(vehicle);
        });

        // Add interruption switch
        const switchs = document.querySelectorAll(".switch_input");
        switchs.forEach(function (switch_input, index) {
            switch_input.addEventListener('change', () => {
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
            cancel_input.addEventListener('click', () => {
                // Open Popup: Are you sure?
                console.log("open");
                openPopupCancelJourney(vehicles[index]);
            });
        });
    }
}


// Switch
// [ANDROID CALLBACK][main (bluetooth)]
function setSwitchState(state){
    if(state === "true"){
        switch_selected.checked = true;
    } else
        switch_selected.checked = false;
}

const DRIVING_REQUEST_PERMISSION_ERROR = 1,
    DRIVING_REQUEST_BLUETOOTH_DISABLED = 2,
    DRIVING_REQUEST_LOCALISATION_DISABLE = 3,
    DRIVING_REQUEST_CAR_NOT_FOUND = 4,
    DRIVING_CONNECTION_FAILED = 5,
    DRIVING_CONNECTION_DISCONNECTED = 6;

const error_messages = {
    messages: [
        "",
        "Permission denied",
        "Bluetooth disabled",
        "Location disabled",
        "You cannot drive, car not found.",
        "Connection failed",
        "Disconnected"
    ],
    getErrorText: function (errorCode) {
        return this.messages[errorCode];
    },
};

// [ANDROID CALLBACK]
/*
 *
 * allowedToDrive = 'true', 'error_code'
 */
function requestDriveCallback(allowedToDrive){
    const box_nav = document.querySelectorAll(".box-nav");

    if(allowedToDrive === "true"){
        // Open Popup : you can drive.
        console.log("You can drive :-)");

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
            case DRIVING_REQUEST_PERMISSION_ERROR:
            case DRIVING_REQUEST_BLUETOOTH_DISABLED:
            case DRIVING_REQUEST_LOCALISATION_DISABLE:
            case DRIVING_REQUEST_CAR_NOT_FOUND:
                // Open Popup : you cannot drive.
                // You're not allowed to drive this car...
            case DRIVING_CONNECTION_FAILED:
                console.error(error_messages.getErrorText(allowedToDrive));
                break;
            case DRIVING_CONNECTION_DISCONNECTED: //(finish to drive)
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
    box.addEventListener('click', () => {
        if (!isDriving && androidConnected()) Android.requestChangePage(box.id.slice(8));
    })
});

// Add element dynamically
function addElement(vehicle){
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

function createJourneyContainer(vehicle) {
    let journeyContainer = document.createElement('li');
    journeyContainer.className = 'journey-container';
  
    let mainContainer = document.createElement('div');
    mainContainer.className = 'main-container';
  
    let ul_infos = document.createElement('ul');
  
    let li1 = document.createElement('li');
    let h2 = document.createElement('h2');
    h2.textContent = "Your trip with " + vehicle.name + "'s car";
    li1.appendChild(h2);
    ul_infos.appendChild(li1);
  
    let li2 = document.createElement('li');
    li2.className = 'info';
    let icon1 = document.createElement('i');
    icon1.className = 'fi fi-rr-map-marker';
    let span1 = document.createElement('span');
    span1.textContent = vehicle.address;
    li2.appendChild(icon1);
    li2.appendChild(span1);
    ul_infos.appendChild(li2);
  
    let li3 = document.createElement('li');
    li3.className = 'info';
    let icon2 = document.createElement('i');
    icon2.className = 'fi fi-rr-rectangle-barcode';
    let span2 = document.createElement('span');
    span2.textContent = 'Licence plate:';
    let span3 = document.createElement('span');
    span3.className = 'lp';
    span3.textContent = vehicle.licencePlate;
    li3.appendChild(icon2);
    li3.appendChild(span2);
    li3.appendChild(span3);
    ul_infos.appendChild(li3);
  
    mainContainer.appendChild(ul_infos);
  
    let driveVehicle = document.createElement('div');
    driveVehicle.className = 'drive_vehicle';
  
    let h3 = document.createElement('h3');
    h3.textContent = 'Ready to drive?';
    driveVehicle.appendChild(h3);
  
    let switchContainer = document.createElement('div');
    switchContainer.className = 'switch_container';
  
    let label = document.createElement('label');
    label.className = 'switch';
  
    let input = document.createElement('input');
    input.type = 'checkbox';
    input.className = 'switch_input';
  
    let span4 = document.createElement('span');
    span4.className = 'slider';
  
    label.appendChild(input);
    label.appendChild(span4);
    switchContainer.appendChild(label);
    driveVehicle.appendChild(switchContainer);
  
    mainContainer.appendChild(driveVehicle);
  
    journeyContainer.appendChild(mainContainer);
  
    let offRoadContainer = document.createElement('div');
    offRoadContainer.className = 'off-road-container';
  
    let buttons = document.createElement('div');
    buttons.className = 'buttons';
  
    let cancelButton = document.createElement('button');
    cancelButton.className = 'bt_cancel border-button';
    cancelButton.textContent = 'Cancel';
  
    buttons.appendChild(cancelButton);
    offRoadContainer.appendChild(buttons);
  
    journeyContainer.appendChild(offRoadContainer);
  
    const ul = document.getElementById("journey_list");
    ul.appendChild(journeyContainer);
  }