
var popup = null;

function sendMainPage() {
   popup.close();
   window.location = getHost() + "/index.html";
}

$(document).ready(function() {

   //Facebook signin
   $('#facebook-sign').click(function(e) {
      e.preventDefault();
      popup = window.open("/eventjuggler-rest/facebook", "name", "height=512, width=512");
      popup.focus();
      popup.window.reload = function(){
         if(popup.document.body.innerHTML.indexOf("true") > -1){
            popup.close();
            var jqxhr = $.ajax('/eventjuggler-rest/facebook', {
               contentType: "application/json",
               dataType:'json',
               type:'POST',
               success:function (data) {
                  if (data.loggedIn) {
                     storeToken(data.token);
                     window.location = getHost() + "/index.html";
                  } else {
                     $('#login-msg').text("Authentication failed. Try again ...");
                  }
               }
            });
            //window.location = getHost() + "/index.html";
         }
      };

      return false; // prevents submit of the form
   });
});