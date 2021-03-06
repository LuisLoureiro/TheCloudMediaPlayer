// Place this asynchronous JavaScript just before your </body> tag
(function () {
    var po = document.createElement('script'),
        s = document.getElementsByTagName('script')[0];
    po.type = 'text/javascript';
    po.async = true;
    po.src = 'https://apis.google.com/js/client:plusone.js';
    s.parentNode.insertBefore(po, s);
}());
var googleHelper = (function () {
	var authResult = undefined;
	return {
		// Helper method that handles the authentication flow.
		signInCallback: function (authResult) {
			// This function is passed a single parameter: a JSON object with the following structure:
//			{
//			  "id_token": the user ID,
//			  "access_token": the access token,
//			  "code": one-time code that the server can exchange for its own access token and refresh token,
//			  "expires_in": the validity of the tokens, in seconds,
//			  "error": The OAuth2 error type if problems occurred,
//			  "error_description": an error message if problems occurred
//			}
			if (authResult.access_token) {
		        // The user is signed in. Save the access token to be used in the client side.
		        this.authResult = authResult;
		        // After we load the OAuth2 API, render the user info.
		        gapi.client.load('oauth2', 'v2', this.renderUserInfo);
			} else if (authResult.error) {
			    // There was an error.
			    // Possible error codes:
			    //   "access_denied" - User denied access to your app
			    //   "immediate_failed" - Could not automatically log in the user
			    console.log('There was an error("' + authResult.error + '"): ' + authResult.error_description);
			}
		},
		renderUserInfo: function () {
//			var request = gapi.client.plus.people.get({'userId': 'me'}); // https://developers.google.com/+/api/latest/people#resource
			var request = gapi.client.oauth2.userinfo.get();
            request.execute(function (profile) {
                // https://developers.google.com/+/web/people/
                if (profile.error) {
                    appendErrorAlert(profile.error);
                    return;
                }
			    // Send the code to the server
                googleHelper.serverExchangeCode(profile.id, profile.email, profile.name);
	        });
		},
		serverExchangeCode: function (userId, userEmail, userName) {
			$.ajax({
                type: 'POST',
                url: '/auth/exchangecode?provider=google',
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                dataType: 'json',
                data: {
                    'code': googleHelper.authResult.code,
                    'userId': userId,
                    'userEmail' : userEmail,
                    'userName' : userName
//                  'csrf': 
                },
                success: function (data) {
                    if (data && data.url) {
                        window.location.assign(data.url);
                    }
                },
			    error: function (e) {
			        // Handle the error
			        console.log(e);
			        // Clean previous errors
			        emptyAlerts();
			        appendErrorAlert(I18nJS.oauthExchangeCodeUnexpected);
			    }
		    });
		}
	};
}());
function signInCallback(authResult) {
	googleHelper.signInCallback(authResult);
}
