/** @type {import('next').NextConfig} */


const nextConfig = {
    transpilePackages: ['@mdxeditor/editor', 'react-diff-view','wkhtmltopdf/index'],
    reactStrictMode: true,
    webpack: (config,{isServer}) => {
        if (!isServer) {
            config.node = {
                fs: 'empty'
            }
        }
        config.module.rules.push({
            test: /\.(woff|woff2)$/, // Correspondência de extensões de arquivo WOFF
            use: {
                loader: 'file-loader', // Use o file-loader
                options: {
                name: 'fonts/[name].[ext]', // Nome de saída do arquivo
                },
            },
        });
        // this will override the experiments
        config.experiments = { serverActions: true,topLevelAwait: true };
        // this will just update topLevelAwait property of config.experiments
        // config.experiments.topLevelAwait = true 
        return config;
    },
}

