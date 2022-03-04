module.exports = {
    title: "NubesGen Documentation",
    description: "From code to deployment in minutes",
    themeConfig: {
        logo: '/assets/nubesgen-logo.svg',
        repo: 'microsoft/nubesgen',
        docsDir: 'docs',
        docsBranch: 'docs-v2',
        editLinks: true,
        lastUpdated: 'Last Updated',
        sidebarDepth: 1,
        smoothScroll: true,
        nav: [
         { text: "Home", link: '/' },
         { text: "Back to NubesGen.com", link: 'https://nubesgen.com' }
        ],
      displayAllHeaders: true,
      sidebar: [
          {
            title: '🔎 What is NubesGen?',
            path: '/what-is-nubesgen/overview/',
            collapsable: false,
            sidebarDepth: 0,
            children: [
              {
                title: 'Overview',
                path: '/what-is-nubesgen/overview',
              },
              '/what-is-nubesgen/features',
              '/what-is-nubesgen/philosophy',
              '/what-is-nubesgen/telemetry',
              '/what-is-nubesgen/contact',
            ],
          },
          {
            title: '🚀 Getting started',
            path: '/getting-started',
            collapsable: false,
            sidebarDepth: 0,
            children: [
              '/getting-started/terraform',
              '/getting-started/bicep',
              '/getting-started/gitops',
              '/getting-started/cli',
            ],
          },
          {
            title: '⌨️ Language support',
            
            collapsable: false,
            sidebarDepth: 0,
            children: [
              '/language-support/docker',
              '/language-support/dot-net',
              '/language-support/java',
              '/language-support/quarkus',
              '/language-support/spring-boot',
              '/language-support/nodejs',
            ],
          },
          {
            title: '🪅 GitOps',
            path: '/gitops/gitops-overview',
            collapsable: false,
            sidebarDepth: 0,
            children: [
              '/gitops/gitops-quick-start',
            ],
          },
          {
            title: '👐 Contributing',
            path: '/contributing/contributing',
            collapsable: true,
            sidebarDepth: 0,
            children: [
                '/contributing/contributing',
              '/contributing/bug-report',
              '/contributing/feature-request',
              '/contributing/documentation',
            ],
          },
          {
            title: '📚 Reference',
            path: '/reference',
            collapsable: true,
            sidebarDepth: 0,
            children: [
              '/reference/frequently-asked-questions',
              '/reference/troubleshooting',
              '/reference/rest-api',
              '/reference/what-is-being-generated',
            ],
          },
          {
            title: '✨ Community content',
            path: '/community-content'
          }
      ]
    },
    
  };