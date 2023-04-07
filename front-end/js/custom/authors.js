import * as lib  from "./methods.js";
import * as pag  from "./pagination.js";

const page = pag.getPage();
const SIZE_OF_PAGE = pag.SIZE_OF_PAGE;
const halfOfRequest = 'https://poems-back-end-app.herokuapp.com/api/authors';
const pageableParameters = `?page=${page}&size=${SIZE_OF_PAGE}`;
const userId = localStorage.getItem('id');


const main_content = document.querySelector(".main-content");
const find_field = document.querySelector('.type');
const find_button = document.querySelector('.find');


checkIsAuthorithation();

function checkIsAuthorithation() { 
  if (localStorage.getItem('token') == null)
    window.location = 'index.html';
}


loadData();


find_button.addEventListener('click', async () => {
    let value = find_field.value;
    main_content.innerHTML = getLoadingBlock();

    loadData(value);
});


async function loadData(value = '') {
    const loadingDataForInnering = getLoadingBlock();
    main_content.innerHTML = loadingDataForInnering;
    const requestUrl = makeRequestForSubscriptions(value);
    const allData = await getData(requestUrl);
    pag.setCorrectSettingForPagination(allData,page,'users.html',getDataParam());
    addData(allData);    
    updateSubbscriptions(allData.content);
}

function getLoadingBlock() { 
     return `<div class='data-loading'>
        <img src="images/icons/Spinner-1s-200px.gif"/>
    </div>`;

}

function makeRequestForSubscriptions(value) { 
    if (checkIsSubscriptions())
        return halfOfRequest+`/${userId}/subscriptions`+pageableParameters+`&name=${value}`;
    else if (checkIsSubscribers())
        return halfOfRequest+`/${userId}/subscribers`+pageableParameters+`&name=${value}`;
    else
        return halfOfRequest+pageableParameters+`&name=${value}`;

}

function checkIsSubscriptions() {
    let data = getDataParam();
    return data == 'subscriptions';
}


function checkIsSubscribers() {
    let data = getDataParam();
    return data == 'subscribers';
}

function getDataParam() { 
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('data');
}


async function getData(requestUrl) { 
    return lib.sendRequest('GET', requestUrl)
        .then(data => data)
        .catch(err => err);
}

function addData(data) { 
    if (data.message == 'Cannot find any authors') {
        main_content.innerHTML = `<div class="not-found"> <p>Авторів немає<p></div>`;
    } else { 
        main_content.innerHTML = '';
        innerData(data.content);
    }   
}

async function innerData(data) {
    for (let i = 0; i < data.length; i++) {
        let div = document.createElement("div");
        let dat = data[i];

        let userDataUrl = createUrl(dat);

        div.innerHTML = `
        <div class="main-user-block">
            <div class="user-image">
                    <img
                        src="${dat.imagePath}"
                        alt="author photo"
                    />
            </div>
            <div class="user-content">
                <div class="user-title">
                    <a href="${userDataUrl}">${dat.fullName}</a>
                </div>
                <div class="user-description">
                    ${getDescription(dat.description)}
                </div>
                <div class="user-details">
                    <div class="user-subscribers">
                        <img src="images/icons/contact.png" alt="" />
                        <p>${dat.amountSubscribers}</p>
                    </div>
                    <div class="subscribe">
                        <input class="button sub" type="button" value="${chooseCorrectValue(dat.subscribe)}" />
                    </div>
                </div>
            </div>
        </div>`;

        main_content.appendChild(div);
    }
}
function createUrl(data) { 
    if (data.id == userId)
        return 'account.html';
    return `poems.html?id=${data.id}&name=${data.fullName}`;
}

function getDescription(description) { 
    return description == null ? '' : description; 
}

function chooseCorrectValue(isSubscribe) { 
    return isSubscribe ? 'Відписатися' : 'Підписатися';
}



// -------------------------- subscribe ---------------------------------------------


function updateSubbscriptions(data) { 
    const subscribe_buttons = document.querySelectorAll('.subscribe');
    const subscribe_button_inputs = document.querySelectorAll('.subscribe input');
    const amountSubscribers = document.querySelectorAll('.user-subscribers p');

    for (let i = 0; i < subscribe_buttons.length; i++) {
        checkAuthorIsCurrentUser(subscribe_button_inputs[i],data[i]);
        subscribe_buttons[i].addEventListener('click', () => doEvent(data[i], subscribe_button_inputs[i], amountSubscribers[i]));
    }
}

function checkAuthorIsCurrentUser(button, data) {
    if (data.id == userId)
        button.style.display = 'none';
}


function doEvent(data, subscribe_button_input, amountSubscribers) {
    const updateRequest = halfOfRequest+`/${localStorage.getItem('id')}/subscriptions/${data.id}`;
        lib.sendRequest('PATCH', updateRequest)
            .catch(err => console.log(err));
        changeInputValue(subscribe_button_input,amountSubscribers);
}

function changeInputValue(button,amountSubscribers) { 
    var subscribers = amountSubscribers.textContent;
    if (button.getAttribute('value') == 'Відписатися') {
        amountSubscribers.textContent = parseInt(subscribers) - 1;
        button.setAttribute('value', 'Підписатися');
    } else { 
        amountSubscribers.textContent = parseInt(subscribers) + 1;
        button.setAttribute('value', 'Відписатися');
    }
}




