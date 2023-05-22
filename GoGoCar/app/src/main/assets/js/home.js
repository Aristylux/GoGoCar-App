// Global Variables
var container_selected = {
    main_container: 0,
    cancel_container: 0,
    onroad_container: 0,
    switch_selected: 0,
    is_asking_for_driving: false,
    is_driving: false
}

// On load page:
if(androidConnected()) Android.requestData();
// For debug
else {
    vehicles = JSON.parse('[{"id":7,"name":"Renault Clio","licencePlate":"FR-456-RY","address":"12 rue du Pain","idOwner":6,"isAvailable":true,"isBooked":false,"idUser":0,"ownerName":"Isen M"},{"id":8,"name":"Porsche 911","licencePlate":"TR-456-FH","address":"976 Avenue Jean","idOwner":6,"isAvailable":false,"isBooked":false,"idUser":0,"ownerName":"Luis R"}]');

    vehicles.forEach((vehicle) => {
        createJourneyContainer(vehicle);
    });

    const switchs = document.querySelectorAll(".switch_input");
    switchs.forEach(function (switch_input, index) {
        switch_input.addEventListener('change', (event) => {

            container_selected.main_container = switch_input.parentElement.parentElement.parentElement.parentElement.parentElement;
            container_selected.cancel_container = container_selected.main_container.getElementsByClassName('off-road')[0];
            container_selected.onroad_container = container_selected.main_container.getElementsByClassName('on-road')[0];

            if (switch_input.checked) {
                if(container_selected.is_driving == true || container_selected.is_asking_for_driving == true){
                    switch_input.checked = false;
                    return;
                }
                console.log("Checked");
                container_selected.is_asking_for_driving = true;
                container_selected.switch_selected = switch_input;               
                openForDrive();
                container_selected.is_driving = true;
            } else {
                console.log("Not checked");
                // User finish to drive
                container_selected.is_asking_for_driving = false;
                closeForStop();
                container_selected.is_driving = false;
            }
        });
    });

    const button_cancel = document.querySelectorAll(".bt_cancel");
    button_cancel.forEach(function (cancel_input, index) {
        cancel_input.addEventListener('click', (event) => {
            if(container_selected.is_driving == true || container_selected.is_asking_for_driving == true){
                return;
            }
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
                // verify if drive or not

                container_selected.main_container = switch_input.parentElement.parentElement.parentElement.parentElement.parentElement;
                container_selected.cancel_container = container_selected.main_container.getElementsByClassName('off-road')[0];
                container_selected.onroad_container = container_selected.main_container.getElementsByClassName('on-road')[0];    

                if (switch_input.checked) {
                    if(container_selected.is_driving == true || container_selected.is_asking_for_driving == true){
                        switch_input.checked = false;
                        console.log("You can't start another journey during a journey");
                        return;
                    }
                    console.log("Checked");
                    container_selected.is_asking_for_driving = true;
                    container_selected.switch_selected = switch_input;
                    // Verify if user can drive car
                    if(androidConnected()) Android.requestDrive(index);
                } else {
                    console.log("Not checked");
                    container_selected.is_asking_for_driving = false;
                    // User finish to drive
                    if(androidConnected()) Android.requestStopDrive();
                }
            });
        });

        // Add interruption cancel button
        const button_cancel = document.querySelectorAll(".bt_cancel");
        button_cancel.forEach(function (cancel_input, index) {
            cancel_input.addEventListener('click', () => {
                // Verify if drive or not
                if(container_selected.is_driving == true || container_selected.is_asking_for_driving == true){ 
                    console.log("You can't cancel during a journey");
                    return;
                }
                // Open Popup: Are you sure?
                openPopupCancelJourney(vehicles[index]);
            });
        });
    }
}


// Switch
// [ANDROID CALLBACK][main (bluetooth)]
function setSwitchState(state){
    if(state === "true"){
        container_selected.switch_selected.checked = true;
        //openForDrive();
    } else {
        container_selected.switch_selected.checked = false;
        //closeForStop();
    }
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
    container_selected.is_asking_for_driving = false;
    if(allowedToDrive === "true"){
        // Open Popup : you can drive.
        console.log("You can drive :-)");
        openForDrive();
        container_selected.is_driving = true;
    } else {
        container_selected.switch_selected.checked = false;
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
                closeForStop();
                container_selected.is_driving = false;
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
        console.log(container_selected);
        if (!container_selected.is_driving && androidConnected()) Android.requestChangePage(box.id.slice(8));
    })
});

function openForDrive(){
    // Stay in the home fragment 
    box_nav.forEach((box) => {
        box.classList.add("disabled");
    });
    
    container_selected.cancel_container.classList.add('hidden');

    container_selected.onroad_container.classList.remove('hidden');
    container_selected.onroad_container.classList.add('display');
    container_selected.onroad_container.style.display = 'grid';
}

