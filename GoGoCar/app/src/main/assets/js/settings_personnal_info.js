// Request
if (androidConnected()) Android.requestPersonalInformation();

// [ANDROID CALLBACK]
function setUserInformation(userInfo) {
    let user_info = JSON.parse(userInfo);

    // Show informations
    let form_personal_info = document.getElementById("form-pi");
    form_personal_info["pi_user_profile_name"].value = user_info.name;
    form_personal_info["pi_user_email"].value = user_info.email;
    form_personal_info["pi_user_phone_numbre"].value = user_info.phone;
}
