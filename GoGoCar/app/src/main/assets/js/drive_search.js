const text_input = document.getElementById("text_input");
const clear_input = document.getElementById("search-clear");
const distance_but = document.getElementById("distance");
const distance_container = document.getElementById('distance-container');
const cityList = document.getElementById("cityList");
const transitionTime = convertToMilliseconds(window.getComputedStyle(distance_container).getPropertyValue("transition-duration"));

let search = {
    "distance": 10,
    "city_name":"",
    "city_selected": false
}

// When user press a key
text_input.addEventListener('input', () => {
    text_input.value = formater(text_input.value);
    clear_input.classList.add('display');

    // Clear previous results list
    cityList.innerHTML = "";

    if(text_input.value.length == 0){
        clear_input.classList.remove('display');
        cityList.classList.remove("show");
        return;
    }
   
    // Ask to android
    if (androidConnected()) Android.searchFrom(text_input.value.toLowerCase());
    // Test
    else setMatchingCities('["Marseille","Monpellier","Nimes"]');
});

// [ANDROID CALLBACK]
function setMatchingCities(cities){
    let t_cities = JSON.parse(cities);

    // Display the matching cities
    cityList.classList.remove("show");
    t_cities.forEach(function(city) {
        let li = document.createElement("li");
        li.appendChild(document.createTextNode(city));
        // Add click event listener to each list item
        li.addEventListener("click", function() {
            text_input.value = city;
            search.city_name = city;
            search.city_selected = true;
            // Send Search to android
            startSearch();

            // Clear the list after selection
            cityList.innerHTML = "";
            cityList.classList.remove("show");
        });
        cityList.appendChild(li);
    });
    cityList.classList.add("show");
}

// When click on the icon, clear input
clear_input.addEventListener('click', () => {
    // Clear previous results list
    cityList.innerHTML = "";
    cityList.classList.remove("show");
    search.city_name = "";
    search.city_selected = false;

    // Clear input text
    text_input.classList.add('clear');
    setTimeout(() => {
        text_input.value = "";
        text_input.classList.remove('clear');
    }, 150);
    clear_input.classList.remove('display');
    resetDatabase();
});

// When clicked, open container
distance_but.addEventListener('click', () => {
    if(distance_container.style.display === "block") hideContainer(distance_container);
    else displayContainer(distance_container);
});

const distances = distance_container.querySelectorAll('li');
distances.forEach(element => {
    element.addEventListener('click', () => {
        hideContainer(distance_container);
        updateDistance(distance_but, element);
        
        search.distance = element.textContent.slice(0, -2);
        console.log("-> " + search.distance);

        // Send Android
        startSearch();
    })
});

function startSearch(){
    console.log("distance: " + search.distance + " - city: " + search.city_name + " - selected: " + search.city_selected + ".");
    if (search.city_selected == true){
        console.log("send to android: startSearch(" + search.city_name + ", " + parseInt(search.distance, 10) + ")");
        if (androidConnected()){
            Android.searchStart(search.city_name, parseInt(search.distance, 10));
            clearVehicles();
        } 
    }
}

function hideContainer(container) {
    container.classList.remove('container-visible');
    setTimeout(() => {
        container.style.display = 'none';
    }, transitionTime);
}

function displayContainer(container){
    container.style.display = 'block';
    // Avoid unnecessary visual artefact
    setTimeout(() => {
        container.classList.add('container-visible');
    }, 10);
}

function updateDistance(text, element) {
    text.classList.add('desappear');
    setTimeout(() => {
        text.textContent = element.textContent;
        text.classList.remove('desappear');
    }, 100);
}

function convertToMilliseconds(timeString){
    // Remove non-numeric characters from the string
    const numericString = timeString.replace(/[^0-9.]/g, '');
    // Convert the numeric string to a number and multiply by 1000
    return parseFloat(numericString) * 1000;    
}

function formater(text){
    // Return an empty string for non-string inputs
    if (typeof text !== 'string') return ''; 
    
    // Return the input as is for an empty string
    if (text.length === 0) return text; 

    const firstLetter = text.charAt(0).toUpperCase();
    const restOfText = text.slice(1);
    
    return firstLetter + restOfText;
}