'use strict';

var gulp    = require('gulp'),
    utils   = require('./utils.js'),
    $       = require('gulp-load-plugins')(),
    config  = require('./config.js');

module.exports = function (options) {
    return gulp.src(options.target)
        .pipe($.plumber({errorHandler: utils.errorHandler}))
        .pipe($.less())
        .pipe($.cached('styles'))
        .pipe($.if(config.isBuild, $.cssnano()))
        .pipe(gulp.dest(options.dst));
};