// Global Variables
var click = 0;
var last_index = 5; // none of the screens

// On load page:
if(androidConnected()) Android.requestUserName();

// [ANDROID CALLBACK]
function setUserName(name){
    let nameElmt = document.getElementById("user_name");
    nameElmt.textContent = name;
}

const PROFILE_PICTURE_SCREEN = 0,
    SELECT_LANGUAGE_SCREEN = 1,
    PERSONAL_INFO_SCREEN = 2,
    JOURNEY_HISTORY_SCREEN = 3,
    BANK_DETAIL_SCREEN = 4;

const buttons = document.querySelectorAll(".settings_button");
//const sections = document.querySelectorAll(".section_moving");

buttons.forEach((btn, index) => {
    btn.addEventListener("click", function () {
        if (last_index != index) click = 0;

        if (++click === 1){
            last_index = index;
            setTimeout(function() {
                click = 0;
            }, 500);
        } 
        else return;
        
        let page;
        switch (index) {
            case PROFILE_PICTURE_SCREEN:
                page = "profile";
                break;
            case SELECT_LANGUAGE_SCREEN:
                page = "languages";
                break;
            case PERSONAL_INFO_SCREEN:
                page = "personal_info";
                break;
            case JOURNEY_HISTORY_SCREEN:
                page = "history";
                break;
            case BANK_DETAIL_SCREEN:
                page = "bank_cards";
                break;
        }
        if(androidConnected()) Android.openSlider("settings", page);
    });
});

// delete account:
// Open popup "Are you sure ?"
const delete_account_button = document.getElementById("delete_account_button");
delete_account_button.addEventListener('click', function () {
    console.log("delete");
    openPopup(popup);
});

// Log out
const logOutButton = document.getElementById("log_out_button");
logOutButton.addEventListener('click', function () {
    console.log("logout");
    if(androidConnected()) Android.logout();
});