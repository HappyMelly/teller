
var gulp = require('gulp'),
    $ = require('gulp-load-plugins')(),
    browserSync = require('browser-sync').create(),
    runSequence =  require('run-sequence'),
    gulpIf = require('gulp-if'),
    named = require('vinyl-named'),
    webpack = require('webpack-stream');

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
        .pipe($.cached('styles'))
        .pipe(gulpIf(isBuild, $.cssnano()))
        .pipe(gulp.dest('public/css'));
});

gulp.task('scripts', function(){

    return gulp.src(['frontend/scripts/**/*.js', '!frontend/scripts/**/_*.js'], {base: 'frontend/scripts' })
        .pipe(named())
        .pipe(webpack(require('./webpack.config.js')))
        .pipe($.cached('scripts'))
        .pipe($.if(isBuild, $.uglify()))
        .pipe(gulp.dest('public/js/package'));
});

gulp.task('watch', function(){
    gulp.watch('frontend/css/**/*.*', gulp.series('styles'));
    gulp.watch('frontend/js/**/*.*', gulp.series('scripts'));

    browserSync.watch('public/**/*.*').on('change', browserSync.reload);
});


// Common task
gulp.task('run', gulp.parallel('styles', 'scripts'));

gulp.task('dev', gulp.series('run', gulp.parallel('watch', 'browser-sync')));

gulp.task('build', gulp.series('run'));

gulp.task('default', gulp.series('dev'));