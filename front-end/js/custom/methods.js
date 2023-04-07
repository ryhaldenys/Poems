export async function sendRequest(method, url, body = null) {
  const headers = {
        'Content-Type': 'application/json',
        'Authorization': localStorage.getItem('token'),
  }
  return await fetch(url, {
    method: method,
    headers: headers
  }).then(response => {
    if (response.ok)
      return response.json()
    else if(response.status === 404) {
      return Promise.reject(response.json());
    } else {
      const e = new Error('Something went wrong! Status: '+response.status);
      throw e;
    }
  })
}


export async function sendRequestWithBody(method, url, body = null) {
  const headers = {
        'Content-Type': 'application/json',
        'Authorization': localStorage.getItem('token')
  }
  return await fetch(url, {
    method: method,
    body: JSON.stringify(body),
    headers: headers
  }).then(response => {
    if (response.ok)
      return response.json()
    
    else {
      const e = new Error('Something went wrong! Status: '+response.status);
      throw e;
    }
  })
}



export function updateLike(likeFields,countLikeFields,data, color) { 
  for (let i = 0; i < likeFields.length; i++) {
    let poemContent = data[i];
    likeFields[i].addEventListener("click", async () => { 
    
    let url = `https://poems-back-end-app.herokuapp.com/api/authors/${localStorage.getItem('id')}/poems/${poemContent.id}/likes`;
    
   
    let like = likeFields[i].getAttribute('src'); 
  
    let countLikes = countLikeFields[i].textContent; 
    sendRequest('PATCH', url)
      .catch(err => console.log(err));
    
    if (like == "images/icons/red-heart.png") {
      likeFields[i].setAttribute("src", `images/icons/${color}-heart.png`);
      countLikeFields[i].textContent = parseInt(countLikes) - 1;
    } else {
      likeFields[i].setAttribute("src", "images/icons/red-heart.png");
      countLikeFields[i].textContent = parseInt(countLikes) + 1;
    }
  });
  }
}


export function addTegBrToEachLine(text,all=false) {
  let lines = text.split('\n');
  if (checkIsMoreLines(4, lines)&& !all)
    return addTeg(lines, 4);
  else
    return addTeg(lines, lines.length);
}


function addTeg(lines,length){
  let newArray = new Array(length);
  
  for (let i = 0; i < length; i++) {
    newArray[i] = lines[i] + "<br>";
  }
  return newArray.join("");
}

function checkIsMoreLines(count, array) {
  return array.length >= count;  
}


export function isLike(like,color) { 
  if (like) {
    return 'images/icons/red-heart.png';
  } else
    return `images/icons/${color}-heart.png`;
}



