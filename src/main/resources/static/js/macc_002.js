
var AjaxFormSubmit = null;

var AjaxModalFormSubmit = null;

(function ($) {
    $(document).ready(function() {
        var $body = $('body');
        var formTestArea = $('#form-test-area-id');
        var modal = $('#modal-empty-js');

        if(formTestArea.length == 0)
        {
            formTestArea = $("<div id='form-test-area-id' class='hidden'></div>");
            $body.append(formTestArea);
        }

        AjaxFormSubmit = function (form, context, successCallback, errorCallback) {
            $.ajax({
                url: $(form).attr('action'),
                dataType: "html",
                type: $(form).attr('method'),
                data: $(form).serialize(),
                async: true,
                success: function(response) {
                    formTestArea.html(response);
                    if (formTestArea.find('.has-error').length > 0) {
                        $(context).empty();
                        $(context).append(formTestArea.html());
                        if(errorCallback)
                            errorCallback();
                    }
                    else {
                        if(successCallback)
                            successCallback(response);
                    }
                    formTestArea.html('');
                },
                error: function(response) {
                    console.log(response);
                }
            });
        };
        // --------------------------------------------------

        AjaxModalFormSubmit = function (form, context, successCallback, errorCallback) {            
            $.ajax({
                url: $(form).attr('action'),
                dataType: "html",
                type: $(form).attr('method'),
                data: $(form).serialize(),
                async: true,
                success: function(response) {
                    formTestArea.html(response);
                    if (formTestArea.find('.has-error').length > 0) {
                        modal.html(formTestArea.html());                        
                        if(errorCallback)
                            errorCallback();
                    }
                    else {
                        if(successCallback)
                            successCallback(response);
                    }
                    formTestArea.html('');
                },
                error: function(response) {
                    console.log(response);
                }
            });
        };
        // --------------------------------------------------

        $body.on('submit', '.ajax-form-submit', function(event){
            event.preventDefault();
            var self = $(this);
            var form = self.find('form');
            AjaxFormSubmit(form, self,
                function(response){
                    location.reload();
                },
                function(){
                    console.log('error on form submit');
                });
        });

        $body.on('submit', '.ajax-modal-form-submit', function(event){
            event.preventDefault();
            var self = $(this);
            var form = self.find('form');
            AjaxModalFormSubmit(form, self,
                function(response){
                    if(reloadAfterModalFormSubmitSuccess || form.hasClass("reload-on-success"))
                        location.reload();
                    else
                        modal.modal('hide');
                },
                function(){
                    console.log('error on form submit');
                });
        });
    });
})(jQuery);

