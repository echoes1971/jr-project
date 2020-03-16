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

    $scope.root_obj = ['-10','Home'];
    $scope.menu_top = [ ['-13','Downloads'], ['-14','About us'],  ];
    $scope.parent_list = [{'id':'aaa','name':'Parent 1'},{'id':'aab','name':'Parent 2'},{'id':'aac','name':'Parent 3'}];
/*
    $http.get('/ui')
        .then(function mySuccess(response) {
            $scope.myui = response.data;
            console.log($scope.myui);

        }, function myError(response) {
            $scope.pmgrError = response;
            console.log('Error: ' + response);
        });
*/
});
