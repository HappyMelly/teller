'use strict';

var gulp = require('gulp'),
    $    = require('gulp-load-plugins')();

module.exports = function (options) {
    return gulp.src(options.target)
        .pipe($.eslint())
        .pipe($.eslint.format())
        .pipe($.eslint.failAfterError());
};