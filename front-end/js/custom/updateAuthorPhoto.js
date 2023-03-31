import * as lib from "./methods.js";

const addButton = document.querySelector('.add input');
const deleteImageButton = document.querySelector('.remove-image');
const image = document.getElementById('logo');

const id = localStorage.getItem('id');

updateImage();

function updateImage() {
    addButton.addEventListener('change', async (event) => {
        const requestData = createForm();
        await sendRequest(requestData);
        await insertImage();
    });
}

function createForm() { 
    const requestData = new FormData();
    let file = addButton.files[0];
    requestData.append('file', file);
    return requestData;
}

async function sendRequest(requestData) {
    await fetch(`http://localhost:8080/api/authors/${id}/image`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Authorization': localStorage.getItem('token')
        },
        body: requestData
    });
    return true;
}


deleteImage();

function deleteImage() {
    const requestUrl = `http://localhost:8080/api/authors/${id}/image`;
    deleteImageButton.addEventListener('click', async () => {
        await lib.sendRequest('DELETE', requestUrl)
            .catch(err => console.log(err));
        await insertImage();
    })
}


async function insertImage() { 
    const data = await loadData();
    image.setAttribute('src', data.imagePath);    
}


async function loadData() { 
    const requestUrl = `http://localhost:8080/api/authors/${id}`;
    return lib.sendRequest('GET', requestUrl)
        .then(data => data)
        .catch(err => console.log(err));
}
