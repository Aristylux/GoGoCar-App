// Background
let modal = document.getElementById("modal_popup");

// Remove popup when cliked ouside
modal.addEventListener("click", function (event) {
    closeModal();
});

function setModal(isActive) {
    if (isActive === "true") openModal();
    else closeModal();
}

function openModal() {
    modal.style.visibility = "visible";
    modal.classList.add("open-modal");
}

function closeModal() {
    if (androidConnected()) Android.removeModal();
    modal.classList.remove("open-modal");
    setTimeout(function () {
        modal.style.visibility = "hidden";
    }, 350);
}
