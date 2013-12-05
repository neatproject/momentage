<!DOCTYPE html>
<html>
  <head>
  <meta charset="UTF-8">
    <title>Example</title>
  </head>
  <body>
    <!-- Load the Facebook JavaScript SDK -->
    <div id="fb-root"></div>
    <script src="//code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="//connect.facebook.net/en_US/all.js"></script>
    
    <script type="text/javascript">
      
      // Initialize the Facebook JavaScript SDK
      FB.init({
        appId: '1433728110188030',
        xfbml: true,
        status: true,
        cookie: true,
      });
      
      // Check if the current user is logged in and has authorized the app
      FB.getLoginStatus(checkLoginStatus);
      
      // Login in the current user via Facebook and ask for email permission
      function authUser() {
        FB.login(checkLoginStatus, {scope:'email'});
      }
      
      // Check the result of the user status and display login button if necessary
      function checkLoginStatus(response) {
        if(response && response.status == 'connected') {
          // Hide the login button
          document.getElementById('loginButton').style.display = 'none';
          
          $.ajax({
            url: "https://familymelon.com/momentage/authenticate.php",
            type: "post",
            data: {
              userID: response.authResponse.userID
            },
            dataType: "json",
            success: function (response) {
              // setup config
              $('#widget_api_txt').html(response.widget.html);
              $('#request_api_uri').attr('href', response.widget.uri);

              // show page
              $('#page').fadeIn();
            },
            error: function () {
              throw "ocurrio un error!";
            }
          });
        } else {
          // Display the login button
          document.getElementById('loginButton').style.display = 'block';
        }
      }
    </script>
    
    <input style="display:none;" id="loginButton" type="button" value="Login!" onclick="authUser();" />

    <div id="page" style="display:none;">
      <h1>Widget API</h1>
      <textarea id="widget_api_txt" style="width:100%;" name="" id="" cols="30" rows="15"></textarea>
      <h1>Request Your Data using Your ClientID</h1>
      <p><a href="#" id="request_api_uri" target="_blank">Show my Logs</a></p>
    </div>
  </body>
</html>