function closeForStop(){
    // Display box navigation 
    box_nav.forEach((box) => {
        box.classList.remove("disabled");
    });

    // Display cancel journey container
    container_selected.cancel_container.classList.remove('hidden');

    // Hide on road container
    container_selected.onroad_container.classList.add('hidden');
    container_selected.onroad_container.classList.remove('display');
    
    setTimeout(() => {
        container_selected.onroad_container.style.display = 'none';
    }, 490);
}

/* ----- ----- */


const PID = {
    SPEED: {
        place: 0,
        min: 0,
        max: 255
    },
    FUEL_LEVEL: {
        place: 1,
        min: 0,
        max: 100
    },
    FUEL_CONSO: {
        place: 2,
        min: 0,
        max: 100
    },
    ENGINE_COOLANT: {
        place: 3,
        min: -40,
        max: 210
    },
    ENGINE_WATER: {
        place: 4,
        min: -40,
        max: 215
    },
    ENGINE_OIL: {
        place: 5,
        min: -40,
        max: 210
    }
}

/**
 * [ANDROID CALLBACK]
 * Convert PID to real value / print id and modify circle
 * @param {string} speedValue
 */
function updateSpeedValue(speedValue) {
    let dashoffset = convertToCircle(speedValue, PID.SPEED);
    updateCircle(speedValue + "km/h", container_selected.onroad_container, PID.SPEED, dashoffset);
}

/**
 * [ANDROID CALLBACK]
 * @param {string} fuelValue 
 */
function updateFuelLevel(fuelValue) {
    let dashoffset = convertToCircle(fuelValue, PID.FUEL_LEVEL);
    updateCircle(fuelValue + "%", container_selected.onroad_container, PID.FUEL_LEVEL, dashoffset);
}

/**
 * [ANDROID CALLBACK]
 * @param {string} fuelValue 
 */
function updateFuelConsumption(fuelValue) {
    let dashoffset = convertToCircle(fuelValue, PID.FUEL_CONSO);
    updateCircle(fuelValue + "%", container_selected.onroad_container, PID.FUEL_CONSO, dashoffset);
}

/**
 * [ANDROID CALLBACK]
 * @param {string} fuelValue 
 */
function updateEngineCoolant(value) {
    let dashoffset = convertToCircle(value, PID.ENGINE_COOLANT);
    updateCircle(value + "°C", container_selected.onroad_container, PID.ENGINE_COOLANT, dashoffset);
}

/**
 * [ANDROID CALLBACK]
 * @param {string} fuelValue 
 */
function updateEngineWater(value) {
    let dashoffset = convertToCircle(value, PID.ENGINE_WATER);
    updateCircle(value + "°C", container_selected.onroad_container, PID.ENGINE_WATER, dashoffset);
}

/**
 * [ANDROID CALLBACK]
 * @param {string} fuelValue 
 */
function updateEngineOil(value) {
    let dashoffset = convertToCircle(value, PID.ENGINE_OIL);
    updateCircle(value + "°C", container_selected.onroad_container, PID.ENGINE_OIL, dashoffset);
}

function convertToCircle(entryValue, type){
    const dashMax = 95;
    const dashMin = 280;
    let value = parseInt(entryValue, 10);
    let progress = ((value - type.min) / (type.max - type.min)) * (dashMax - dashMin) + dashMin;
    return progress;
}

function updateCircle(value, container, type, circleValue){
    // Get the speed element by its parameter name
    let element = container.querySelectorAll('.inner .text')[type.place];
    element.textContent = value;
  
    // Update the stroke-dashoffset property
    let circleElement = container.querySelectorAll('circle')[type.place];    
    circleElement.style.strokeDashoffset = circleValue;
}

/* ----- ----- */

// Example usage:
//let fuelParameter = createParameterElement('Fuel Level', '70%', 'grad-one');
//document.body.appendChild(fuelParameter);
function createParameterElement(parameterName, paramerValue, colorCircle) {
    let parameter = document.createElement('div');
    parameter.setAttribute("class", 'parameter');

    let figure = document.createElement('div');
    figure.setAttribute("class", 'figure');
  
    let circular = document.createElement('div');
    circular.setAttribute("class", 'circular');
  
    let outer = document.createElement('div');
    outer.setAttribute("class", 'outer');
  
    let inner = document.createElement('div');
    inner.setAttribute("class", 'inner');
  
    let text = document.createElement('div');
    text.setAttribute("class", 'text');
    text.textContent = paramerValue;
  
    inner.appendChild(text);
    outer.appendChild(inner);
    circular.appendChild(outer);
    figure.appendChild(circular);
  
    let svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('version', '1.1');
  
    let circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    circle.setAttribute('stroke-linecap', 'round');
    circle.setAttribute("class", colorCircle);
  
    svg.appendChild(circle);
    circular.appendChild(svg);
  
    let div = document.createElement('div');
    div.setAttribute("class", 'parameter-name');

    let span = document.createElement('span');
    span.textContent = parameterName;
    div.appendChild(span);
  
    parameter.appendChild(figure);
    parameter.appendChild(div);
  
    return parameter;
}

