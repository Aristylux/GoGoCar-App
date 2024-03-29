// data base test //
//data base vehicle 1
/*
            var vehicle_1 = {
                "id":1,
                "owner":{
                    "type":"company",
                    "name":"ISEN Yncréa",
                },
                "name":"Nissan GT",
                "licence-plate":"TE-157-RE",
                "picture":"none",
                "gogocar-module": {
                    "id":"#0-0001",
                    "mac-address":"FF:FF:FF:FF:FF:FF",
                },
                "address":{
                    "number":35,
                    "name":"Avenue champ de Mars",
                    "city":"Toulon",
                    "postal-code":83000,
                    "country":"FRANCE",
                },
                "coordinate": {
                    "latitude":43.121140,
                    "longitude":5.939126
                }
            }

            //data base vehicle 2
            var vehicle_2 = {
                "id": 2,
                "owner":{
                    "type":"individual",
                    "name":"Axel Mezade",
                },
                "name":"Peugeot 206",
                "licence-plate":"TA-058-PM",
                "picture":"none",
                "gogocar-module": {
                    "id":"#0-0002",
                    "mac-address":"FF:FF:FF:FF:FF:FF",
                },
                "address":{
                    "number":1,
                    "name":"Boulevard Jean Michel",
                    "city":"Montpellier",
                    "postal-code":34000,
                    "country":"FRANCE",
                },
                "coordinate": {
                    "latitude":43.121140,
                    "longitude":5.939126
                }
            }
            */
/*
            var vehicle_1 = {
                "id":1,
                "name":"Nissan GT",
                "address":"Avenue champ de Mars"
            }

            //data base vehicle 2
            var vehicle_2 = {
                "id": 2,
                "name":"Peugeot 206",
                "address":"Boulevard Jean Michel"
            }
            var vehicles = [vehicle_1, vehicle_2];
            setDatabase(vehicles);
*/

// Global Variables
var vehicles = [];
var vehicle_selected;
var index_vh = 0;

// Request database
if (androidConnected()) Android.requestDatabase();
else{
    let vehicle_j = JSON.parse('[{"id":7,"name":"Renault Clio","licencePlate":"FR-456-RY","address":"12 rue du Pain","idOwner":6,"isAvailable":true,"isBooked":false,"idUser":0},{"id":8,"name":"Porsche 911","licencePlate":"TR-456-FH","address":"976 Avenue Jean","idOwner":6,"isAvailable":false,"isBooked":false,"idUser":0}]');
    
    vehicle_j.forEach((vehicle) => {
        let element = addElement(vehicle, index_vh++);
        vehicles.push(vehicle);
        element.addEventListener("click", () => {
            console.log(vehicles[parseInt(element.id.substring(3))]);

            // Open popup 'book'
            vehicle_selected = vehicles[parseInt(element.id.substring(3))];
            openPopupBook();
        });
    });
}

// [ANDROID CALLBACK] Add vehicle
function cbDriveAddVehicle(_vehicle){
    let vehicle = JSON.parse(_vehicle.replace(/\$/g, "'"));
    let element = addElement(vehicle, index_vh++);
    vehicles.push(vehicle);

    element.addEventListener("click", () => {
        console.log(vehicles[parseInt(element.id.substring(3))]);
        // Open popup 'book'
        vehicle_selected = vehicles[parseInt(element.id.substring(3))];
        openPopupBook();
    });
}

// [ANDROID] Reset database for update
function resetDatabase(){
    clearVehicles();
    if (androidConnected()) Android.requestDatabase();
}

// Clear list
function clearVehicles(){
    console.log("clear list");
    let ul = document.getElementById("vehicles_list");

    // loop through all its child nodes
    let liElements = ul.getElementsByTagName("li");
    for (let i = liElements.length - 1; i >= 0; i--) { 
        ul.removeChild(liElements[i]); // remove the li element from the ul element
    }
}

// Add element to ul list
function addElement(vehicle, index) {
    const vehicle_info = [vehicle.name, vehicle.address];
    const icons = ["fi-sr-car-side", "fi-sr-map-marker"]

    // Create Container
    let li = document.createElement("li");
    li.classList.add("vehicle_container");
    li.id = "vh_" + index;

    vehicle_info.forEach((info, index) => {
        // Line
        let div = document.createElement("div");
        div.classList.add("info");

        // Icon
        let icon = document.createElement("i");
        icon.classList.add("fi");
        icon.classList.add(icons[index]);
        div.appendChild(icon);

        // Text
        let text = document.createElement("span");
        text.textContent = info;
        div.appendChild(text);
        
        li.appendChild(div);
    });
    const ul = document.getElementById("vehicles_list");
    ul.appendChild(li);
    
    return li;
}
