const but_module = document.getElementById("have_module_buuton");
const but_no_module = document.getElementById("not_have_module_button");

var click_m = 0,
    click_no_m = 0; // disable multi click

but_module.addEventListener("click", () => {
    console.log("open 'vehicle_add_menu_module.html'");
    // Open new window for add new vehicle
    if (++click_m === 1) {
        if (androidConnected()) Android.openSlideTest("vehicles", "add");
        setTimeout(function () {
            click_m = 0;
        }, 500);
    }
});

but_no_module.addEventListener("click", () => {
    console.log("open 'vehicle_add_menu_no_module.html'");
    // Open new window for ask a module
    if (++click_no_m === 1) {
        if (androidConnected()) Android.openSlideTest("vehicles", "ask");
        setTimeout(function () {
            click_no_m = 0;
        }, 500);
    }
});
