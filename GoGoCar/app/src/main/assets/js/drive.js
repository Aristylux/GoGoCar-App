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

// Global Variable
var vehicles = [];
var vehicle_selected;

var i = 0;

// Request database
if (androidConnected()) Android.requestDatabase();
else{
    let vehicle_j = JSON.parse('[{"id":7,"name":"Renault Clio","licencePlate":"FR-456-RY","address":"12 rue du Pain","idOwner":6,"isAvailable":true,"isBooked":false,"idUser":0},{"id":8,"name":"Porsche 911","licencePlate":"TR-456-FH","address":"976 Avenue Jean","idOwner":6,"isAvailable":false,"isBooked":false,"idUser":0}]');
    
    vehicle_j.forEach((vehicle) => {
        let element = addElement(vehicle, i++);
        vehicles.push(vehicle);
        element.addEventListener("click", () => {
            console.log(vehicles[parseInt(element.id.substring(3))]);

            // Open popup 'book'
            vehicle_selected = vehicles[parseInt(element.id.substring(3))];
            openPopupBook();
        });
    });
/*
    const vehicles_container = document.querySelectorAll(".vehicle_container");
    vehicles_container.forEach(function (container, index) {
        container.addEventListener("click", (event) => {
            // Open popup 'book'
            vehicle_selected = vehicles[index];
            openPopupBook();
        });
    });
    */
}

// Not used
// [ANDROID CALLBACK] Retrive databases from android (result)
function setDatabase(_table_vehicle) {
    vehicles = JSON.parse(_table_vehicle);

    vehicles.forEach((vehicle) => {
        addElement(vehicle);
    });

    const vehicles_container = document.querySelectorAll(".vehicle_container");
    vehicles_container.forEach(function (container, index) {
        container.addEventListener("click", () => {
            // Open popup 'book'
            vehicle_selected = vehicles[index];
            openPopupBook();
        });
    });
}



// [ANDROID CALLBACK] Add vehicle
function addVehicle(_vehicle){
    let vehicle = JSON.parse(_vehicle);
    let element = addElement(vehicle, i++);
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
    var ul = document.getElementById("vehicles_list");

    // loop through all its child nodes
    let liElements = ul.getElementsByTagName("li");
    for (let i = liElements.length - 1; i >= 0; i--) { 
        ul.removeChild(liElements[i]); // remove the li element from the ul element
    }
    if (androidConnected()) Android.requestDatabase();
}

// Add element to ul list
function addElement(vehicle, index) {
    const vehicle_info = [vehicle.name, vehicle.address];

    // Create Container
    let li = document.createElement("li");
    li.classList.add("vehicle_container");
    li.id = "vh_" + index;
    vehicle_info.forEach((info) => {
        let div = document.createElement("div");
        div.appendChild(document.createTextNode(info));
        li.appendChild(div);
    });
    const ul = document.getElementById("vehicles_list");
    ul.appendChild(li);
    
    return li;
}
