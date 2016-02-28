"use strict";

var webpack = require('webpack'),
    path = require('path');

module.exports = {
    output: {
        filename: '[name].js'
    },
    devtool: "#inline-source-map",
    cache: true,
    module: {
        loaders: [
            {
                test: /\.jsx$/,
                loaders: ['babel?presets[]=react,presets[]=es2015']
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