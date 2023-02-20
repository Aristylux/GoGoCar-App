const button_return = document.getElementById("sec_but_re");
button_return.addEventListener("click", function () {
    if (androidConnected()) Android.requestClosePanel();
});
