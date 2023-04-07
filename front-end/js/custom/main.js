const requestURL = 'https://poems-back-end-app.herokuapp.com/api/poems?page=0&size=6&sort=createdAt,desc'

import * as lib  from "./methods.js";


let content = await lib.sendRequest('GET', requestURL)
  .then(data => data.content)
  .catch(err => console.log(err));


const poems = document.querySelector(".poems-container");
const loadingBlock = document.querySelector('.data-loading');
const contentBlock = document.querySelector('.content-block');


checkIsAuthorithation();

function checkIsAuthorithation() { 
  if (localStorage.getItem('token') == null)
    window.location = 'index.html';
}



loadData();

function loadData() {
  loadingBlock.style.display = 'none';
  contentBlock.style.display = 'contents';

  for (let i = 0; i < content.length; i++) {
    var div = document.createElement("div");

    var text = lib.addTegBrToEachLine(content[i].text);
  
    div.innerHTML =
      `<li class="t822__col t-col t-col_4 t-align_left t822__col_line">
        <div class="t822__col-wrapper t822__col-wrapper">
          <div
            class="t822__title t-name t-name_xl"
            style="color: #ffffff"
            field="li_title__1518437870968"
            >
            ${content[i].name}
            </div>
            <div
            class="t822__text t-descr t-descr_xs"
            style="color: #ffffff"
            field="li_text__1518437870968"
              >
              ${text}
              <div class="read-more">
              <a href="poem.html?id=${content[i].id}">Читати більше...</a>
            </div>
            <div class="details">
              <div class="author">
                <a href="poems.html?id=${content[i].authorId}&name=${content[i].authorName}">${content[i].authorName}</a>
              </div>
              <div class="likes">
                <img src="${lib.isLike(content[i].like, "white")}" alt="" srcset="" />
                <p>${content[i].amountLikes}</p>
              </div>
            </div>
        </div>
      </li>`;

  
    poems.appendChild(div);
  }
}


////////////////// add likes


var likeButtons = document.querySelectorAll('.poems-container li img');
var countLikesFields = document.querySelectorAll('.poems-container li p');


lib.updateLike(likeButtons, countLikesFields, content,"white");









/////////////////////////////////////////////////////AUTHORS


const authors_container = document.querySelector('.authors-container');

const getAuthorsUrl = "https://poems-back-end-app.herokuapp.com/api/authors/most-popular?page=0&size=6";

const response = await lib.sendRequest('GET', getAuthorsUrl)
  .then(data => data.content)
  .catch(err => console.log(err));

for (let i = response.length -1 ; i >= 0 ; i--) {
  var div = document.createElement("div");

  div.innerHTML =
    `<div class="t016__text t-text t-text_md" style="" field="text">
        <div class="author-img">
          <img
            src="${response[i].imagePath}"
            alt=""
          />
          </div>
          <div class="author-content">
            <div class="author-name"><a href="poems.html?id=${response[i].id}&name=${response[i].fullName}">${response[i].fullName}</a></div>
            <div class="author-subscribers">
              <img src="images/icons/contact.png" alt="" />
              <p>${response[i].amountSubscribers}</p>
          </div>
        </div>
      </div>`;
  
  authors_container.prepend(div);
}
