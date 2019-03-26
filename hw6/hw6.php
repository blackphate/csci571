
<html>
<head>
	<style>
		table{border-collapse: collapse; width: 100%;}
		td,th{border: 1px solid gray;padding-left:5px;padding-right:5px;}
		th{text-align:center;}
		a{text-decoration: none;}
		a:link{color: black;}
		a:visited{color: black;}
		.center{text-align: center;}
		.notfound{text-align: center;background-color:#f4f4f4;}
		.map{height: 100%;}
		.box{position: absolute; width: 40%; height: 300px;}
		.leftpanel{position: absolute; z-index: 2;background-color:#f4f4f4;width:25%;text-align:center;}
		.comment{height:50px; text-align:center;width:100%;}
		#output1{position:relative;left:10%;width:80%;}
		#output2{position:relative;left:25%;width:50%;}
		#distance{width:26%}
		.searchpanel{position:relative;left:25%; border: 2px solid #babbbc; 
				margin-left:5px,margin-right:5px;background-color:#f4f4f4;
				margin-top: 50px;width: 50%;}
		h1{margin-bottom: 5px;margin-top:5px;text-align: center;}
		hr{margin:5px;}
		.firstpanel{position:relative; left:1%;}
		.secondpanel{position:relative; left:50%}
		.thirdpanel{position:relative; left:10%;}
		.button{position:relative;left:20%;width:60%;background-color:white;border:none;}
		.panels{background-color:#f4f4f4; font-size: 15px;width:100%;position:relative;padding-top:10%;padding-bottom:10%;}
		.panels:hover{background-color:#babbbc;}
	</style>
	<meta charset = "UTF-8">
</head>

<body>
<div class = 'searchpanel'>
<h1><i>Travel and Entertainment Search</i></h1>
<hr/>
<form name = "myform" method = "GET" id = "myform">
<div class = "firstpanel">
<b>Keyword</b><input type = "text" name = "keyword" id = "keyword" required><br>
<b>Category</b><select name = "category"	id = "category">
	<option selected>default</option>
	<option>cafe</option>
	<option>bakery</option>
	<option>restaurant</option>
	<option>beauty salon</option>
	<option>casino</option>
	<option>movie theater</option>
	<option>lodging</option>
	<option>airport</option>
	<option>train station</option>
	<option>subway station</option>
	<option>bus station</option>
</select><br>
<b>Distance(miles)</b><input type = "text" name = "distance" id = "distance" placeholder = "10">
<b>from</b><input type = "radio" name = "location" id = "Here" value = "Here" checked onclick = "radioHere();">Here
<input type = "hidden" name = "lat" id = "lat"><input type = "hidden" name = "lon" id = "lon"><br>
</div>
<div class = "secondpanel">
<input type = "radio" name = "location" id = "location" value = "" onclick = "radioPlace()">
<input type = "text" name = "place" id = "place" value = "location" required disabled><br>
</div>
<div class = "thirdpanel">
<input type = "submit" name = "submit" id = "submit" value = "Search" disabled>
<input type = "button" name = "Clear" value = "Clear" onclick = "resetValues();">
</div>
</form>
</div>

<p id = "output1">

</p>

<p id = "output2">

</p>

</body>
</html>

<script type = "text/javascript">

function getLoc(){
	var xmlhttp = new XMLHttpRequest();
	var url = "http://ip-api.com/json";
	try{
		xmlhttp.open("GET", url, false);
		xmlhttp.send();
		document.getElementById("submit").disabled = false;
		return JSON.parse(xmlhttp.responseText);
	}
	catch(err) {document.getElementById("submit").disabled = true;}
}

function radioHere(){
	document.getElementById("place").disabled = true;
	document.getElementById("place").value = "location";
	getLoc();
}

function radioPlace(){
	document.getElementById("place").disabled = false;
	document.getElementById("place").value = "";
	document.getElementById("place").setAttribute("placeholder", "location");
	document.getElementById("submit").disabled = false;
}

function resetValues(){
	document.getElementById("myform").reset();
	document.getElementById("place").disabled = true;
	document.getElementById("place").value = "location";
	document.getElementById('output1').innerHTML = "";
	document.getElementById('output2').innerHTML = "";
}

function subSections1(){
	var hide_review = "click to hide reviews<br>";
	var hide_photos = "click to hide photos<br>";
	var arrow_up = "<img src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png' style = 'width: 10px; height: 10px;'>"
	var show_review = "click to show reviews<br>";
	var show_photos = "click to show photos<br>";
	var arrow_down = "<img src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' style = 'width: 10px; height: 10px;'>"
	if(document.getElementById("panel1").style.display === "none"){
		document.getElementById("panel1").style.display = "block";
		document.getElementById("panel2").style.display = "none";
		document.getElementById("sub1").innerHTML = hide_review + arrow_up;
		document.getElementById("sub2").innerHTML = show_photos + arrow_down;
	}
	else{
		document.getElementById("panel1").style.display = "none";
		document.getElementById("panel2").style.display = "none";
		document.getElementById("sub1").innerHTML = show_review + arrow_down;
		document.getElementById("sub2").innerHTML = show_photos + arrow_down;	
	}
}
function subSections2(){
	var hide_reviews = "click to hide reviews<br>";
	var hide_photos = "click to hide photos<br>";
	var arrow_up = "<img src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png' style = 'width: 10px; height: 10px;'>"
	var show_review = "click to show reviews<br>";
	var show_photos = "click to show photos<br>";
	var arrow_down = "<img src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' style = 'width: 10px; height: 10px;'>"	
	if(document.getElementById("panel2").style.display === "none"){
		document.getElementById("panel1").style.display = "none";
		document.getElementById("panel2").style.display = "block";
		document.getElementById("sub1").innerHTML = show_review + arrow_down;
		document.getElementById("sub2").innerHTML = hide_photos + arrow_up;
	}
	else{
		document.getElementById("panel1").style.display = "none";
		document.getElementById("panel2").style.display = "none";
		document.getElementById("sub1").innerHTML = show_review + arrow_down;
		document.getElementById("sub2").innerHTML = show_photos + arrow_down;	
	}
}

</script>

<script>
var loc = getLoc();
document.getElementById("lat").value = loc.lat;
document.getElementById("lon").value = loc.lon;
var forms = <?php echo formValues(); ?>;
for(key in forms){
	console.log(key,forms[key]);
	if(key == "keyword" || key == "category" || key == "distance"){
		document.getElementById(key).value = forms[key];
	}
	if(key == "location"){
		if(forms[key] == "Here"){
			document.getElementById("Here").checked = true;
			document.getElementById("location").checked = false;
			document.getElementById("place").disabled = true;
			document.getElementById("place").value = "location";
		}
		else{
			document.getElementById("location").checked = true;
			document.getElementById("Here").checked = false;
			document.getElementById("place").value = forms["place"];
			document.getElementById("place").disabled = false;
		}
	}
}
if(forms.hasOwnProperty("submit")){
	var json = <?php echo getNearby(); ?>;
	if(json.status == "ZERO_RESULTS") {var html_text = "<table><tr><td class = 'notfound'>No Records has been found</td></tr></table>";}
	else{
		var results = json.results;
		var html_text = "";
		html_text += "<table>\n" + "<tr>\n";
		html_text += "<th>Category</th>" + "<th>Name</th>" + "<th>Address</th>\n";
		html_text += "</tr>";
		for(var i = 0; i < results.length; i++){
			var rowContent = results[i];
			html_text += "<tr>";
			for(key in rowContent){
				if(key == "Icon"){
					html_text += "<td>";
					html_text += "<img src = '" + rowContent[key] + "' style = 'width: 50px; height:30px;'>"; 
					html_text += "</td>";
				}
				else if(key == "Name") {
					html_text += "<td><a href = './hw6.php?func=getDetails&place-id=" + rowContent["place-id"];
					for(keys in forms){
						if(keys != "submit")	html_text += "&" + keys + "=" + forms[keys];
					}
					html_text += "'>";
					html_text += rowContent[key];
					html_text += "</a></td>";
				}
				else if(key == "Address") {
					html_text += "<td>";
					html_text += "<a href = # onclick = 'initMap(" + i + "); return false;'>" + rowContent[key] + "</a>";
					html_text += "<div class = 'anchor'></div></td>";
				}
			}
			html_text += "</tr>";
	
		}
	}
	document.getElementById("output1").innerHTML = html_text;	
}
if(forms.hasOwnProperty("func")){
	var json = <?php echo getDetail(); ?>;
	var html_text = "";
	html_text += "<div class = 'center'><b>" + json.name + "</b></div><b><br><br>";
	var review = json.reviews;
	html_text += "<button class = 'button' id = 'sub1' onclick = 'subSections1();'>click to show reviews<br>";
	html_text += "<img src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' style = 'width: 10px; height: 10px;'></button>";
	html_text += "<div id = 'panel1' style = 'display:none;'>"
	html_text += "<table>";
	if(review.length == 0)	html_text += "<tr><td class = 'center'>No Reviews Found</td></tr>";
	else{
		for(var i = 0; i < review.length; i++){
			html_text += "<tr><td class = 'center'>";
			if(review[i].photo != "")	html_text += "<img src = '" + review[i].photo + "' style = 'width: 20px; height: 20px;' >" ;
			html_text += review[i].name + "</td></tr>";
			html_text += "<tr><td class = 'comment'>" + review[i].text + "</td></tr>";
		}
	}
	html_text += "</table></div>";
	html_text += "<br><br>";
	
	html_text += "<button class = 'button' id = 'sub2' onclick = 'subSections2();'>click to show photos<br>";
	html_text += "<img src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' style = 'width: 10px; height: 10px;'></button>";
	html_text += "<div id = 'panel2' style = 'display:none;'>"
	html_text += "<table>";
	if(json.photo_nums == 0)	html_text += "<tr><td class = 'center'>No Photos Found</td><tr>";
	else{
		for(var i = 0; i < json.photo_nums; i++){
			html_text += "<tr><td>";
			var photo = "./image" + i + ".png";
			html_text += "<a target='_blank' href = '" + photo + "'>";
			html_text += "<img src = '" + photo + "' style = 'width:100%;'></a>";
			html_text += "</td></tr>";
		}
	}
	html_text += "</table></div>";
	document.getElementById("output2").innerHTML = html_text;	
}
</script>


<script>
  	function initMap(i) {
  		var html_text = "";
		if(forms.hasOwnProperty("submit") && results.length != 0){
			var anchors = document.getElementsByClassName("anchor");
			var a = anchors[i].innerHTML;
			if(anchors[i].innerHTML != "")	{
				var elem = document.getElementsByClassName('box');
				elem[0].remove();
			}
			else{
				for(var j = 0; j < results.length; j++){
					anchors[j].innerHTML = "";
				}
				html_text += "<div class = 'box'>"
				html_text += "<div class = 'leftpanel'>"
				html_text += "<div class = 'panels'><a href = '' class = 'mode' onclick = 'return false;'>Walk there<br></a></div>";
				html_text += "<div class = 'panels'><a href = '' class = 'mode' onclick = 'return false;'>Bike there<br></a></div>";
				html_text += "<div class = 'panels'><a href = '' class = 'mode' onclick = 'return false;'>Drive there</a></div>";
				html_text += "</div><div class = 'map'></div>"
				html_text += "</div>";
				anchors[i].innerHTML = html_text;
				var position = json.position;
				var coor = {"start": [Number(position[0]),Number(position[1])], "des": [Number(results[i].lat), Number(results[i].lon)]}
				var directionsDisplay = new google.maps.DirectionsRenderer;
				var directionsService = new google.maps.DirectionsService;
				var des_pos = {lat: coor.des[0], lng: coor.des[1]};
				var maps = document.getElementsByClassName("map");
				var map = new google.maps.Map(maps[0], {
					zoom: 13,
					center: des_pos
				});
				var marker = new google.maps.Marker({
					position: des_pos,
					map: map
				});
				directionsDisplay.setMap(map);

				document.getElementsByClassName('mode')[0].addEventListener('click', function() {
					calculateAndDisplayRoute(directionsService, directionsDisplay,coor,i,'WALKING',marker);
				});
				document.getElementsByClassName('mode')[1].addEventListener('click', function() {
					calculateAndDisplayRoute(directionsService, directionsDisplay,coor,i,'BICYCLING',marker);
				});
				document.getElementsByClassName('mode')[2].addEventListener('click', function() {
					calculateAndDisplayRoute(directionsService, directionsDisplay,coor,i,'DRIVING',marker);
				});			
			}
		}
  	}

  	function calculateAndDisplayRoute(directionsService, directionsDisplay, coor,i,selectedMode,marker) {
  		marker.setMap(null);
		directionsService.route({
	  	origin: {lat: coor.start[0], lng: coor.start[1]},
	  	destination: {lat: coor.des[0], lng: coor.des[1]},
		travelMode: selectedMode},
		function(response, status) {
	  		if (status == 'OK') {
				directionsDisplay.setDirections(response);
	  		} else {
				window.alert('Directions request failed due to ' + status);
			}
		});
  	}
</script>
<script async defer
src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDB0gC8639anF_sbCPg3zYp0UgrWkdUmng&callback=initMap">
</script>



<?php
	function getLocHere(){
		return array($_GET["lat"], $_GET["lon"]);
	}
	function getLocSpe(){
		$url = "https://maps.googleapis.com/maps/api/geocode/json?";
		$url .= "address=" . urlencode($_GET["place"]) . "&key=AIzaSyBArv764sUxnpmUtd-_A5GURbiaanAMK8c";
		$json = file_get_contents($url);
		$loc_obj = json_decode($json);
		file_put_contents("./place.txt",$url);
		if($loc_obj -> status == "ZERO_RESULTS")	return $loc_obj;
		else{
			$loc = $loc_obj -> results[0] -> geometry -> location;
			return array($loc -> lat, $loc -> lng);
		}
	}
	
	function getNearby(){
		if(isset($_GET["submit"])){
			if($_GET["location"] == "Here")	{$loc_arr = getLocHere();}
			else	{$loc_arr = getLocSpe();}
			if(isset($loc_arr -> status) && $loc_arr -> status == "ZERO_RESULTS")	{echo json_encode($loc_arr);}
			else{
				$url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
				$url .= "location=" . $loc_arr[0] ."," . $loc_arr[1];
				if($_GET["distance"] == ""){
					$url .= "&radius=" . (10 * 1609.34);
				}
				else $url .= "&radius=" . ($_GET["distance"] * 1609.34);
				$url .= "&type=" . urlencode($_GET["category"]);
				$url .= "&keyword=" . urlencode($_GET["keyword"]);
				$url .= "&key=AIzaSyDFa0wiB7HR_4fn5804KajdscYeLhEqi1w";
				file_put_contents ("./url.txt", $url);
				$json = file_get_contents($url);
				if(json_decode($json) -> status == "ZERO_RESULTS")	{echo $json;}
				else{
					$json_nearby = json_decode($json) -> results;
					$arr = array();
					for($i = 0;$i < count($json_nearby); $i++){
						$arr[] = array("Icon" => $json_nearby[$i] -> icon,"Name" => $json_nearby[$i] -> {'name'},
							"Address" => $json_nearby[$i] -> vicinity,"place-id" => $json_nearby[$i] -> place_id,
							"lat" => $json_nearby[$i] -> geometry -> location -> lat, 
							"lon" => $json_nearby[$i] -> geometry -> location -> lng);
					}
					$places = array("results" => $arr, "position" => $loc_arr);
					echo json_encode($places,JSON_UNESCAPED_SLASHES);
				}
			}
		}
		else echo "\"\"";
	}
	function formValues(){
		if(isset($_GET["submit"]) || isset($_GET["func"])){
			echo json_encode($_GET,JSON_UNESCAPED_SLASHES);
		}
		else echo "\"\"";
	}
	/*
	function resetValues(){
		foreach($_GET as $key => $value){
			unset($_GET[$key]);
		}
	}
	*/
	function getDetail(){
		if(isset($_GET["func"])){
			$url = "https://maps.googleapis.com/maps/api/place/details/json?";
			$url .= "placeid=" . urlencode($_GET["place-id"]);
			$url .= "&key=AIzaSyDg3m452tWzTCkL6PwJgIl-x5LObsgmuLA";
			$json = file_get_contents($url);
			$json_details = json_decode($json);
			$arr_review = array();
			file_put_contents("./placeDetail.txt",$url);
			if(isset($json_details -> result -> reviews)){
				$reviews = $json_details -> result -> reviews;
				for($i = 0; $i < min(5,count($reviews)); $i++){
					if(isset($reviews[$i] -> profile_photo_url)){
						$profile = $reviews[$i] -> profile_photo_url;
					}
					else {$profile = "";}
					$arr_review[] = array("name" => $reviews[$i] -> author_name, 
						"photo" => $profile,
						"text" => $reviews[$i] -> text);
				}	
			}
			$num_photos = 0;
			if(isset($json_details -> result -> photos)){
				$photos = $json_details -> result -> photos;
				putPhotos($photos);
				$num_photos = min(5,count($photos));
			}
			$name = $json_details -> result -> name;
			$arr = array("reviews" => $arr_review, "name" => $name, "photo_nums" => $num_photos);
			echo json_encode($arr,JSON_UNESCAPED_SLASHES);
		}
		else return "\"\"";
	}
	
	function putPhotos($photos){
		if(isset($_GET["func"])){
			for($i = 0; $i < min(5,count($photos)); $i++){
				$url = "https://maps.googleapis.com/maps/api/place/photo?";
				$url .= "maxwidth=" . (min(1600, $photos[$i] -> width));
				$url .= "&photoreference=" . $photos[$i] -> photo_reference;
				$url .= "&key=AIzaSyD-rrnN2fTxbyVFhhIY-Gf32urL_SKR7e4";
				$file_name = "./image" . $i .".png";
				file_put_contents("./img.txt", $url);
				$photo = file_get_contents($url);
				file_put_contents($file_name, $photo);
			}
		}
	}
?>




