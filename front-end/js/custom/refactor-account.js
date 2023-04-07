import * as lib from "./methods.js";
import * as conf from "./confirm.js";


const name = document.getElementById('name');
const email = document.getElementById('email');
const description = document.getElementById('description');
const image = document.getElementById('logo');
const logoutButton = document.querySelector('.logout');
const removeAccountButton = document.querySelector('.remove-account input');

const id = localStorage.getItem('id');


logout();

function logout() { 
    logoutButton.addEventListener('click', () => { 
        localStorage.clear();
        window.location = 'index.html';
    })
}


insertData();

async function insertData() { 
    const data = await loadData();

    name.value = data.fullName;
    email.value = data.email;

    const descrip = data.description;
    description.value = descrip != null ? descrip : '';
    image.setAttribute('src', data.imagePath);
}


async function loadData() { 
    const requestUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${id}`;
    return lib.sendRequest('GET', requestUrl)
        .then(data => data)
        .catch(err => console.log(err));
}



//////////////////////////////////////// delete account/////////////////////////////////

const deleteUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${id}`;

removeAccountButton.addEventListener("click", () => {
    conf.openConfirmForm("Ви дійсно хочете видалити акаунт?").then(response=>{
        if (response == true) {
            lib.sendRequest('DELETE', deleteUrl);
            localStorage.clear();
            window.location = 'index.html';
        }
    })
})
