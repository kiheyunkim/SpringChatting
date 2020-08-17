$(document).ready(()=>{
    $('#sendBttn').click(()=>{
        sendMessage($('#sendTextBox').val());
    });

    $('.menu').click(()=>{
        $(".menu").toggleClass("menuopen");
        $(".slide").toggleClass("menuopen");
    });
});