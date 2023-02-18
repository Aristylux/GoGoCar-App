// For callback
var vehicle_selected;

// Select vehicle & open popup
function openPopupCancelJourney(vehicle) {
    vehicle_selected = vehicle;
    openPopup(popup);
}

// Action popup (button)
let but_pop_back = document.getElementById("but_pop_back");
let but_pop_cancel = document.getElementById("but_pop_cancel");

// Return nothing
but_pop_back.addEventListener("click", function () {
    console.log("close");
    closePopup(popup);
});

// Cancel the journey
but_pop_cancel.addEventListener("click", function () {
    console.log("cancel");
    closePopup(popup);
    // Remove vehicle
    if (androidConnected()) Android.requestCancelJourney(vehicle_selected.id);
});

// [ANDROID CALLBACK]
function journeyDelete(success) {
    if (success === "true") {
        // Remove (hide) journey_selected
        const vehicles_container_info = document.querySelectorAll(".lp");
        vehicles_container_info.forEach((Element) => {
            if (Element.innerText === vehicle_selected.licencePlate) {
                console.log(Element);
                Element.parentElement.parentElement.parentElement.classList.add(
                    "journey-container--hidden"
                );
            }
        });
    }
}
