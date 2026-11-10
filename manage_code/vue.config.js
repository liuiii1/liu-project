// var webpack = require('webpack');
//vue2
const path = require('path')

function resolve(dir) {
	return path.join(__dirname, dir)
}

function publicPath() {
	if (process.env.NODE_ENV == 'production') {
		return "././";
	} else {
		return "/";
	}
}
// vue.config.js
module.exports = {
publicPath: publicPath(),
    configureWebpack: {
        resolve: {
            fallback: { path: require.resolve("path-browserify") },
        },
     },
    lintOnSave: false,
    devServer: {
        host: "0.0.0.0",
        port: 8081,
        hot: true,
        https: false,
        // 直接布尔值关闭浮层（兼容新版webpack-dev-server）
        client: {
            overlay: false
        },
        proxy: {
            [process.env.VUE_APP_BASE_API]: {
                target: process.env.VUE_APP_BASE_API_URL,
                changeOrigin: true,
                secure: false,
                pathRewrite: {
                    ['^' + process.env.VUE_APP_BASE_API]: process.env.VUE_APP_BASR_API_PREFIX
                }
            }
        }
    },
	chainWebpack(config) {
		config.module
			.rule('svg')
			.exclude.add(resolve('src/icons'))
			.end()
		config.module
			.rule('icons')
			.test(/\.svg$/)
			.include.add(resolve('src/icons'))
			.end()
			.use('svg-sprite-loader')
			.loader('svg-sprite-loader')
			.options({
				symbolId: 'icon-[name]'
			})
			.end()
	}
}
