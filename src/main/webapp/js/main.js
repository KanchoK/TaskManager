//Global variable
var isCurrentUserAdmin = null;

function logout() {
	$.ajax({
		url: 'rest/user/logout',
		type: 'POST',
		success: function() {
			isCurrentUserAdmin = null;
			window.location.replace("/TaskManager/");
		}
				
	});
}

function authorization() {
	if (isCurrentUserAdmin == null) {
		$.ajax({
			url: 'rest/user/authorization',
			type: 'GET',
			success: function(data) {
				isCurrentUserAdmin = data;
				hideButtons(isCurrentUserAdmin);
			}			
		});
	} else {
		hideButtons(isCurrentUserAdmin);
	}
}

function hideButtons(isAdmin) {
	
	if(isAdmin) {
		$('#logoutButton').show();
        $('#adminPanelButton').show();
        $('#createUserButton').show();
        $('#editProfileButton').show();
        $('#homeButton').show();
        $('#changesButton').show();
        $('#changeButtonsDiv').show();
	} else {
		$('#logoutButton').show();
        $('#adminPanelButton').hide();
        $('#editProfileButton').show();
        $('#homeButton').show();
        $('#changesButton').hide();
        $('#changeButtonsDiv').hide();
	}
}

function dateParser(date) {
	var tempDate = new Date(date);
	var month = ["January", "February", "March", "April", "May", "June",
	                "July", "August", "September", "October", "November", "December"];
	var parsedDate = tempDate.getDate() + " " + month[tempDate.getMonth()] + " " + tempDate.getFullYear();
	return parsedDate
}

function dateParserWithTime(date) {
	var parsedDate = dateParser(date);
	var tempDate = new Date(date);
	var parsedDateAndTime = parsedDate + " " + tempDate.getHours() + ":" + tempDate.getMinutes() + ":" + tempDate.getSeconds();
	return parsedDateAndTime
}

function showPopup(elementId) {
	document.getElementById(elementId).style.display = 'block';
}

function hidePopup(elementId) {
	document.getElementById(elementId).style.display = 'none';
}