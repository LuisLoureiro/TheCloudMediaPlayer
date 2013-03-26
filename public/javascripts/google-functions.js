// Place this asynchronous JavaScript just before your </body> tag
(function() {
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
    po.src = 'https://apis.google.com/js/client:plusone.js?onload=start';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
})();
var googleHelper = (function() {
	var clientAccessToken = undefined;
	return {
		// Helper method that handles the authentication flow.
		signInCallback: function(authResult) {
			// This function is passed a single parameter: a JSON object with the following structure:
//			{
//			  "id_token": the user ID,
//			  "access_token": the access token,
//			  "code": one-time code that the server can exchange for its own access token and refresh token,
//			  "expires_in": the validity of the tokens, in seconds,
//			  "error": The OAuth2 error type if problems occurred,
//			  "error_description": an error message if problems occurred
//			}
			if (authResult['code']) {
		        // The user is signed in. Save the access token to be used in the client side.
		        this.clientAccessToken = authResult['access_token'];
			    // Send the code to the server
			    $.ajax({
			    	type: 'POST',
			    	url: '/auth/exchangecode',
				    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			    	success: function(result) {
//			    		// Handle or verify the server response if necessary.
//			    		// Prints the list of people that the user has allowed the app to know
//			    		// to the console.
//			    		console.log(result);
//			    		if (result['profile'] && result['people']){
//			    			$('#results').html('Hello ' + result['profile']['displayName'] + '. You successfully made a server side call to people.get and people.list');
//			    		} else {
//			    			$('#results').html('Failed to make a server-side call. Check your configuration and console.');
//			    		}
			    		window.location.assign('/user/google');
			    	},
			    	data: {
			    		'code': authResult['code'],
//			    		'csrf': 
			    	},
				    error: function(e) {
				        // Handle the error
				        console.log(e);
				    }
			    });
			} else if (authResult['error']) {
			    // There was an error.
			    // Possible error codes:
			    //   "access_denied" - User denied access to your app
			    //   "immediate_failed" - Could not automatically log in the user
			    console.log('There was an error("' + authResult['error'] + '"): ' + authResult['error_description']);
			}
		},
		disconnectUser: function() {
			var revokeUrl = 'https://accounts.google.com/o/oauth2/revoke?token=' + this.clientAccessToken;
			// Perform an asynchronous GET request.
			$.ajax({
			    type: 'GET',
			    url: revokeUrl,
			    async: false,
			    contentType: "application/json",
			    dataType: 'jsonp',
			    success: function(nullResponse) {
			        // Do something now that user is disconnected
			        // The response is always undefined.
				    // Hide the sign-out button now that the user is disconnected:
//				    document.getElementById('revokeButton').setAttribute('style', 'display: none');
//				    // Show the sign-in button
//				    document.getElementById('signinButton').setAttribute('style', 'display: inline');
				    window.location.assign('/auth/signout');
			    },
			    error: function(e) {
			        // Handle the error
			        console.log(e);
			        // You could point users to manually disconnect if unsuccessful
			        // https://plus.google.com/apps
			    }
			});
		}
	}
})();
function signInCallback(authResult) {
	googleHelper.signInCallback(authResult);
}
function disconnectUser() {
	googleHelper.disconnectUser();
}
$('#googleSignOut').click(disconnectUser);
