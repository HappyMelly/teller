'use strict';

var util = require('gulp-util');

var utils = {
    errorHandler: function (error) {
        util.log([
            (error.name + ' in ' + error.plugin).bold.red,
            '',
            error.message,
            ''
        ].join('\n'));

        this.emit('end');
    }
};

module.exports = utils;