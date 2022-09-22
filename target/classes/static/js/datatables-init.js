var dataTableInit = function ($) {
    $('.bootstrap-data-table-basic').DataTable({
        "info": false,
        paging: false,
        searching: false,
    });

    $('#bootstrap-data-table').DataTable({
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "Todo"]],
        searching: false,
    });

    $('#bootstrap-data-tableS1').DataTable({
        lengthMenu: [[10, 30, 50, -1], [10, 30, 50, "Todo"]],
        searching: false,
    });

    $('#bootstrap-data-tableS2').DataTable({
        lengthMenu: [[10, 20, 30, 50, 100, -1], [10, 20, 30, 50, 100, "Todo"]],
        searching: false,
    });

    $('#bootstrap-data-tableS3').DataTable({
        lengthMenu: [[10, 30, 50, -1], [10, 30, 50, "Todo"]],
        searching: false,
    });

    $('#bootstrap-data-tableS4').DataTable({
        lengthMenu: [[10, 30, 50, -1], [10, 30, 50, "Todo"]],
        searching: false,
    });

    $('#bootstrap-data-tables1').DataTable({
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "Todo"]],
        searching: false,
    });

    $('#soy-primero-table').DataTable({
        lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "Todo"]],
        searching: false,
    });

    $('#bootstrap-data-table-export').DataTable({
        dom: 'lBfrtip',
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "Todo"]],
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ]
    });

    $('#row-select').DataTable({
        initComplete: function () {
            this.api().columns().every(function () {
                var column = this;
                var select = $('<select class="form-control"><option value=""></option></select>')
                    .appendTo($(column.footer()).empty())
                    .on('change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );

                        column
                            .search(val ? '^' + val + '$' : '', true, false)
                            .draw();
                    });

                column.data().unique().sort().each(function (d, j) {
                    select.append('<option value="' + d + '">' + d + '</option>')
                });
            });
        }
    });

    $('.bootstrap-data-table-custom-1').each(function(index, element){
        var self = $(element);        
        self.DataTable({
            lengthMenu: [[10, 20, 50, -1], [10, 20, 50, "Todo"]],
                    searching: false,
                    paging: false,
                    "scrollY": "350px",
                    "scrollCollapse": true,
                    "info": true,                    
        });
    });
};

(function ($) {
    //    "use strict";


    /*  Data Table
    -------------*/
    dataTableInit($);



})(jQuery);