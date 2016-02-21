$(function(){
    var $socialConnect = $('.js-social-connect');
    if ($socialConnect.length){
        new App.widgets.PersonSocialConnect('.js-social-connect');
    }

    var $emailConnect = $('.js-email-connect');
    if ($emailConnect.length){
        new App.widgets.PersonEmailConnect('.js-email-connect');
    }
});