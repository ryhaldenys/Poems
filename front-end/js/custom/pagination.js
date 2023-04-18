export const SIZE_OF_PAGE = 10;

const urlParams = new URLSearchParams(window.location.search);
let page = urlParams.get('page');


export function getPage(){
    if (page == null)
        return 0;
    else { 
        page = parseInt(page);
        return page;
    }
}

export function setCorrectSettingForPagination(data,page,main_page,dataParam = '',id = null,name = null){ 
    let left_button = document.querySelector('.left');
    let right_button = document.querySelector('.right');
    let counter = document.querySelector('.counter');
    counter.textContent = page + 1;

    let prevPageUrl = `${main_page}?page=${page - 1}&data=${dataParam}`;
    let nextPageUrl = `${main_page}?page=${page + 1}&data=${dataParam}`;

    if (id != null && name!=null) {
        prevPageUrl += `&id=${id}&name=${name}`;
        nextPageUrl += `&id=${id}&name=${name}`;
    }

    left_button.setAttribute('href', prevPageUrl);
    right_button.setAttribute('href', nextPageUrl);

    if (isUndefind(data.first, data.last)) {
        left_button.removeAttribute('href');
        right_button.removeAttribute('href');
    }   
    if (data.last && data.first) {
        left_button.removeAttribute('href');
        right_button.removeAttribute('href');
    }else if (data.last)
        right_button.removeAttribute('href');
    else if (data.first) 
        left_button.removeAttribute('href');
}

function isUndefind(first,last) { 
    return first == undefined && last == undefined;
}