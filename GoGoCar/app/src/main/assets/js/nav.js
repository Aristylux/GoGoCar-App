let nav_buttons = document.querySelectorAll(".nav__link");
let nav_icons = document.querySelectorAll(".nav__icon");


nav_buttons.forEach((button) => {
    button.addEventListener("click", (event) => {

        // Get content page name
        //let src = document.getElementById("content").src;
        let src = document.location.href;

        // If the user clicks on a button other than the current page
        if(!src.includes('/pages/' + button.id.slice(4) + '.html')){

            if(androidConnected()) Android.changePage(button.id.slice(4));

            /*
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
            //Android.changePage();

            // Set icon
            nav_icons.forEach((icon) => {
                if(icon.id.slice(4) === button.id.slice(4)){
                    let classIco = icon.classList[2];
                    icon.classList.remove(classIco);
                    icon.classList.add('fi-sr-' + classIco.slice(6));
                }   
            });
            */
        }
    });
});