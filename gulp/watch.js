
'use strict';

var gulp = require('gulp');

module.exports = function(){
    gulp.watch('frontend/css/**/*.*', gulp.series('styles'));
    // gulp.watch('frontend/js/**/*.*', gulp.series('scripts'));
};