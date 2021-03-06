// Additional JS functions here
window.fbAsyncInit = function() {
    FB.init({
        appId      : '390901374297616', // App ID
        channelUrl : '//thecloudmediaplayer.herokuapp.com/assets/channel.html', // Channel File
        status     : true, // check login status
        cookie     : true, // enable cookies to allow the server to access the session
        xfbml      : true  // parse XFBML
    });
    // Additional init code here
    FB.getLoginStatus(function(response) {
    	if (response.status === 'connected') {
    	    // connected
    	} else if (response.status === 'not_authorized') {
    	    // not_authorized
    	    // login();
    	} else {
    	    // not_logged_in
    	    // login();
    	}
    });
    function login() {
        FB.login(function(response) {
            if (response.authResponse) {
                // connected
                testAPI();
            } else {
                // cancelled
            }
        });
    }
    function testAPI() {
        console.log('Welcome!  Fetching your information.... ');
        FB.api('/me', function(response) {
            console.log('Good to see you, ' + response.name + '.');
        });
    }
  };
// Load the SDK Asynchronously
(function(d){
    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
    if (d.getElementById(id)) {return;}
    js = d.createElement('script'); js.id = id; js.async = true;
    js.src = "//connect.facebook.net/en_US/all.js";
    ref.parentNode.insertBefore(js, ref);
}(document));