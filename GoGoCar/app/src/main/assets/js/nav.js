let nav_buttons = document.querySelectorAll(".nav__link");
let nav_icons = document.querySelectorAll(".nav__icon");


nav_buttons.forEach((button) => {
    button.addEventListener("click", (event) => {

        // Get content page name
        let src = document.getElementById("content").src;

        // If the user clicks on a button other than the current page
        if(!src.includes('/pages/' + button.id.slice(4) + '.html')){
            // Remove color for all buttons
            nav_buttons.forEach((btn) => btn.classList.remove("nav__link--active"));
            // //sections.forEach((sec) => sec.hidden = true);

            // Reset the icon
            nav_icons.forEach((icon) => {
                let classIco = icon.classList[2];
                if(classIco.includes('fi-sr-')){
                    icon.classList.remove(classIco);
                    icon.classList.add('fi-rr-' + classIco.slice(6));
                }
            });

            // Set color for the specified button
            button.classList.add("nav__link--active");

            // Set new content
            document.getElementById("content").src = './pages/' + button.id.slice(4) + '.html';

            // Set icon
            nav_icons.forEach((icon) => {
                if(icon.id.slice(4) === button.id.slice(4)){
                    let classIco = icon.classList[2];
                    icon.classList.remove(classIco);
                    icon.classList.add('fi-sr-' + classIco.slice(6));
                }   
            });
        }
    });
});