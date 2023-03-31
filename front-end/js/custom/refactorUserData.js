import * as lib from "./methods.js";
import * as conf from "./confirm.js";

const name = document.getElementById('name');
const email = document.getElementById('email');
const description = document.getElementById('description');

const updateDataButton = document.querySelector('.update');
const errorBlocks = document.querySelectorAll('.refactor-block-two .error-block');

const MIN_USERNAME_LENGTH = 3;
const id = localStorage.getItem('id');


const updateRequestUrl = `http://localhost:8080/api/authors/${id}`;

updateDataButton.addEventListener('click', async () => {

    if (checkIsAnyAndPrintErrors()) {
        let body = createBody();
        conf.openConfirmForm("Ви дійсно хочете редагувати дані?").then(async response => {
            if (response == true) {
                const response =  await lib.sendRequestWithBody('PUT', updateRequestUrl, body)
                .then(content => content)
                .catch(error => error);
                localStorage.setItem('token', response.token);
        }
        })
    }
});



function checkIsAnyAndPrintErrors() { 
    let result;
    if (!checkIsValidName()) {
        errorBlocks[0].textContent = 'Ім\'я користувача повинно містити не менше ' + MIN_USERNAME_LENGTH+' символів';
        result = false;
    }
    
    if (!checkIsValidEmail()) {
        errorBlocks[1].textContent = 'Некоректна електронна адреса';
        result = false;
    }
    return true;
}

function createBody() { 
    return {
        fullName: name.value,
        email: email.value,
        description: description.value
    }
}


name.addEventListener('input', () => { 
    checkIsValidName();
});

function checkIsValidName() {
    var result;
    const nameValue = name.value;

    if (nameValue.length >= MIN_USERNAME_LENGTH) {
        name.style.border = '1px solid green';
        result = true;
    } else { name.style.border = '1px solid red';
        result = false;
    }
    return result;

}


email.addEventListener('input', () => { 
    checkIsValidEmail();
});


function checkIsValidEmail(){ 
    const emailValue = email.value;
    var result;
    if (validateEmail(emailValue)) {
        email.style.border = '1px solid green';
        result = true;
    }
    else {
        email.style.border = '1px solid red';
        result = false;
    }
    return result;
}

function validateEmail(email){
  return String(email)
    .toLowerCase()
    .match(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );
};
