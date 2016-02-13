$(function(){
    var $socialConnect = $('.js-social-connect');
    if ($socialConnect.length){
        new App.widgets.PersonSocialConnect('.js-social-connect');
    }
});