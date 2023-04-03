import * as lib from "./methods.js";

const oldPassword = document.getElementById('old');
const newPassword = document.getElementById('new');
const errorBlock = document.querySelector('.error-block');
const changePasswordButton = document.querySelector('.change-password-btn');

const MIN_PASSWORD_LENGTH = 5;
const id = localStorage.getItem('id');


const requestUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${id}/password`;
changePasswordButton.addEventListener('click', async () => { 

    if (checkPasswordLength(oldPassword) && checkPasswordLength(newPassword)) {
        const body = {
            oldPassword: oldPassword.value,
            newPassword: newPassword.value
        }
        const error = await lib.sendRequestWithBody('PATCH', requestUrl,body)
            .catch(error => error);
        printErrorMessage(error);
    } else { 
        oldPassword.style.border = '1px solid red'; 
        newPassword.style.border = '1px solid red'; 
        errorBlock.textContent = 'Введені дані некоректні! Пароль повинен містити не менше '+ MIN_PASSWORD_LENGTH +' символів';
    }
})

function printErrorMessage(error) { 
    if (error.toString().includes('Status: 400')) {
        errorBlock.style.color = 'red';
        errorBlock.textContent = 'Введений старий пароль некоректний!';
    } else { 
        oldPassword.style.border = '1px solid green'; 
        newPassword.style.border = '1px solid green'; 
        errorBlock.textContent = 'Пароль змінено';
        errorBlock.style.color = 'green';
    }
}


setErrorStyles();

function setErrorStyles() {
    oldPassword.addEventListener('input', () => {
        
       if (!checkPasswordLength(oldPassword))
            oldPassword.style.border = '1px solid red'; 
        else 
           oldPassword.style.border = '1px solid green';
    });

    newPassword.addEventListener('input', () => {
       if (!checkPasswordLength(newPassword))
            newPassword.style.border = '1px solid red'; 
        else 
           newPassword.style.border = '1px solid green';
        
    });
}


function checkPasswordLength(element) { 
    const password = element.value;
    var length = password.length;

    return length >= MIN_PASSWORD_LENGTH;
}
