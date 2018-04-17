$(function(){
   $('#query').click(function(){
       $('#dg').datagrid('load', $('#frmQuery').serializeObject()); 
   });
   $('#add').click(function(){
       edit(false);
   });
   $('#update').click(function(){
       edit(true);
   });
   $('#delete').click(function(){
       var row = $('#dg').datagrid('getSelected');
       if(!row) return;
       openConfirmDeleteDialog(function(){
           $.post(CONFIG.baseUrl + 'volunteerHelp/delete.do?id=' + row.id, function(ret){
               showOpResultMessage(ret);
               $('#dg').datagrid('load');
           });
       });
   });
   
   function edit(update) {
       var url = 'volunteerHelp/form.do';
       if (update) {
           var row = $('#dg').datagrid('getSelected')
           if (!row)
               return;
           url += '?id=' + row.id;
       }
       
       var dlg = openEditDialog({
           title: '编辑',
           width: 500,
           height: 440,
           href: url,
           onSave: function() {
               formSubmit({
                   url: 'volunteerHelp/save.do',
                   success: function(ret) {
                       if (ret.success) {
                           dlg.dialog('destroy');
                           $('#dg').datagrid('load');
                       }
                       showOpResultMessage(ret);
                   }
               });
           }
       });
   }
});