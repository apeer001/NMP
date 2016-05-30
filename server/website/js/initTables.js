  function init_table(tablename) {
    $(document).ready(function() {
      $(tablename).DataTable( {
        "scrollY":        "200px",
        "scrollCollapse": true,
        "paging":         false
      } );
    } );
  }