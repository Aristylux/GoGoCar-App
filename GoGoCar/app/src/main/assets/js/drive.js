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
// Request database
if (androidConnected()) Android.requestDatabase();

// Retrive databases from android (result)
function setDatabase(_table_vehicle) {
    var vehicles = JSON.parse(_table_vehicle);

    vehicles.forEach((vehicle) => {
        addElement(vehicle);
    });

    const vehicles_container = document.querySelectorAll(".vehicle_container");
    vehicles_container.forEach(function (container, index) {
        container.addEventListener("click", (event) => {
            // Open popup 'book'
            console.log(index);
            if (androidConnected()) Android.openPopupBook(vehicles[index].id);
        });
    });
}

// Add element to ul list
function addElement(vehicle) {
    console.log(vehicle.name);
    console.log(vehicle.address);
    let vehicle_info = [vehicle.name, vehicle.address];

    // Create Container
    let li = document.createElement("li");
    li.classList.add("vehicle_container");
    vehicle_info.forEach((info) => {
        let div = document.createElement("div");
        div.appendChild(document.createTextNode(info));
        li.appendChild(div);
    });
    const ul = document.getElementById("vehicles_list");
    ul.appendChild(li);
}
