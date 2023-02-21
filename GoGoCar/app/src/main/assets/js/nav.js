let nav_buttons = document.querySelectorAll(".nav__link");
let nav_icons = document.querySelectorAll(".nav__icon");

// Contain the active button
var currentButton = document.querySelector(".nav__link--active");
// Contain the clicked button
var selectedButton;

nav_buttons.forEach((button) => {
    button.addEventListener("click", (event) => {
        // If the user clicks on a button other than the current page
        if (currentButton.id.slice(4) !== button.id.slice(4)) {
            selectedButton = button;
            if (androidConnected()) Android.requestChangePage(button.id.slice(4));
            // For debug
            else pageChanged();
        }
    });
});

function pageChanged() {
    // Remove color for all buttons
    nav_buttons.forEach((btn) => btn.classList.remove("nav__link--active"));

    // Reset the icon
    nav_icons.forEach((icon) => {
        let classIco = icon.classList[2];
        if (classIco.includes("fi-sr-")) {
            icon.classList.remove(classIco);
            icon.classList.add("fi-rr-" + classIco.slice(6));
        }
    });

    // Set color for the specified button
    selectedButton.classList.add("nav__link--active");

    // Update current button
    currentButton = selectedButton;

    // Set icon
    nav_icons.forEach((icon) => {
        if (icon.id.slice(4) === selectedButton.id.slice(4)) {
            let classIco = icon.classList[2];
            icon.classList.remove(classIco);
            icon.classList.add("fi-sr-" + classIco.slice(6));
        }
    });
}