// Example usage:
//let onRoadContainer = createOnRoadContainer();
//document.body.appendChild(onRoadContainer);
function createOnRoadContainer() {
    let onRoadContainer = document.createElement('div');
    onRoadContainer.setAttribute("class", 'on-road container hidden');
  
    let row1 = document.createElement('div');
    row1.setAttribute("class", 'row');
  
    row1.appendChild(createParameterElement('Vehicle Speed', '56km/h', 'grad-one'));
    row1.appendChild(createParameterElement('Fuel Level', '70%', 'grad-one'));
    row1.appendChild(createParameterElement('Fuel Consumption', '3L/h', 'grad-one'));
    onRoadContainer.appendChild(row1);
  
    let row2 = document.createElement('div');
    row2.setAttribute("class", 'row');
  
    row2.appendChild(createParameterElement('Engine Coolant', '90°C', 'grad-one'));
    row2.appendChild(createParameterElement('Engine Water', '90°C', 'grad-one'));
    row2.appendChild(createParameterElement('Engine Oil', '90°C', 'grad-one'));
    onRoadContainer.appendChild(row2);
  
    return onRoadContainer;
}
  
// Example usage:
//var offRoadContainer = createOffRoadContainer();
//document.body.appendChild(offRoadContainer);
function createOffRoadContainer() {
    let offRoadContainer = document.createElement('div');
    offRoadContainer.setAttribute("class",'off-road container');
  
    let buttons = document.createElement('div');
    buttons.setAttribute("class", 'buttons');
  
    let cancelButton = document.createElement('button');
    cancelButton.setAttribute("class", 'bt_cancel border-button');
    cancelButton.textContent = 'Cancel';
  
    buttons.appendChild(cancelButton);
    offRoadContainer.appendChild(buttons);
  
    return offRoadContainer;
}

// Example usage:
//let mainContainer = createMainContainer();
//document.body.appendChild(mainContainer);
function createMainContainer(vehicle) {
    let mainContainer = document.createElement('div');
    mainContainer.setAttribute("class", 'main container');
  
    let ul = document.createElement('ul');
  
    let li1 = document.createElement('li');
    let h2 = document.createElement('h2');

    h2.textContent = "Your trip with " + vehicle.ownerName.split(' ')[0] + "'s car";
    li1.appendChild(h2);
    ul.appendChild(li1);

    // Vehicle name
    let li_vn = document.createElement('li');
    li_vn.setAttribute("class", 'info');
    let icon_vn = document.createElement('i');
    icon_vn.setAttribute("class", 'fi fi-rr-car-side');
    let vehicle_n = document.createElement('span');
    vehicle_n.textContent = vehicle.name;
    li_vn.appendChild(icon_vn);
    li_vn.appendChild(vehicle_n);
    ul.appendChild(li_vn);

    // Vehicle position
    let li2 = document.createElement('li');
    li2.setAttribute("class", 'info');
    let icon1 = document.createElement('i');
    icon1.setAttribute("class", 'fi fi-rr-map-marker');
    let span1 = document.createElement('span');
    span1.textContent = vehicle.address;
    li2.appendChild(icon1);
    li2.appendChild(span1);
    ul.appendChild(li2);
  
    // Vehicle licence plate
    let li3 = document.createElement('li');
    li3.setAttribute("class", 'info');
    let icon2 = document.createElement('i');
    icon2.setAttribute("class", 'fi fi-rr-rectangle-barcode');
    let span2 = document.createElement('span');
    span2.textContent = 'Licence plate:';
    let span3 = document.createElement('span');
    span3.setAttribute("class", 'lp');
    span3.textContent = vehicle.licencePlate;
    li3.appendChild(icon2);
    li3.appendChild(span2);
    li3.appendChild(span3);
    ul.appendChild(li3);
  
    mainContainer.appendChild(ul);
  
    let driveVehicle = document.createElement('div');
    driveVehicle.setAttribute("class", 'drive_vehicle');
  
    let h3 = document.createElement('h3');
    h3.textContent = 'Ready to drive?';
    driveVehicle.appendChild(h3);
  
    let switchContainer = document.createElement('div');
    switchContainer.setAttribute("class", 'switch_container');
  
    let label = document.createElement('label');
    label.setAttribute("class", 'switch');
  
    let input = document.createElement('input');
    input.type = 'checkbox';
    input.setAttribute("class", 'switch_input');
  
    let span4 = document.createElement('span');
    span4.setAttribute("class", 'slider');
  
    label.appendChild(input);
    label.appendChild(span4);
    switchContainer.appendChild(label);
    driveVehicle.appendChild(switchContainer);
  
    mainContainer.appendChild(driveVehicle);
  
    return mainContainer;
}
  
// create the complete container
function createJourneyContainer(vehicle) {
    let journeyContainer = document.createElement('li');
    journeyContainer.className = 'journey-container';

    journeyContainer.appendChild(createMainContainer(vehicle));
    journeyContainer.appendChild(createOffRoadContainer());
    journeyContainer.appendChild(createOnRoadContainer());
  
    const ul = document.getElementById("journey_list");
    ul.appendChild(journeyContainer);
}