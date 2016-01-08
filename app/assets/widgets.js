/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

/**
 *  Profile widget with completion information
 */
(function ($, App) {
    'use strict';

    function CompletionWidget(selector){
        var self = this;

        self.selector = selector;
        self.$el = $(selector);
        self.currentUserId = $("#activeUserId").val();

        self.assignEvents();
        self.reload();
    }

    CompletionWidget.prototype.assignEvents = function(){
        var self = this;

        App.events
            .sub('hmtReloadCompletionWidget', function () {
                self.reload();
            });

        self.$el
            // show tab in profile page and dialog
            .on('click', '.js-completion-tab', function (e) {
                var $this = $(this),
                    menuTab = $this.data('tab'),
                    modalDialog = $this.data('popup');

                if (!self.isProfilePage()) {
                    App.history.add('hmtShowTabAndDialog', [menuTab, modalDialog]);

                    window.location = jsRoutes.controllers.People.details(self.currentUserId).url;
                    return false;
                }

                App.events.pub('hmtShowTabAndDialog', [menuTab, modalDialog]);
                e.preventDefault();
            })
            // show profile page and photo upload dialog
            .on('click', '.js-completion-photo', function (e) {
                var $this = $(this),
                    modalDialog = $this.data('popup');

                if (!self.isProfilePage()) {
                    App.history.add('hmtShowSelectPhotoForm', [modalDialog]);

                    window.location = jsRoutes.controllers.People.details(self.currentUserId).url;
                    return false;
                }

                App.events.pub('hmtShowSelectPhotoForm', [modalDialog]);
                e.preventDefault();
            });
    };

    CompletionWidget.prototype.reload = function(){
        var self = this,
            url = jsRoutes.controllers.ProfileStrengths.personWidget(self.currentUserId, true).url;

        $.get(url, function(data){
            self.$el.html(data);
        });
    };

    CompletionWidget.prototype.isProfilePage = function(){
        var personId = $('#personId').val();

        return (this.currentUserId == personId);
    };

    App.widgets.CompletionWidget = CompletionWidget;

})(jQuery, App);


/**
 *  Upload photo widget
 */
(function ($, App) {
    'use strict';

    function UploadPhotoWidget(options){
        var self = this;

        self.$el = $(options.selector);

        self.defaults = {
            urlUpdate: '',
            urlContent: '',
            uploadedPhoto: null
        }

        self.locals = {
            selector: options.selector,
            $el: $(options.selector),
            $modalContent: self.$el.find('#uploadPhotoContent'),
            $modal: self.$el.find('#uploadPhotoDialog'),
            $uploadSaveBtn: self.$el.find('#uploadBtnSave'),
            $uploadCustomPhoto: self.$el.find('#uploadCustomPhoto')
        };

        $.extend(this, self.defaults, options);

        self.assignEvents();
        self.setupCustomPhotoActions();
    }

    UploadPhotoWidget.prototype.assignEvents = function(){
        var self = this;

        self.locals.$el
            .on('click', '.js-choose-link', function(e){
                self.showSelectPhotoForm();
                self.locals.$modal.modal('show');

                e.preventDefault();
            })
            .on('click', '.b-photoupload__item', function (e) {
                var $this = $(this);

                self.switchActivePhoto($this);
                e.preventDefault();
            })
            .on('click', '#uploadBtnSave', function (e) {
                self.updatePhoto();
                e.preventDefault();
            })
            .on('click', '#uploadBtnDelete', function (e) {
                self.deletePhoto();
                e.preventDefault();
            })
            .on('fileuploadadd', '#photoUpload', function (e, data) {
                self.uploadedPhoto = data;
                self.locals.$uploadCustomPhoto
                    .attr('src', URL.createObjectURL(data.files[0]))
                    .addClass('photo');
            })


        App.events.sub('hmtShowSelectPhotoForm', function(arr){
            self.showSelectPhotoForm();

            setTimeout(function(){
                $(arr[0]).modal('show');
            }, 200);
            e.preventDefault();
        });
    };

    UploadPhotoWidget.prototype.showSelectPhotoForm = function(){
        var self = this;

        $.get( self.urlContent, function(data) {
            self.locals.$modalContent.html(data);

            self.setupCustomPhotoActions();
        });
    };

    UploadPhotoWidget.prototype.switchActivePhoto = function($object){
        var self = this;

        if (!$object.length) return;

        self.locals.$el
            .find('.b-photoupload__item').removeClass('type_active');
        $object.addClass('type_active');
    }


    UploadPhotoWidget.prototype.setupCustomPhotoActions = function(){
       var self = this;


        $('#photoUpload').fileupload({
            dataType: 'json',
            disableImageResize: false,
            imageMaxWidth: 300,
            imageMaxHeight: 300,
            imageCrop: false,
            autoUpload: false,
            replaceFileInput: false,
            done: function (e, data) {
                self.locals.$uploadCustomPhoto
                    .attr('src', data.result.link);

                self.switchActivePhoto(
                    self.locals.$uploadCustomPhoto.closest('.b-photoupload__item')
                );
            }
        })

        initializeFileUploadField();

    }

    UploadPhotoWidget.prototype.updatePhoto = function(){
        var self = this,
            $currentItem = self.locals.$modalContent.find('.type-active'),
            type = $currentItem.attr('id'),
            src = $currentItem.find('b-photoupload__img').attr('src');

        if (type == "custom" && self.uploadedPhoto != null) {
            self.locals.$uploadSaveBtn.text('Uploading...');
            self.uploadedPhoto.submit();
        }

        $.post(
            self.urlUpdate,
            { type: type },
            null,
            "json"
        ).done( self.updateSuccess)
    };

    UploadPhotoWidget.prototype.updateSuccess = function(data){
        self.local.$modal.modal('hide');

        $('.b-avatar__real .b-photoupload__img')
            .first()
            .attr('src', src);

        $('.b-avatar')
            .addClass('b-avatar_stat_real');

        App.events.pub('hmtReloadCompletionWidgethmt');
    }

    UploadPhotoWidget.prototype.deletePhoto = function(){
        var self = this;

        $.ajax({
            type: "DELETE",
            url: $(this).data('href'),
            dataType: "json"
        }).done(function() {
            $('.b-avatar')
                .removeClass('b-avatar_stat_real');

            App.events.pub('hmtReloadCompletionWidget');
        });

        return false;
    };

    App.widgets.UploadPhotoWidget = UploadPhotoWidget;

})(jQuery, App);
