function androidConnected(){
    if(typeof Android === 'undefined'){
        console.warn("Android undefined");
        return false;
    } else {
        return true;
    } 
}