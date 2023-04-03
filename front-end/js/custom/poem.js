import * as lib from "./methods.js";

const urlParams = new URLSearchParams(window.location.search);
const poemId = urlParams.get('id');

const requestUrl = 'https://poems-back-end-app.herokuapp.com/api/poems/' + poemId;
const main_container = document.querySelector('.main-container');

const htmlForInnering = `
    <div class='data-loading'>
        <img src="images/icons/Spinner-1s-200px.gif"/>
    </div>`;

main_container.innerHTML = htmlForInnering;


const data = await lib.sendRequest('GET', requestUrl)
    .then(data => data)
    .catch(err => console.log(err));

console.log(data);
innerData(data);


function innerData(data) { 

    const text = lib.addTegBrToEachLine(data.text, true);

    const page = `
        <div class="author-name">${data.authorName}</div>
        <div class="poem-container">
            <div class="title">${data.name}</div>
            <div class="text">
              ${text}
            </div>
        </div>`;

    main_container.innerHTML = page;
}