import { sendRequest } from "./methods.js";


const requestUrl = `https://poems-back-end-app.herokuapp.com/api/authors/${localStorage.getItem('id')}`;
const img = document.querySelector(".img-block");
const sub_block = document.querySelector(".sub-block");
const poems = document.querySelector(".poems");
const likes = document.querySelector(".likes");
const own_data_block = document.querySelector(".own-data-block");


const loadingBlock = document.querySelector('.data-loading');
const contentBlock = document.querySelector('.all-content-block');
const refactorBlock = document.querySelector('.refactor-block');



checkIsAuthorithation();

function checkIsAuthorithation() { 
  if (localStorage.getItem('token') == null)
    window.location = 'index.html';
}


const responce = await sendRequest("GET", requestUrl)
    .then(data => data)
    .catch(err => console.log(err));

console.log(responce);

addData(responce);

function addData(data) { 
    loadingBlock.style.display = 'none';
    contentBlock.style.display = 'block';
    refactorBlock.style.display = 'block';
   
    img.innerHTML = 
    `<img src="${data.imagePath}" alt="avatar" />
    `

    sub_block.innerHTML =
        `<div class="subscriptions">
                  <a href="users.html?data=subscriptions">Підписки:<span>${data.amountSubscriptions}</span></a>
                </div>
                <div class="subscribers">
                  <a href="users.html?data=subscribers">Підписники:<span>${data.amountSubscribers}</span></a>
                </div>`;
    
    poems.innerHTML =
        `  <a href="my-poems.html?id">Вірші:<span>${data.amountPoems}</span></a>
        `;
    
    likes.innerHTML =
        `<a href="poems.html?data=likes">
            <img src="images/icons/red-heart.png" alt=""><span>${data.amountLikes}</span>
         </a>`;
    
    let description;
    if (data.description == null)
        description = 'Опис відсутній';
    else
        description = data.description;

    
    own_data_block.innerHTML =
        `<div class="name">${data.fullName}</div>
            <div class="email">${data.email}</div>
            <div class="description">
              ${description}
            </div>`
}
