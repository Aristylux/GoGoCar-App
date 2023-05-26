/*
<!-- Popups message -->
<div id="modal_popup" class="modal">
    <!-- Popup message for book a vehicle -->
    <div id="popup" class="popup">
        <h1 id="vehicle_name">Vehicle Name</h1>
        <div id="popup_vehicle_information">
            <i id="ico_position" class="fi fi-rr-map-marker"></i>
            <span id="vehicle_position">Position</span>
        </div>
        <div id="popup_vehicle_buttons">
            <button id="but_pop_veh_book">Book</button>
            <button id="but_pop_veh_close" class="border-button">Close</button>
        </div>
    </div>
</div>

*/

// Background
let modal = document.getElementById("modal_popup");

// Popups
let popup = document.getElementById("popup");

// Remove popup when cliked ouside
modal.addEventListener("click", function (event) {
    let clicked = false;
    let targetClickedElement = event.target;
    do {
        if (targetClickedElement == popup) {
            //console.log("clicked inside");
            clicked = true;
        }
        targetClickedElement = targetClickedElement.parentNode;
    } while (targetClickedElement);
    if (clicked == false) {
        //console.log("clicked ouside");
        if (androidConnected()) Android.setModal(false);
        closePopup(popup);
    }
});

function openPopup(_popup) {
    if (androidConnected()) Android.setModal(true);
    document.body.style.overflow = 'hidden';
    modal.style.visibility = "visible";
    modal.classList.add("open-modal");
    _popup.classList.add("open-popup");
}

function closePopup(_popup) {
    document.body.style.overflow = 'auto';
    modal.classList.remove("open-modal");
    if (_popup) _popup.classList.remove("open-popup");
    else popup.classList.remove("open-popup");
    setTimeout(function () {
        modal.style.visibility = "hidden";
    }, 350);
}