const submitButton = document.forms['form544922433'];
const inputs = document.getElementsByClassName('t-input');

const fields = document.querySelectorAll('input');

const nameFiled = fields[0];
const emailFiled = fields[1];
const passwordFiled = fields[2];
const confirmPasswordField = fields[3];

const errorFileds = document.querySelectorAll('.t-input-error');

const errorWrapper = document.querySelector('.error-wrapper');
const errorMessage = document.querySelector('.error-message');

const MIN_PASSWORD_LENGTH = 5;
const MIN_USERNAME_LENGTH = 3;


nameFiled.addEventListener('input', () => { 
    checkIsValidName();
});

function checkIsValidName() {
    var result;
    const email = inputs[0].value;

    if (email.length >= MIN_USERNAME_LENGTH) {
        errorFileds[0].style.display = 'none';
        nameFiled.style.border = '1px solid green';
        result = true;
    } else { nameFiled.style.border = '1px solid red';
        result = false;
    }
    return result;

}



emailFiled.addEventListener('input', () => { 
    checkIsValidEmail();
});


function checkIsValidEmail(){ 
    const email = inputs[1].value;
    var result;
    if (validateEmail(email)) {
        errorFileds[1].style.display = 'none';
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
        errorFileds[2].style.display = 'none';
        passwordFiled.style.border = '1px solid green';
        result = true;
    } else {
        passwordFiled.style.border = '1px solid red';
        result = false;
    }

    return result;
}

function checkPasswordLength() { 
    const password = inputs[2].value;
    var length = password.length;

    return length >= MIN_PASSWORD_LENGTH;
}


confirmPasswordField.addEventListener('input', () => {
    checkIsPasswordConfirm();
});

function checkIsPasswordConfirm() { 
    var result;
    password = inputs[2].value;
    confirmPassword = inputs[3].value;

    if (password == confirmPassword && confirmPassword != '') {
        errorFileds[3].style.display = 'none';
        passwordFiled.style.border = '1px solid green';
        confirmPasswordField.style.border = '1px solid green';
        result = true;
    } else { 
        passwordFiled.style.border = '1px solid red';
        confirmPasswordField.style.border = '1px solid red';
        result = false;
    }
    return result;
}


submitButton.addEventListener('submit', (event) => {
    if (!printErrorIfAreIncorrectData()) {
        sendForm(event);
    }
});


function printErrorIfAreIncorrectData() {
    var result = false;

    if (!checkIsValidName()) { 
        result = true;
        errorFileds[0].textContent = 'Ім\'я користувача повинно містити не менше ' + MIN_USERNAME_LENGTH+' символів';
        errorFileds[0].style.display = 'contents';
    }


    if (!checkIsValidEmail()) {
        result = true;
        errorFileds[1].textContent = 'Некоректна електронна адреса';
        errorFileds[1].style.display = 'contents';
    }

    if (!checkIsCorrectPassword()) {
        result = true;
        errorFileds[2].textContent = 'Пароль повинен містити більше '+MIN_PASSWORD_LENGTH+' символів';
        errorFileds[2].style.display = 'contents';
    }

    if (!checkIsPasswordConfirm()) { 
        result = true;
        errorFileds[3].textContent = 'Паролі не співпадають';
        errorFileds[3].style.display = 'contents';
    }
    return result;
}



function sendForm(event) { 
    event.preventDefault();
        
    fetch(event.target.action, {
        method: 'POST',
        body: new URLSearchParams(new FormData(event.target)) // event.target is the form
    }).then((response) => response.json()
    ).then((body) => {
        if (!printErrorMessageIfUserDataAlredyExist(body)) { 
            saveUserData(body);
            window.location.replace("main.html");
        }     
    });
}

function printErrorMessageIfUserDataAlredyExist(body) { 
    var message = body.message;
    if (message == undefined) {
        message = ' ';
    }
    
    var name = inputs[0].value;
    var email = inputs[1].value;
    
    var result = false;

    if (checkIsUserNameAlreadyExist(message, name)) {
        printErrorMessage('Автор з iм\'ям: \"' + name + '\" вже існує');
        result = true;
    }
    else if (checkIsUserEmailAlreadyExist(message, email)) {
        printErrorMessage('Автор з електронною адресою: \"' + email + '\" вже існує');
        result = true;
    }

    return result;
}

function checkIsUserNameAlreadyExist(message, name) { 
    return message.includes(name);
}

function checkIsUserEmailAlreadyExist(message,email) { 
    return message.includes(email);
}


function saveUserData(body) { 
    localStorage.setItem('id', body.id);
    localStorage.setItem('token', body.token);
}

function printErrorMessage(message) { 
    errorWrapper.style.display = 'contents';
    errorMessage.textContent = message;
}