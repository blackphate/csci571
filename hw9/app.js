var express = require('express');
var app = express();
var https = require('https');
var yelp = require('yelp-fusion');
var yelp_apikey = "mYYuG9HF4mxaBgtu6aCv9a0-CE0118IKExvzpUrWBwYP9e5hZ1bj1HEMwU57pIIajLTeZ45YBc29cqGJSAzGidDnLUTMRVc3nkRI9dxhNK5CY0hBf5PzRpTJJXnBWnYx";
var client = yelp.client(yelp_apikey);


var host = 'maps.googleapis.com';


app.use(express.static('./'));


app.use(function(req, res, next) {
	//console.log(`${req.method} request for '${req.url}'`);
	next();
});

app.get('/', function(req, res){
	res.send("Please go to hw8.html");
	
});

app.get('/geo', function(req, res){
	
	//console.log(req.query.place);
	var geopath = "/maps/api/geocode/json?address=";
	geopath += encodeURIComponent(req.query.place);
	geopath += "&key=AIzaSyBArv764sUxnpmUtd-_A5GURbiaanAMK8c";

	var options = {
		hostname: host,
		path: geopath,
		method: 'get'
	};

	https.request(options,function(response){
	
		var json = "";
	
		response.on('data', function(data){
			json += data;
		});

		response.on('end',function(){
			//console.log("It works");
			jsondata = JSON.parse(json);
			console.log(jsondata);
			if(jsondata.results.length == 0){
				res.json({
					"status" : "Not Found"
				});
			}
			else{
				loc = jsondata.results[0].geometry.location;
				res.json({
					"loc": {
						"lat" : loc.lat,
						"lng" : loc.lng
					}
			
				});
			}
		});
		
	}).end();
	

});

app.get('/nearby',function(req,res){

	var form = req.query;
	console.log(form);
	if(form.distance == ''){
		dis = 10;
	}
	else dis = form.distance;
	dis = dis * 1609.34;
	var nearbypath = "/maps/api/place/nearbysearch/json?location=";
	nearbypath += form.lat + "," + form.lng;
	nearbypath += "&radius=" + dis;
	nearbypath += "&type=" + encodeURIComponent(form.category);
	nearbypath += "&keyword=" + encodeURIComponent(form.keyword);
	nearbypath += "&key=AIzaSyDFa0wiB7HR_4fn5804KajdscYeLhEqi1w";
	//console.log(host+nearbypath);
	
	
	var options = {
		hostname: host,
		path: nearbypath,
		method: 'get'
	};

	https.request(options,function(response){
	
		var json = "";
	
		response.on('data', function(data){
			json += data;
		});

		response.on('end',function(){
			//console.log("It works");
			res.json(JSON.parse(json));
		});
		
	}).end();	


});


app.get('/detail',function(req,res){

	var form = req.query;
	console.log(form);
	if(form.distance == ''){
		dis = 10;
	}
	else dis = form.distance;
	dis = dis * 1609.34;
	var detailpath = "/maps/api/place/details/json?placeid=";
	detailpath += form.placeid;
	detailpath += "&key=AIzaSyDg3m452tWzTCkL6PwJgIl-x5LObsgmuLA";
	//console.log(host+nearbypath);
	
	
	var options = {
		hostname: host,
		path: detailpath,
		method: 'get'
	};

	https.request(options,function(response){
	
		var json = "";
	
		response.on('data', function(data){
			json += data;
		});

		response.on('end',function(){
			//console.log("It works");
			res.json(JSON.parse(json));
		});
		
	}).end();	


});

app.get('/page', function(req,res){

	console.log(req.query);
	var pagepath = "/maps/api/place/nearbysearch/json?pagetoken=";
	pagepath += req.query.token;
	pagepath += "&key=AIzaSyDFa0wiB7HR_4fn5804KajdscYeLhEqi1w";

	var options = {
		hostname: host,
		path: pagepath,
		method: 'get'
	};

	https.request(options,function(response){
	
		var json = "";
	
		response.on('data', function(data){
			json += data;
		});

		response.on('end',function(){
			//console.log("It works");
			res.json(JSON.parse(json));
		});
		
	}).end();	
	



});



app.get('/yelp', function(req, res){
	//console.log(req.query);
	
	client.businessMatch('best',req.query).then(response => {
	
		console.log(response);
		
		var busi = response.jsonBody.businesses;
		if(busi.length != 0)	{
			client.reviews(busi[0].id).then(response => {
  				res.json(response.jsonBody);
			});
		}
		else res.json({
			"status" : "Not Found"
		});
		
	});
	
	
});




app.listen(8081);

console.log("Server is running");

