// Action popup (button)
let popup_vehicle_button_close = document.getElementById("but_pop_veh_close");
let popup_vehicle_button_book = document.getElementById("but_pop_veh_book");


function openPopupBook() {
    let vehicle_name = document.getElementById("vehicle_name");
    let vehicle_position = document.getElementById("vehicle_position");

    vehicle_name.innerText = vehicle_selected.name;
    vehicle_position.innerText = vehicle_selected.address;

    openPopup(popup);
}

popup_vehicle_button_close.addEventListener("click", function () {
    console.log("close");
    closePopup(popup);
});

popup_vehicle_button_book.addEventListener("click", function () {
    console.log("book");
    closePopup(popup);
    // Open new html (booking)
    if (androidConnected()) Android.requestOpenBook(JSON.stringify(vehicle_selected));
});