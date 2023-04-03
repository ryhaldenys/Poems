import * as lib  from "./methods.js";

const title = document.getElementById('title');
const body = document.getElementById('body');
const checkBox = document.getElementById('check');
const errorBlock = document.querySelectorAll('.error-message');

const requestUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${localStorage.getItem('id')}/poems`;

const createButton = document.querySelector('.create-button');
const refactorButton = document.querySelector('.refactor-button');
const MIN_TITLE_SIZE = 1;
const MIN_BODY_SIZE = 1;

let status;


if (createButton == null)
    refactorPoem();
else
    createPoem();

async function refactorPoem() { 
    await setDataInInputs();

    refactorButton.addEventListener('click', () => {
        handleErrors();
        let body = createUpdatePoemBody();
        let poemId = getPoemId();
        lib.sendRequestWithBody('PUT', requestUrl + '/' + poemId, body);
        window.location = 'my-poems.html';
    });    
}

async function setDataInInputs() { 
    let poemId = getPoemId();
    console.log(poemId);
    const getRequestUrl = `https://poems-back-end-app.herokuapp.com/api/poems/${poemId}`;
    let poem = await lib.sendRequest('GET', getRequestUrl)
                    .then(data => data)
                    .catch(err => err);

    title.value = poem.name;
    body.value = poem.text;
    status = poem.status;
}


function getPoemId() { 
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('poemId');
}

function createUpdatePoemBody() { 
    return {
        name:  title.value,
        text: body.value,
        status: status
    }
}

function createPoem() { 
    createButton.addEventListener('click', () => {
        handleErrors();
        let body = createBody();
        lib.sendRequestWithBody('POST', requestUrl, body);
        window.location = 'my-poems.html';
    });    
}


function handleErrors() {
    handleTitleErrors();
    handleBodyErrors();
}

function handleTitleErrors() { 
    if (!checkIsCorrectTitle())
        printTitleErrorMessage();
    else
        setCorrectStyles(title, errorBlock[0]);
}


function handleBodyErrors() { 
      if (!checkIsCorrectBody())
        printBodyErrorMessage();
      else
        setCorrectStyles(body, errorBlock[1]);
}

function checkIsCorrectTitle() { 
    let titleLength = title.value.length;
    return titleLength >= MIN_TITLE_SIZE;
}

function checkIsCorrectBody() { 
    let bodyLength = body.value.length;

    return bodyLength >= MIN_BODY_SIZE;
}

function printTitleErrorMessage(){ 
    setErrorStyles(title);
    errorBlock[0].textContent = errorMessage();
    throw new Error('Title contains not enough symbols');
}


function printBodyErrorMessage(){ 
    setErrorStyles(body);
    errorBlock[1].textContent = errorMessage();
    throw new Error('Body of the poem contains not enough symbols');
}

function setErrorStyles(input){ 
    input.style.border = '1px solid red';
}


function setCorrectStyles(input,errorBlock){ 
    input.style.border = '1px solid black';
    errorBlock.textContent = '';
}

function errorMessage() { 
    return 'Заповніть пусте поле';
}


function createBody() { 
    
    return {
        name:  title.value,
        text: body.value,
        status: getStatus()
    }
}

function getStatus() { 
    if (checkBox.checked)
        return 'PUBLIC';
    return 'PRIVATE';
}

