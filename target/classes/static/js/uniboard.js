// JavaScript Document
var GetPage = null;

var reloadAfterModalFormSubmitSuccess = false;

;
(function ($) {

    /**
     * In genereal you should avoid to use jQuery code in AngularJS
     * apps, if you need any jQuery functionality create a directive
     *
     */
    $(document).ready(function () {
        var body = $('body');
        
        body.on('click', '.nav-item.nav-link.disable', function(e){
           e.preventDefault(); 
           e.stopPropagation();
        });

        // Enable/Disable search input with checkbox
        /// <input type="checkbox" class="checkbox-switch" 
        //                         data-target="#select-a" 
        //                         data-input-tag="select"
        function UpdateCheckboxSwitch(checkbox){
            var self = checkbox;
            var target = self.data('target');
            var inputTag = self.data('input-tag');
            var form = self.parents('form');
            var container = form.find(target);
            var checked = self.prop("checked");
            container.find(inputTag).each(function(i, e){
                if(checked)
                    $(e).removeAttr('disabled');
                else    
                    $(e).attr('disabled', 'disabled');
            });
        };        
        body.on('click', '.checkbox-switch', function(event){
            var self = $(this);
            UpdateCheckboxSwitch(self);
         });
        body.find('.checkbox-switch').each(function(i, element){
            UpdateCheckboxSwitch($(element));    
        });

        // [Pedir pagina por ajax] -------------
        // opt
        /// url
        /// data
        /// success (callback(response))
        GetPage = function (opt) {
            var url = opt.url;
            var data = opt.data;
            var success = opt.success;
            $.ajax({
                url: url,
                dataType: "html",
                type: 'get',
                data: data,
                async: true,
                success: function(response) {
                    if(success){
                        success(response);                        
                    }
                },
                error: function(response) {
                    console.log(response);
                },
            });
        };
        // -------------------------------------------

        // Accion y Recarga--------------------------------------------------
        body.on('click', '.profesor-row.ajax-on-click-js', function (event) {
            event.preventDefault();            
            var self = $(this);
            var data = {id: self.data('id')};
            var container = $(self.data('target'));
            var table = self.parents('table');
            table.find('.profesor-row.ajax-on-click-js').removeClass('table-success');
            self.addClass('table-success');
            GetPage({
                url: self.attr('href'),
                data: data,
                success: function(response){
                    container.html(response);
                }});
        });
        // --------------------------------------------------

        // Boton Confirmar Borrar ---------------------------
        body.on('click', '.delete-on-click-js.submit-on-success', function (event) {
            event.preventDefault();
            var self = $(this);
            var data = {id: self.data('id')};
            var form = $(self.data('target'));            
            GetPage({
                url: self.attr('href'),
                data: data,
                success: function(response){
                    form.submit();
                }});
        });
        // --------------------------------------------------

        body.on('click', 'button[type=reset]', function(event){
            event.preventDefault();
            var self = $(this);
            var form = self.parents('form');            
            var action = form.attr('action');
            location.replace(action);
        });
        
        body.on('click', '.change-links', function (event) {
            event.preventDefault();
            var self = $(this);
            var id = self.data('id');            
            var targetLinks = self.data('target-link');
            var links = $(targetLinks);
            var table = self.parents('table');
            table.find('.profesor-row').removeClass('table-success');
            self.addClass('table-success');
            var fromPage = self.data('from-page');
            links.each(function(index, element){
                var link = $(element);
                var param = link.data('param');
                if(link.hasClass('disabled')){
                    link.removeClass('disabled');
                }
                if(!param)
                    param = 'teaching';
                var separator = link.data('separator');
                if(!separator)
                    separator = "";
                var successAction = self.data('success-action');
                if(!successAction)
                    successAction = "";
                if(!fromPage)
                    fromPage = "";
                link.attr('href', link.data('href') + '?'+param+'=' +id + separator + '&success_action='+successAction+'&page-from='+fromPage);            
            });            
        });        

        body.on('click', '.anadir-asignaturas-btn', function (event) {
            event.preventDefault();
            var self = $(this);            
            var tempHtml = $('#temp-html');
            var sectionPrefix = self.data('section-prefix');
            var container1 = $(self.data('response-container-1'));
            var container2 = $(self.data('response-container-2'));
            var tableContainer = self.parents('.list-group-item');

            var successClass = self.data('success-class');

            tableContainer.find('.profesor-row.table-success').removeClass('table-success');
            GetPage({
                url: self.attr('href'),            
                success: function(response){
                    tempHtml.html(response);
                    var section1 = $('#'+sectionPrefix+'section-1');
                    var section2 = $('#'+sectionPrefix+'section-2');
                    container1.empty();
                    container2.empty();
                    section1.appendTo(container1);
                    section2.appendTo(container2);
                    self.addClass(successClass);

                }});
        });

        body.on('click', '.change-text', function (event) {
            event.preventDefault();
            var self = $(this);
            var release = self.data('release');            

            var targetLinks = self.data('target-link');
            var links = $(targetLinks);

            links.each(function(index, element){
                var link = $(element);
                link.text(release == 1 ? "Rescatar" : "Liberar");
            });            
        });

        body.on('click', '.change-user-status', function (event) {
            event.preventDefault();
            var self = $(this);
            var href = self.data('href');
            var check = event.target.checked;
            if(check) check = 1;
            else check = 0;
            GetPage({
                url: href,
                data:{check:check}, 
                success: function(response){
                    location.reload();
                }});

        });


        $('.bootstrap-data-table-checkbox').each(function(index, element){
            var self = $(element);
            var paging = self.hasClass('paging');
            self.DataTable({
                lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "Todo"]],
                        searching: true,
                        paging: paging,
                        "scrollY": "350px",
                        "scrollCollapse": true,
                        "info": false,
                        "dom": '<f<t>>'
            });
        });
        body.on('click', '.checkbox-btn', function(event){
            event.preventDefault();
            event.stopPropagation();
            var self = $(this);
            clickCheckBtn(self);
        });
        function clickCheckBtn(self){
            var tr = self.parents('tr');
            var target = $(tr.data('target'));
            tr.removeClass('active');
            target.addClass('active');
            var inputIdSelector = tr.data('input');
            refreshCheckboxInput(inputIdSelector, ".checkbox-row.in");
        };
        body.on('click', '.checkbox-btn-all', function(event){
            event.preventDefault();
            event.stopPropagation();
            var self = $(this);
            var container = self.parents('.checkbox-table-container');
            var target = $(self.data('target'));
            var table = container.find(target);
            table.find('.checkbox-btn').each(function(index, element){
                clickCheckBtn($(element));
            });            
        });
        function refreshCheckboxInput(inputIdSelector, inSelector){
            var input = $(inputIdSelector);
            input.val("");
            var inputVal = "";
            $(inSelector+".active").each(function(index, e){
                var tr = $(e);
                inputVal += tr.data('id')+",";
            });
            input.val(inputVal);
        }
        function refreshCheckboxRows(inputIdSelector){
            var input = $(inputIdSelector);            
            var inputVal = input.val();
            var selection = inputVal.split(',');
            for(var i = 0; i < selection.length - 1; i++){
                var id = selection[i];
                var outTR = $('#out-'+id);
                var inTR = $('#in-'+id);
                outTR.removeClass('active');
                inTR.addClass('active');
            }            
        }
        if($('#accessGroups').length > 0){
            refreshCheckboxRows('#accessGroups');
        }
        body.on('click', '.access-groups-switch', function(event){
            var self = $(this);
            self.toggleClass('checked');
            refreshAccessGroupSwitch(self);
        });
        
        function refreshAccessGroupSwitch(agSwitch){
            var target = $('#access-groups-section');
            if($('#'+agSwitch.attr('id')+":checked").length > 0)
                target.fadeIn();                
            else    
                target.fadeOut();
        }
        if($('.access-groups-switch').length > 0){
            var agSwitch = $('.access-groups-switch');
            refreshAccessGroupSwitch(agSwitch);
        }
        
        body.on('click', '.get-page-js', function (event) {
            event.preventDefault();                        
            var self = $(this);
            var href = self.attr('href');
            var target = $(self.data('target'));
            GetPage({
                url: href,
                success: function(response){
                    target.html(response);
                }});
        });
        
        body.on('hidden.bs.modal', '#modal-empty-js', function (e) {
            var self = $(this);
            var target = self.find(".modal-reloader");
            if (target.length > 0) {
                location.reload();
            }
        });
        
        var messageModal = $("#message-modal");
        if (messageModal.length > 0) {
            messageModal.modal('show');
        }

    });
})(jQuery);