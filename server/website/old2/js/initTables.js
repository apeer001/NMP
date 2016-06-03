  function init_table(tableid) {
    $(document).ready(function() {
      $('#example' + tableid).DataTable( {
        "scrollY":        "200px",
        "scrollCollapse": true,
        "paging":         false
      } );
    } );
  }