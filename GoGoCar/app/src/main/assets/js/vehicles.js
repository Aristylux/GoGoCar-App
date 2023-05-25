// Global Variables

var vehicles = [];
var index_vh = 0;
var click_add = 0, click_edit = 0;

const EL_LI = 0, 
    EL_TRASH_ICON = 1, 
    EL_EDIT_ICON = 2;

const addVehicleButton = document.getElementById("add_vehicle_button");
addVehicleButton.addEventListener('click', () => {
    // Open new window for add new vehicle
    if (++click_add === 1)
        if(androidConnected()) Android.openSlider("vehicles", "add");
});

// Request database
if (androidConnected()) Android.requestUserVehicles();
// For debug on PC

else{
    let vh_test = [
        {"id":7,"name":"Renault Clio","licencePlate":"FR-456-RY","address":"12 rue du Pain","idOwner":6,"isAvailable":true,"isBooked":false,"idUser":0},
        {"id":8,"name":"Porsche 911","licencePlate":"TR-456-FH","address":"976 Avenue Jean","idOwner":6,"isAvailable":false,"isBooked":false,"idUser":0}
    ]
    var vehicles = vh_test;//JSON.parse('[]');
    console.log(vehicles.length);

    vehicles.forEach((vehicle) => {
        //document.getElementById("no_vh_logo").classList.add('logo-visible');
        setTimeout(() => {
            let elements = addElement(vehicle, index_vh++);
            vehicles.push(vehicle);

            elements[EL_TRASH_ICON].addEventListener('click', () => {
                console.log(JSON.stringify(vehicles[parseInt(elements[EL_LI].id.substring(3))]));
                // Open popup
                openPopupBook(vehicles[parseInt(elements[EL_LI].id.substring(3))]);
            });
    
            elements[EL_EDIT_ICON].addEventListener('click', () => {
                console.log(JSON.stringify(vehicles[parseInt(elements[EL_LI].id.substring(3))]));
            });
        }, 1000);
        
    });
}

// [ANDROID CALLBACK](requestUserVehicles)
function setResult(resultEmpty) {
    // If result empty -> Show 'no vehicle' message
    if (resultEmpty === "true") {
        document.getElementById("no_vh_logo").classList.add('logo-visible');
    }
}

// [ANDROID CALLBACK](requestUserVehicles) Add vehicle
function cbVehicleAddVehicle(_vehicle){
    let vehicle = JSON.parse(_vehicle.replace(/\$/g, "'"));
    let elements = addElement(vehicle, index_vh++);
    vehicles.push(vehicle);

    elements[EL_TRASH_ICON].addEventListener('click', () => {
        // Open popup
        openPopupBook(vehicles[parseInt(elements[EL_LI].id.substring(3))]);
    });

    elements[EL_EDIT_ICON].addEventListener('click', () => {
        // Open window edit
        console.log(JSON.stringify(vehicles[parseInt(elements[EL_LI].id.substring(3))]));

        if (++click_edit === 1)
            if (androidConnected()) Android.openSlider("vehicles", "edit", JSON.stringify(vehicles[parseInt(elements[EL_LI].id.substring(3))]));
    });
}

// [ANDROID] Reset database for update
function resetDatabase(){
    vehicles = [];
    index_vh = 0;
    click_add = 0;
    click_edit = 0;

    var ul = document.getElementById("vehicles_list"); 

    // loop through all the li elements in reverse order
    let liElements = ul.getElementsByTagName("li");
    for (let i = liElements.length - 1; i >= 0; i--) {
        ul.removeChild(liElements[i]); // remove the li element from the ul element
    }
    if (androidConnected()) Android.requestUserVehicles();
}

// Add element to ul list
function addElement(vehicle, index) {
    // Create Container
    let li = document.createElement("li");
    li.classList.add("vehicle_container");
    li.id = "vh_" + index;

    // Container Left
    let con_left = document.createElement("div");
    con_left.classList.add("vehicle_container_left");

    // Line model
    let vehicle_model = document.createElement("div");
    vehicle_model.classList.add("vehicle_model");
    vehicle_model.appendChild(document.createTextNode(vehicle.name));
    con_left.appendChild(vehicle_model);

    // Ligne Address
    let vehicle_info_add = document.createElement("div");
    vehicle_info_add.classList.add("info"); 

    let map_icon = document.createElement("i");
    map_icon.classList.add("fi");
    map_icon.classList.add("fi-sr-map-marker");
    vehicle_info_add.appendChild(map_icon);

    let address = document.createElement("span");
    address.appendChild(document.createTextNode(vehicle.address));
    vehicle_info_add.appendChild(address);
    con_left.appendChild(vehicle_info_add);

    // Ligne licence plate
    let vehicle_info_lp = document.createElement("div");
    vehicle_info_lp.classList.add("info"); 

    let bc_icon = document.createElement("i");
    bc_icon.classList.add("fi");
    bc_icon.classList.add("fi-sr-rectangle-barcode");
    vehicle_info_lp.appendChild(bc_icon);

    let licence = document.createElement("span");
    licence.appendChild(document.createTextNode(vehicle.licencePlate));
    vehicle_info_lp.appendChild(licence);
    con_left.appendChild(vehicle_info_lp);
    li.appendChild(con_left);
    
    // Container Right
    let con_right = document.createElement("div");
    con_right.classList.add("vehicle_container_right");

    // State
    let state = determineState(vehicle);
    let vehicle_state = document.createElement("div");
    vehicle_state.classList.add("state");
    vehicle_state.classList.add(state[0]);
    vehicle_state.appendChild(document.createTextNode(state[1]));
    con_right.appendChild(vehicle_state);

    // Icons
    let con_icon = document.createElement("div");
    con_icon.classList.add("ico");

    let remove_icon = document.createElement("i");
    remove_icon.classList.add("fi");
    remove_icon.classList.add("fi-sr-trash");
    con_icon.appendChild(remove_icon);

    let edit_icon = document.createElement("i");
    edit_icon.classList.add("fi");
    edit_icon.classList.add("fi-sr-pencil");
    con_icon.appendChild(edit_icon);
    con_right.appendChild(con_icon);
    li.appendChild(con_right);

    const ul = document.getElementById("vehicles_list");
    ul.appendChild(li);

    return [li, remove_icon, edit_icon];
}

/**
 * determine the vehicle state
 * 
 * return: class and text
 */
function determineState(vehicle){
    if(vehicle.isAvailable){
        if(vehicle.isBooked)
            return ["state--booked", "Booked"];
        else
            return ["state--available", "Available"];
    } else
        return ["state--unavailable", "Unavailable"];
}