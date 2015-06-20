function logout() {
	$.ajax({
		url: 'rest/user/logout',
		type: 'POST',
		success: function() {
			window.location.replace("index.html");
		}
				
	});
}