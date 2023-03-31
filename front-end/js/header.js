function t_throttle(fn, threshhold, scope) {
    var last; var deferTimer;
    threshhold || (threshhold = 250); return function ()
    {
        var context = scope || this; var now = +new Date(); var args = arguments;
        if (last && now < last + threshhold) {
            clearTimeout(deferTimer);
            deferTimer = setTimeout(function () { last = now; fn.apply(context, args) }, threshhold)
        } else { last = now; fn.apply(context, args) }
    }
}
function t454_setLogoPadding(recid) {
    var rec = document.getElementById('rec' + recid);
    if (!rec || window.innerWidth <= 980) return;
    var menu = rec.querySelector('.t454');
    var logo = menu ? menu.querySelector('.t454__logowrapper') : null;
    var leftWrapper = menu ? menu.querySelector('.t454__leftwrapper') : null;
    var rightWrapper = menu ? menu.querySelector('.t454__rightwrapper') : null;
    var logoWidth = logo ? logo.offsetWidth : 0;
    var updateWidth = (logoWidth / 2) + 50;
    if (leftWrapper) leftWrapper.style.paddingRight = updateWidth + 'px';
    if (rightWrapper) rightWrapper.style.paddingLeft = updateWidth + 'px'
}