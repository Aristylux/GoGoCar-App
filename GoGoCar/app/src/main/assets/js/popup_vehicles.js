var vehicle_selected;

// Action popup (button)
let popup_vehicle_button_close = document.getElementById("but_pop_close");
let popup_vehicle_button_remove = document.getElementById("but_pop_remove");

function openPopupBook(vehicle) {
    let vehicle_name = document.getElementById("vehicle_name");
    let vehicle_position = document.getElementById("vehicle_position");

    vehicle_name.innerText = "Delete your " + vehicle.name + " ?";
    vehicle_position.innerText = vehicle.address;

    vehicle_selected = vehicle;

    openPopup(popup);
}

popup_vehicle_button_close.addEventListener("click", (event) => {
    if (androidConnected()) Android.setModal(false);
    closePopup(popup);
});

popup_vehicle_button_remove.addEventListener("click", function () {
    closePopup(popup);
    if (androidConnected()) Android.requestRemoveVehicle(vehicle_selected.id);
});

// [ANDROID CALLBACK]
function vehicleDelete(success) {
    if (success === "true") {
        // Remove (hide) vehicle_selected
        const vehicles_container_info = document.querySelectorAll(".info");
        vehicles_container_info.forEach((Element) => {
            if (Element.innerText === vehicle_selected.licencePlate) {
                //Element.parentElement.parentElement.style.display = "none";
                Element.parentElement.parentElement.classList.add(
                    "vehicle_container--hidden"
                );
            }
        });
    }
}
