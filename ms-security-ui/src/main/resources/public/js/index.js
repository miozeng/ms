var app = angular.module('myApp', ["ngResource","ngRoute","ngCookies","angular-jwt"]);

app.controller('mainCtrl', function($scope,$resource,$http,$httpParamSerializer,$cookies,jwtHelper) {
    
	 var token = $cookies.get("access_token");
	    //alert(token);
	
	    $scope.foo = {id:1 , name:"sample foo"};
	    $scope.foos = $resource("http://localhost:8082/spring_resourceServer/foos/:fooId/?access_token="+token,{fooId:'@id'});
	    
	    $scope.getFoo = function(){
	        $scope.foo = $scope.foos.get({fooId:$scope.foo.id});
	    }
	    
	   
	    
	    //$scope.loginData = {grant_type:"password", username: "", password: "", client_id: "fooClientIdPassword"};
	    $scope.refreshData = {grant_type:"refresh_token"};
	        
	    $scope.data = {
	            grant_type:"password", 
	            username: "", 
	            password: "", 
	            client_id: "test1"
	        };
	        $scope.encoded = btoa("test1:test1secret");
	         
	        $scope.login = function() {   
	            var req = {
	                method: 'POST',
	                url: "http://localhost:8072/spring_oauthServer/oauth/token",
	                headers: {
	                    "Authorization": "Basic " + $scope.encoded,
	                    "Content-type": "application/x-www-form-urlencoded; charset=utf-8"
	                },
	                data: $httpParamSerializer($scope.data)
	            }
	            $http(req).then(function(data){
	                $http.defaults.headers.common.Authorization = 
	                  'Bearer ' + data.data.access_token;
	                $cookies.put("access_token", data.data.access_token);
	                window.location.href="index.html";
	            });   
	       }    
	    
    
});