import * as lib from "./methods.js";
import * as pag  from "./pagination.js";


const page = pag.getPage();
const SIZE_OF_PAGE = pag.SIZE_OF_PAGE;

const poems = document.querySelector(".poems");
const find_field = document.querySelector('.type');
const find_button = document.querySelector('.find');


checkIsAuthorithation();

function checkIsAuthorithation() { 
  if (localStorage.getItem('token') == null)
    window.location = 'index.html';
}


loadData();

find_button.addEventListener('click', async () => {
    let value = find_field.value;
    poems.innerHTML = getLoadingBlock();
    loadData(value);     
});

async function loadData(value ='') {
    const loadingDataForInnering = getLoadingBlock();
    poems.innerHTML = loadingDataForInnering;
    const requestUrl = makeRequestForSubscriptions(value);
    const allData = await getData(requestUrl);
    pag.setCorrectSettingForPagination(allData,page,'poems.html',getDataParam());
    addData(allData);    
    updateLikes(allData.content);  
}


function getLoadingBlock() { 
     return `<div class='data-loading'>
        <img src="images/icons/Spinner-1s-200px.gif"/>
    </div>`;

}


function makeRequestForSubscriptions(value) { 
  let data = getDataParam();
  let authorId = getIdParam();
  let id = localStorage.getItem('id');

  if (data == 'likes')
    return `http://localhost:8080/api/authors/${id}/likes?page=${page}&size=${SIZE_OF_PAGE}&poemName=${value}`;
  else if(authorId != null)
    return `http://localhost:8080/api/authors/${authorId}/poems?page=${page}&size=${SIZE_OF_PAGE}&poemName=${value}`;

  return `http://localhost:8080/api/poems?page=${page}&size=${SIZE_OF_PAGE}&name=${value}`;
}


async function getData(requestUrl) { 
    return lib.sendRequest('GET', requestUrl)
        .then(data => data)
        .catch(err => err);
}


function getDataParam() {  
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('data');
} 

function getIdParam() { 
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}


function getNameParam() { 
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('name');
}


function addData(data) {  
    innerTitle();
    if (data.message == 'Cannot find any poems') {
        poems.innerHTML = '<div class="not-found"> <p>Віршів немає</div><p>';
    } else { 
        poems.innerHTML = '';
        innerPoems(data.content);
    }   
}


function innerPoems(data) { 
  for (let i = 0; i < data.length; i++) {
  var div = document.createElement("div");

  var text = lib.addTegBrToEachLine(data[i].text);
  
  div.innerHTML =
    `<div
          id="rec545657708"
          class="r t-rec t-rec_pt_45 t-rec_pb_45"
          style="padding-top: 45px; padding-bottom: 45px"
          data-record-type="47"
        >
          <!-- T033 -->
          <div class="t033">
            <div class="t-container">
              <div class="t-col t-col_4">
                <div class="t033__lineTop" style=""></div>
                <div class="t033__title t-title" style="" field="title">
                  ${data[i].name}
                </div>
              </div>
              <div class="t-col t-col_8">
                <div
                  class="t033__descr t-descr t-descr_xl"
                  style=""
                  field="text"
                >
                  ${text}
                </div>
                <div class="read-more">
                  <a href="poem.html?id=${data[i].id}">Читати більше</a>
                </div>
                <div class="likes">
                  <img src="${lib.isLike(data[i].like,"black")}" alt="" />
                  <p>${data[i].amountLikes}</p>
                </div>
              </div>
            </div>
          </div>
        </div>`;

  poems.prepend(div);
  }
}


const title = document.querySelector('.t-title');

function innerTitle() {
  let dataParam = getDataParam();
  let authorId = getIdParam();
  let name = getNameParam();
  if (dataParam == 'likes')
    title.textContent = 'Вподобані вірші';
  else if (authorId != null)
    title.textContent = name;
  else
    title.textContent = 'Всі вірші';
}




/*------------------------- update likes------------------- */

function updateLikes(data) { 
  var likeButtons = document.querySelectorAll('.poems img');
  var countLikesFields = document.querySelectorAll('.poems p');

  lib.updateLike(likeButtons, countLikesFields, data,"black");
}


/*----------------------------------------------------------- */

