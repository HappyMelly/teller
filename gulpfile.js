
var gulp = require('gulp'),
    $ = require('gulp-load-plugins')(),
    named = require('vinyl-named'),
    webpack = require('webpack-stream');


function lazyLoad(name, path, options){
    options = options || {};
    options.name = options.name || name;

    gulp.task(name, function(done){
        var task = require(path);
        return task(options, done);
    })
}

lazyLoad('styles', './gulp/styles.js', {
    target: ['frontend/css/**/*.less', '!frontend/css/**/_*.less'],
    dst: 'public/stylesheets'
});

lazyLoad('scripts', './gulp/scripts.js', {
    target: ['frontend/js/**/*.js', '!frontend/js/**/_*.js'],
    dst: 'public/js/package'
});

lazyLoad('jscs', './gulp/js-codestyle.js', {
    target: 'frontend/**/*.js'
});

lazyLoad('jslint', './gulp/js-lint.js', {
    target: 'frontend/**/*.js'
});

lazyLoad('watch', './gulp/watch.js');


/*== Tasks ==*/
gulp.task('prepare', gulp.parallel('styles', 'scripts'));

gulp.task('dev', gulp.series('prepare', gulp.parallel('watch')));

gulp.task('build', gulp.series('prepare'));

gulp.task('default', gulp.series('dev'));