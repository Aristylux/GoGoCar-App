const qrCode_button = document.getElementById("qr_code");
qrCode_button.addEventListener("click", (event) => {
    if (androidConnected()) Android.openScanQRCode();
    // TEST
    else setQRCode("#00-00-0001");
});

// [ANDROID CALLBACK]
function setQRCode(qrCode) {
    if (qrCode !== "null") {
        document.getElementById("vehicle_module_code").value = qrCode;
    }
}
