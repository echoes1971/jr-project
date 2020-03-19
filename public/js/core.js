// public/core.js
var app = angular.module('rprjApp', ['ngRoute']);

app.run(function($rootScope) {
    $rootScope.pageTitle='R-Prj';
});

app.controller('mainCtrl', function($scope, $http, $interval) {
    $scope.title = 'R-Prj';
    $scope.formData = {};

    //$scope.myuser = { 'fullname':'echoes'};
    $scope.myuser = null;

    $scope.root_obj = {}; //{'id':'-10','name':'Home'};
    $scope.menu_top = []; //[ {'id':'-13','name':'Downloads'}, {'id':'-14','name':'About us'},  ];
    $scope.parent_list = []; //[{'id':'aaa','name':'Parent 1'},{'id':'aab','name':'Parent 2'},{'id':'aac','name':'Parent 3'}];

    $scope.indent = "";
    $scope.menu_items = [
        {'id':'-13','name':'Downloads','icon':'glyphicon-folder-close'},
        {'id':'-14','name':'About us','icon':'glyphicon-folder-close'},
        ];

    $http.get('/ui/rootobj')
        .then(function mySuccess(response) {
            $scope.root_obj = response.data;
            console.log($scope.root_obj);
        }, function myError(response) {
            $scope.rprjError = response;
            console.log('Error: ' + response);
        });
    $http.get('/ui/topmenu')
        .then(function mySuccess(response) {
            $scope.menu_top = response.data;
            console.log($scope.menu_top);
        }, function myError(response) {
            $scope.rprjError = response;
            console.log('Error: ' + response);
        });
    $http.get('/ui/parentlist')
        .then(function mySuccess(response) {
            $scope.parent_list = response.data;
            console.log($scope.parent_list);
        }, function myError(response) {
            $scope.rprjError = response;
            console.log('Error: ' + response);
        });
/*
    $http.get('/ui')
        .then(function mySuccess(response) {
            $scope.myui = response.data;
            console.log($scope.myui);

        }, function myError(response) {
            $scope.rprjError = response;
            console.log('Error: ' + response);
        });
*/
});
