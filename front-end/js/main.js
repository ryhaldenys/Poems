window.isMobile=!1;if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)){window.isMobile=!0}
function t_throttle(fn, threshhold, scope)
{
    var last;
    var deferTimer;
    threshhold || (threshhold = 250);
    return function () {
        var context = scope || this;
        var now = +new Date(); var args = arguments;
        if (last && now < last + threshhold) {
            clearTimeout(deferTimer); deferTimer = setTimeout(function () {
                last = now;
                fn.apply(context, args)
            }, threshhold)
        } else { last = now; fn.apply(context, args) }
    }
}
function t822_init(recid)
{
    t822_setHeight(recid);
    window.onload = function () { t822_setHeight(recid) };
    window.addEventListener('resize', function () {
        t_throttle(function () {
            if (window.noAdaptive && window.$isMobile) { return }
t822_setHeight(recid)},200)});var currentBlock=document.querySelector('.t822');currentBlock.addEventListener('displayChanged',function(){t822_setHeight(recid)})}
function t822_setHeight(recid){var allContainer=document.querySelectorAll('#rec'+recid+' .t822 .t-container');if(!allContainer)return;Array.prototype.forEach.call(allContainer,function(container){var highestColumn=0;Array.prototype.forEach.call(container.querySelectorAll('.t822__col'),function(column){var columnChild=column.querySelector('.t822__col-wrapper');var childDimension=t822_getElementDimension(columnChild);var columnDimension=t822_getElementDimension(column);if(columnDimension.height<childDimension.height){column.style.height=childDimension.height+'px'}
columnDimension=t822_getElementDimension(column);if(columnDimension.height>highestColumn){highestColumn=columnDimension.height}});Array.prototype.forEach.call(container.querySelectorAll('.t822__col'),function(column){if(window.innerWidth>=960){column.style.height=highestColumn+'px'}else{column.style.height='auto'}})})}
function t822_getElementDimension(element){return{height:parseInt(getComputedStyle(element).height.replace('px',''),10),width:parseInt(getComputedStyle(element).width.replace('px',''),10),}}
function t454_setLogoPadding(recid){var rec=document.getElementById('rec'+recid);if(!rec||window.innerWidth<=980)return;var menu=rec.querySelector('.t454');var logo=menu?menu.querySelector('.t454__logowrapper'):null;var leftWrapper=menu?menu.querySelector('.t454__leftwrapper'):null;var rightWrapper=menu?menu.querySelector('.t454__rightwrapper'):null;var logoWidth=logo?logo.offsetWidth:0;var updateWidth=(logoWidth/2)+50;if(leftWrapper)leftWrapper.style.paddingRight=updateWidth+'px';if(rightWrapper)rightWrapper.style.paddingLeft=updateWidth+'px'}