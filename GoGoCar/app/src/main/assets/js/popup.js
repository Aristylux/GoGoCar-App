// Background
let modal = document.getElementById("modal_popup");

// Popups
let popup_vehicle = document.getElementById("popup_vehicle");
//let popup_vehicle = document.getElementById("popup_vehicle");

// Action popup (button)
let popup_vehicle_button_close = document.getElementById("but_pop_veh_close");
let popup_vehicle_button_book = document.getElementById("but_pop_veh_book");

// [[From android]]
function openPopupBook(_vehicle_name, _vehicle_position) {
    let vehicle_name = document.getElementById("vehicle_name");
    let vehicle_position = document.getElementById("vehicle_position");

    vehicle_name.innerText = _vehicle_name;
    vehicle_position.innerText = _vehicle_position;

    openPopup(popup_vehicle);
}

function openPopup(_popup) {
    console.log("open");
    modal.style.visibility = "visible";
    modal.classList.add("open-modal");
    _popup.classList.add("open-popup");
}

// Remove popup when cliked ouside
modal.addEventListener("click", function (event) {
    let clicked = false;
    let targetClickedElement = event.target;
    let i = 0;
    do {
        if (targetClickedElement == popup_vehicle) {
            console.log("clicked inside");
            clicked = true;
        }
        targetClickedElement = targetClickedElement.parentNode;
    } while (targetClickedElement);
    if (clicked == false) {
        console.log("clicked ouside");
        closePopup(popup_vehicle);
    }
});

popup_vehicle_button_close.addEventListener("click", function () {
    console.log("close");
    closePopup(popup_vehicle);
});

popup_vehicle_button_book.addEventListener("click", function () {
    console.log("book");
    closePopup(popup_vehicle);
    // Open new html (booking)
});

function closePopup(_popup) {
    modal.classList.remove("open-modal");
    _popup.classList.remove("open-popup");
    setTimeout(function () {
        modal.style.visibility = "hidden";
    }, 350);
}
