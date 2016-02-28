
var gulp = require('gulp'),
    $ = require('gulp-load-plugins')(),
    browserSync = require('browser-sync').create(),
    runSequence =  require('run-sequence'),
    gulpIf = require('gulp-if');

var isBuild = process.env.NODE_ENV && process.env.NODE_ENV == 'production',
    errorHandler = function (error) {
        util.log([
            (error.name + ' in ' + error.plugin).bold.red,
            '',
            error.message,
            ''
        ].join('\n'));
        this.emit('end');
    };

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
    browserSync.init({
        baseDir: './public',
        proxy: 'http://localhost:9000'
    })
});

gulp.task('styles', function(){
    return gulp.src(['frontend/css/**/*.less', '!frontend/css/**/_*.less'])
        .pipe($.plumber({errorHandler: errorHandler}))
        .pipe($.less())
        .pipe(gulpIf(isBuild, $.cssnano()))
        .pipe(gulp.dest('public/css'));
});

gulp.task('scripts', function(){
    return gulp.src(['frontend/scripts/**/*.js', '!frontend/scripts/**/_*.js'])
        .pipe(gulp.dest('public/js'));
});

gulp.task('watch', function(){
    gulp.watch('frontend/css/**/*.*', 'styles');
    gulp.watch('frontend/js/**/*.*', 'scripts');

    browserSync.watch('public/**/*.*').on('change', browserSync.reload);
});


// Common task
gulp.task('run', function(done){
    runSequence(['styles', 'scripts'], done);
});

gulp.task('dev', function(done){
    runSequence('run', ['watch', 'browser-sync'])
});

gulp.task('build', ['run']);

gulp.task('js-valid', ['jscs', 'jslint']);

gulp.task('default', function () {
    gulp.start('dev');
});