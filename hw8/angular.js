var app = angular.module('myapp',['ngAnimate']);

app.controller('myController', function($scope){
	$scope.favor = false
	
	$scope.reset = function(){
		$scope.favor = true
	}
	
	$scope.create = function(){
		$scope.favor = true
	}
	
});


