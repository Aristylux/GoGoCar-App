// Action popup (button)
let popup_button_goback = document.getElementById("but_pop_goback");
let popup_button_delete = document.getElementById("but_pop_delete");

popup_button_goback.addEventListener("click", function () {
    console.log("goback");
    closePopup(popup);
});

popup_button_delete.addEventListener("click", function () {
    console.log("delete");
    closePopup(popup);
    if (androidConnected()) Android.deleteUserAccount();
});