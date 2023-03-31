export function openConfirmForm(message) {
    let element = document.createElement("div");
    element.classList.add("box-background");
    element.innerHTML = `<div class="box">  
            ${message}
            <div>
              <button id="trueButton" class="btn button black">Так</button>
              <button id="falseButton" class="btn button white">Ні</button>
            </div>
        `;
        document.body.appendChild(element);
        return new Promise(function (resolve, reject) {
            document.getElementById("trueButton").addEventListener("click", function () {
                resolve(true);
                document.body.removeChild(element);
            });
                document.getElementById("falseButton").addEventListener("click", function () {
                    resolve(false);
                    document.body.removeChild(element);
                });
            })
        }

