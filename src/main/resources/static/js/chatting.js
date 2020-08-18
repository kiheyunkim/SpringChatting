let sendMessage;
let max;

let wsUri = "ws://127.0.0.1:8080/socket";
let webSocket = null;
let AdjustCaretPoint =()=>{
    let height = $("#text").height();
    if(height > max) {
        $(".chat").scrollTop(height);
    }
}

$(document).ready(()=>{
    let setToast=(type)=>{
        let target = $(`.snack .${type}`);
        target.attr('class','snackshow');
    
        window.setTimeout(() => {
            target.attr('class',type);//되돌림
        },2000);
    }

    max = $(".chat").height();

    sendMessage = (message) => {
        $('.chat #text').append(
            `
                <div class="mychat">
                    <div>${message.replace(/</g,'&lt;').replace(/>/g,'&gt;')}</div>
                </div>
            `
        );
        AdjustCaretPoint();
        let request = "message";
        websocket.send(JSON.stringify({request, message}));
    }

    let setWebSocketReady = () => {
        websocket = new WebSocket(wsUri);
        websocket.onopen = (e) => {
            setToast('connect');
            websocket.send(JSON.stringify({request:"connect"}));
        }
        websocket.onmessage = (e) => {
            let data = JSON.parse(e.data);
            if(data.type === 'message'){
                // -> 일반 메세지 전송
                let isSame=false;
                let message = JSON.parse(data.message);
                let lastChatting = $('.chat #text > div').last();
                if(lastChatting.attr('class') === 'youchat'){//연속해서 상대가 보냈나?
                    if(lastChatting.attr('id') === message.nick){//이전과 같음 판단
                        isSame=true;    //###########ID가 한글이 되지 않도록 처리해야함
                    }
                }

                $('.chat #text').append(
                    `
                        <div class="youchat" id=${message.nick}>
                        ${!isSame ? `<label>${message.nick}</label>` : ''}
                        <div>${message.msg}</div>
                        </div>
                    `
                );
                AdjustCaretPoint();
            }
            else if(data.type === 'join'){
                console.log(JSON.parse(data.message));
                let nick = JSON.parse(data.message).nick;
                $('.talkerlist #list').append(
                    `
                        <li class="talker you">${nick}</li>
                    `
                );
            }
            else if(data.type === 'exit'){
                let nick = data.nick;
                let list = $('.talkerlist #list li');
                let length = list.length;

                for(let i=0;i<length;++i){
                    if($(list[i]).text() === nick){
                        $(list[i]).remove();
                        break;
                    }
                };
            }
            else if(data.type === 'joinedList'){

                let list=[];
                if(data.joinedList !== "[]"){
                    list = data.joinedList.replace("[","").replace("]","").split();
                }


                for(let i=0;i<list.length;++i){
                    $('.talkerlist #list').append(
                        `
                        <li class="talker you">${list[i]}</li>
                    `
                    )
                }

            }
        }
        websocket.onerror = (e) => {
            /*socket.on('reconnect_attempt',(attemptCount)=>{
                if(attemptCount > 5){
                    socket.disconnect();        //접속 완전종료
                    setToast('reconnectFail');
                    // -> 접속 완전히 종료됨을 알림 -> 재접속 실패.
                    return;
                }
                // -> 접속 재시도를 말함
                setToast('reconnect');
                console.log('Trying to Connecting :'+ attemptCount);
            });*/
            //onError(e);
        }
        websocket.onclose = (e) => {
            console.log('disconnected Reason:',e);
            setToast('quit');
        }
    }

    setWebSocketReady();

    $(document).keyup((event)=>{
        if(event.keyCode === 13){
            $('#sendBttn').click();
            $('#sendTextBox').val('');
            $('#sendTextBox').focus();
        }
    });
})