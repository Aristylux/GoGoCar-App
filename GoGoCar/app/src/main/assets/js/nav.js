let sections = document.querySelectorAll(".section_screen");

let nav_buttons = document.querySelectorAll(".nav__link");

nav_buttons.forEach((button) => {
    button.addEventListener("click", (event) => {
        // Remove color for all buttons
        nav_buttons.forEach((btn) => btn.classList.remove("nav__link--active"));
        sections.forEach((sec) => sec.hidden = true);

        // Set color for the specified button
        button.classList.add("nav__link--active");

        // Set content
        sections.forEach((sec) => {
            if(sec.id.slice(4) === button.id.slice(4))
                sec.hidden = false;
        });
    });
});