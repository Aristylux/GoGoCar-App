const button_return = document.getElementById("button_return");
button_return.addEventListener("click", function () {
    if (androidConnected()) Android.requestClosePanel();
});