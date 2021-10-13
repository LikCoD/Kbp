async function getStatus() {
    let xhr = new XMLHttpRequest();
    xhr.open('POST', '//nehai.by/ej/ajax.php');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(encodeURI('action=login_parent&student_name=Сергеюк&group_id=435&birth_day=24.04.2005&S_Code='));

    xhr.onload = function () {
        bridge.onReceiveStatus(result)
    };
}

getStatus();