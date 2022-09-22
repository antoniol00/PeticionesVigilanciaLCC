(function($) {
    $(document).ready(function() {
        var $body = $('body');
        var modal = $('#modal-empty-js');
        
        $body.on('click', '.show-modal-on-click-js', function(event) {
            event.preventDefault();
            $($(this).data('target')).modal();
        });
        
        // Cargar contenido en modal ------------------------
        $body.on('click', '.load-on-modal-js', function(event) {
            event.preventDefault();
            var self = $(this);            
            modal.modal('hide');
            GetPage({
                url: self.attr('href'),
                success: function(response) {
                    modal.html(response);
                    modal.modal('show');
                }
            });
        });
        // --------------------------------------------------

        $body.on('click', '.load-tables-on-modal-js', function(event) {
            event.preventDefault();
            var self = $(this);            
            modal.modal('hide');
            GetPage({
                url: self.attr('href'),
                success: function(response) {
                    modal.html(response);                    
                    modal.modal('show');
                    setTimeout(function(){                
                        dataTableInit($);
                    }, 300);
                }
            });
        });
    
    });
})(jQuery);
