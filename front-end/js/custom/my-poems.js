import * as lib from './methods.js';

const requestUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${localStorage.getItem('id')}/poems/own?sort=createdAt,desc`;

const poems = document.querySelector('.poems');

const htmlForInnering = `
    <div class='data-loading'>
        <img src="images/icons/Spinner-1s-200px.gif"/>
    </div>`;

poems.innerHTML = htmlForInnering;


const response = await lib.sendRequest('GET', requestUrl)
    .then(data => data.content)
    .catch(err => err);

addData(response);

function addData(data) {  
    if (data.message == 'Cannot find any poems') {
        poems.innerHTML = '<div class="not-found"> <p>У вас ще немає власних поем</div><p>';
    } else { 
        poems.innerHTML = '';
        getData(data);
    }   
}
    
function getData(data){ 
    for (let i = 0; i < data.length; i++) {
        var div = document.createElement("div");
        innerData(div, data[i]);
        poems.appendChild(div);
  }
}

function innerData(div, data) { 
    var text = lib.addTegBrToEachLine(data.text);
    div.innerHTML =
    `<div class="poem">
    <div class="poem-description">
        <div class="poem-title">
            ${data.name} <span>(${data.status.toLowerCase()})</span>
        </div>
        <div class="poem-containing">
            <div class="text">
                ${text}
            </div>
            <a href="poem.html?id=${data.id}" class="read-more">Відкрити повністю</a>
            <div class="likes">
                <img src="${lib.isLike(data.like,"black")}" alt="">
                <p>${data.amountLikes}</p>
            </div>
        </div>
    </div>
    <div class="poem-buttons">
        <div class="refactor-poem">
            <input type="button" class ="button" onclick="window.location='refactor-poem.html?poemId=${data.id}';" value="Редагувати">
        </div>
        <div class="delete-poem">
            <input type="button" class ="button" value="Видалити">
                </div>
            <div class="hide-poem">
                <input type="button" class ="button"  value="${changeButtonValue(data.status)}">
            </div>
        </div>
    </div>`;
}

function changeButtonValue(status){ 
    if (status == 'PUBLIC') 
        return 'Приховати';
    return 'Опублікувати';
}


const likeButtons = document.querySelectorAll('.poems img');
const countLikesFields = document.querySelectorAll('.poems p');

lib.updateLike(likeButtons, countLikesFields, response,"black");


/////////////////////////////delete poems


const delete_blocks = document.querySelectorAll('.delete-poem');
const poem_filds = document.querySelectorAll('.poem');

deletePoem(response);

function deletePoem(data) {   
    for (let i = 0; i < delete_blocks.length; i++){
        delete_blocks[i].addEventListener('click', () => {
            const deleteRequest = `https://poems-back-end-app.herokuapp.com/api/authors/${localStorage.getItem('id')}/poems/${data[i].id}`;
            lib.sendRequest('DELETE', deleteRequest)
                .catch(err => console.log(err));
            
            poem_filds[i].remove();
        });
    }    
}


//////////////////////////hide poem

const statuses = document.querySelectorAll('.poem-title span');
const update_button = document.querySelectorAll('.hide-poem');
const update_button_input = document.querySelectorAll('.hide-poem input');

updatePoemsStatus(response);



function updatePoemsStatus(data) { 
    for (let i = 0; i < update_button.length; i++) {
        update_button[i].addEventListener('click', () => { 
            const updateUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${localStorage.getItem('id')}/poems/${data[i].id}`;
            let status = getNewStatus(statuses[i]);
            let body = createBody(data[i].name, data[i].text, status);
            lib.sendRequestWithBody('PUT', updateUrl, body)
                .catch(err => console.log(err));
            setAttributeValue(update_button_input[i], status);
            statuses[i].innerText = `(${status.toLowerCase()})`;
        })
    }
}

function getNewStatus(status) { 
    if (status.textContent == '(public)')
        return 'PRIVATE';
    return 'PUBLIC';
}

function createBody(name, text, status) { 
    return {
        name: name,
        text: text,
        status: status
    }
};

function setAttributeValue(update_button,status) { 
    if (status == 'PUBLIC') {
        update_button.setAttribute('value', 'Приховати');
    } else { 
        update_button.setAttribute('value', 'Опублікувати');
    }
}

