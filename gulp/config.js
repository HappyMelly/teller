"use strict";

var isBuild = process.env.NODE_ENV && process.env.NODE_ENV == 'production',
    config = {
        isBuild: isBuild
    };

module.exports = config;