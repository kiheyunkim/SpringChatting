let sendMessage;
let max;

let wsUri = "ws://127.0.0.1:8080/socket";
Websocket websocket = new WebSocket()
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

    const socket = io.connect('http://127.0.0.1:8080/send');

    max = $(".chat").height();

    sendMessage = (message)=>{
        // -> 전체에 메세지 보냄
        $('.chat #text').append(
            `
                <div class="mychat">
                    <div>${message.replace(/</g,'&lt;').replace(/>/g,'&gt;')}</div>
                </div>
            `
        );
        AdjustCaretPoint();
        socket.emit('Msg', message);
    };
    
    

    socket.on('connect',()=>{
        setToast('connect');
        socket.emit('GetList');
    })
    
    socket.on('disconnect',(reason)=>{  //종료된 그순간 부터 나옴
        console.log('disconnected Reason:',reason);
        setToast('quit');
        // -> 접속 끊어졌다고 알림
    })
    
    socket.on('reconnect_attempt',(attemptCount)=>{
        if(attemptCount > 5){
            socket.disconnect();        //접속 완전종료
            setToast('reconnectFail');
            // -> 접속 완전히 종료됨을 알림 -> 재접속 실패.
            return;
        }
        // -> 접속 재시도를 말함
        setToast('reconnect');
        console.log('Trying to Connecting :'+ attemptCount);
    });
    
    //Normal Message
    socket.on('Msg',(message)=>{
        // -> 일반 메세지 전송
        let isSame=false;
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
    });
    
    socket.on('Join',(nick)=>{
        $('.talkerlist #list').append(
            `
                <li class="talker you">${nick}</li>
            `
        )
    })
    
    socket.on('Exit',(nick)=>{
        let list = $('.talkerlist #list li');
        let length = list.length;
    
        for(let i=0;i<length;++i){
            if($(list[i]).text() === nick){
                $(list[i]).remove();
                break;
            }
        };
    })
    
    socket.on('List',(clientList)=>{
        let list = Array.from(clientList);
        for(let i=0;i<list.length;++i){
            $('.talkerlist #list').append(
                `
                    <li class="talker you">${list[i]}</li>
                `
            )
        }
    })

    $(document).keyup((event)=>{
        if(event.keyCode === 13){
            $('#sendBttn').click();
            $('#sendTextBox').val('');
            $('#sendTextBox').focus();
        }
    });
})