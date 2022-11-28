
let button_toast = document.getElementById("button_toast");
let button_change_color = document.getElementById("button_change_color");
let button_reset_color = document.getElementById("button_reset_color");

button_change_color.addEventListener('click', function () {
    //changeColorBackground('#BB86FCFF');
    document.body.style.backgroundColor = '#BB86FCFF';
});

button_reset_color.addEventListener('click', function () {
    //changeColorBackground('#FFFFFF');
    document.body.style.backgroundColor = '#FFFFFF';
});

button_toast.addEventListener('click', function () {
    if(androidConnected()) Android.showToast("Toast");
});

function changeColorBackground (_color) {
    if(androidConnected()) Android.changeBackground(_color);
}

function dataReceived(_value) {
    document.body.style.backgroundColor = _value;
}

// -----------

function androidConnected(){
    if(typeof Android === 'undefined'){
        console.warn("Android undefined");
        return false;
    } else {
        return true;
    } 
}