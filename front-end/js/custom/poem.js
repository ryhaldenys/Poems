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
    innerTitle(data.name);
    addMetaDescription(data);
}

function innerTitle(name) { 
    const title = document.querySelector('title');
    title.innerText = name;
}

function addMetaDescription(data) { 
     const metaDescriptionTag = document.querySelector('meta[name="description"]');
    metaDescriptionTag.setAttribute('content', `Прочитайте вірш "${data.name}" від автора ${data.authorName}.`);
}