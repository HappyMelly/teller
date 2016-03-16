"use strict";

var webpack = require('webpack'),
    path    = require('path'),
    config  = require('./gulp/config.js');

module.exports = {
    watch: !config.isBuild,
    output: {
        filename: '[name].js'
    },
    devtool: 'inline-source-map',
    cache: true,
    module: {
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loaders: ['babel?presets[]=es2015']
            }
        ]
    },
    plugins: [
        new webpack.optimize.DedupePlugin(),
        new webpack.NoErrorsPlugin()
    ],
    resolve: {
        root: __dirname + '/frontend/js'
    }
};