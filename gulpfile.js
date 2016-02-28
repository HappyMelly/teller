
var gulp = require('gulp'),
    $ = require('gulp-load-plugins')(),
    browserSync = require('browser-sync').create()

gulp.task('jscs', function() {
    return gulp.src('app/**/*.js')
        .pipe($.jscs())
        .pipe($.jscs.reporter());
});

gulp.task('jslint', function() {
    return gulp.src('app/**/*.js')
        .pipe($.eslint())
        .pipe($.eslint.format())
        .pipe($.eslint.failAfterError());
});

gulp.task('browser-sync', function() {

});

gulp.task('styles', function(){
    return gulp.src(['frontend/css/**/*.less', '!frontend/css/**/_*.less'])
        .pipe($.less())
        .pipe($.cssnano())
        .pipe(gulp.dest('public/css'));
});

gulp.task('scripts', function(){
    return gulp.src(['frontend/scripts/**/*.js'])
        .pipe(gulp.dest('public/js'));
});

gulp.task('watch', function(){
    gulp.watch('frontend/css/**/*.*', 'styles');
    gulp.watch('frontend/js/**/*.*', 'scripts');
});

// Watch task
gulp.task('dev', ['styles', 'sripts']);


//Default task
gulp.task('default', function () {
    gulp.start('dev');
});