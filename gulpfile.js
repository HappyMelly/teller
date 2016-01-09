
var gulp = require('gulp'),
    $ = require('gulp-load-plugins')();

// Javascript code style - .jscsrc ( http://jscs.info/rules )
gulp.task('jscs', function() {
    return gulp.src('app/**/*.js')
        .pipe($.jscs())
        .pipe($.jscs.reporter());
});

// Javascript hint - .jshintrc (http://jshint.com/docs/options/)
gulp.task('jshint', function() {
    return gulp.src('app/**/*.js')
        .pipe($.jshint())
        .pipe($.jshint.reporter('default'))
});


// Watch task
gulp.task('serve', ['jscs'], function () {
    gulp.watch('app/**/*.js', ['jscs']);
});


//Default task
gulp.task('default', function () {
    gulp.start('serve');
});