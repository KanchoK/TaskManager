function logout() {
	$.ajax({
		url: 'rest/user/logout',
		type: 'POST',
		success: function() {
			window.location.replace("/TaskManager/");
		}
				
	});
}

function authorization() {
	$.ajax({
		url: 'rest/user/authorization',
		type: 'GET',
		success: function(data) {
			hideButtons(data);
		}			
	});
}

function hideButtons(isAdmin) {
	
	if(isAdmin) {
		$('#logoutButton').show();
        $('#createTaskButton').show();
        $('#createUserButton').show();
        $('#editProfileButton').show();
        $('#homeButton').show();
	} else {
		$('#logoutButton').show();
        $('#createTaskButton').hide();
        $('#createUserButton').hide();
        $('#editProfileButton').show();
        $('#homeButton').show();
	}
}