$(document).ready(()=>{
    $('#sendBttn').click(()=>{
        sendMessage((String)($('#sendTextBox').val()));
    });

    $('.menu').click(()=>{
        $(".menu").toggleClass("menuopen");
        $(".slide").toggleClass("menuopen");
    });
});