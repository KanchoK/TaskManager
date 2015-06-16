function logout() {
	$.ajax({
		url: 'rest/user/logout',
		type: 'GET',
		success: function() {
			window.location.replace("index.html");
		}
				
	});
}