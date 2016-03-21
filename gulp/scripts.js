'use strict';

var gulp    = require('gulp'),
    utils   = require('./utils.js'),
    $       = require('gulp-load-plugins')(),
    config  = require('./config.js'),
    webpack = require('webpack-stream'),
    named   = require('vinyl-named');

module.exports = function (options, cb) {
    var firstBuild = false;

    function done(err){
        firstBuild = true;

        if (err){
            return;
        }
    }

    return gulp.src(options.target)
        .pipe($.plumber({errorHandler: utils.errorHandler}))
        .pipe(named())
        .pipe(webpack(require('./../webpack.config.js'), null, done))
        .pipe($.cached('scripts'))
        .pipe($.if(config.isBuild, $.uglify()))
        .pipe(gulp.dest(options.dst))
        .on('data', function(){
            if (firstBuild && !config.isBuild){
                cb();
            }
        })
};