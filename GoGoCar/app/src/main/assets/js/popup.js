// Background
let modal = document.getElementById("modal_popup");

// Popups
let popup = document.getElementById("popup");

// Remove popup when cliked ouside
modal.addEventListener("click", function (event) {
    let clicked = false;
    let targetClickedElement = event.target;
    let i = 0;
    do {
        if (targetClickedElement == popup) {
            console.log("clicked inside");
            clicked = true;
        }
        targetClickedElement = targetClickedElement.parentNode;
    } while (targetClickedElement);
    if (clicked == false) {
        console.log("clicked ouside");
        closePopup(popup);
    }
});

function openPopup(_popup) {
    console.log("open");
    modal.style.visibility = "visible";
    modal.classList.add("open-modal");
    _popup.classList.add("open-popup");
}

function closePopup(_popup) {
    modal.classList.remove("open-modal");
    _popup.classList.remove("open-popup");
    setTimeout(function () {
        modal.style.visibility = "hidden";
    }, 350);
}