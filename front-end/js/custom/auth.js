
const submitButton = document.forms['form544925042'];
const inputs = document.getElementsByClassName('t-input');

const emailFiled = document.querySelectorAll('input')[0];
const passwordFiled = document.querySelectorAll('input')[1];

const emailError = document.querySelectorAll('.t-input-error')[0];
const passwordError = document.querySelectorAll('.t-input-error')[1];

const errorWrapper = document.querySelector('.error-wrapper');
const errorMessage = document.querySelector('.error-message');


const MIN_PASSWORD_LENGTH = 5;

emailFiled.addEventListener('input', () => { 
    checkIsValidEmail();
});


function checkIsValidEmail(){ 
    const email = inputs[0].value;
    var result;
    if (validateEmail(email)) {
        emailError.style.display = 'none';
        emailFiled.style.border = '1px solid green';
        result = true;
    }
    else {
        emailFiled.style.border = '1px solid red';
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


passwordFiled.addEventListener('input', () => {
    checkIsCorrectPassword();
});


function checkIsCorrectPassword() { 
    var result;

    if (checkPasswordLength()) {
        passwordError.style.display = 'none';
        passwordFiled.style.border = '1px solid green';
        result = true;
    } else {
        passwordFiled.style.border = '1px solid red';
        result = false;
    }

    return result;
}

function checkPasswordLength() { 
    const password = inputs[1].value;
    var length = password.length;

    return length >= MIN_PASSWORD_LENGTH;
}



submitButton.addEventListener('submit', (event) => {
    if (!printErrorIfAreIncorrectData()) {
        event.preventDefault();

        fetch(event.target.action, {
            method: 'POST',
            body: new URLSearchParams(new FormData(event.target)) // event.target is the form
        }).then((response) => {
            if (!response.ok) {
                printErrorMessage('Неправельний пароль або електронна адреса');
                throw new Error(`HTTP error! Status: ${response.status}`);
            } else
                return response.json();
        }).then((body) => {
            saveData(body);
            window.location.replace("main.html");
        });
    }
});

function saveData(body) { 
    localStorage.setItem('id', body.id);
    localStorage.setItem('token', body.token);
}

function printErrorIfAreIncorrectData() {
    var result = false;
    if (!checkIsValidEmail()) {
        result = true;
        emailError.textContent = 'Некоректна електронна адреса';
        emailError.style.display = 'contents';
    }

    if (!checkIsCorrectPassword()) { 
        result = true;
        passwordError.textContent = 'Пароль повинен містити більше '+MIN_PASSWORD_LENGTH+' символів';
        passwordError.style.display = 'contents';
    }
    return result;
}

function printErrorMessage(message) { 
    errorWrapper.style.display = 'contents';
    errorMessage.textContent = message;
}