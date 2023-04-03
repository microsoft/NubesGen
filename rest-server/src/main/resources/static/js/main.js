(function (c, l, a, r, i, t, y) {
    c[a] = c[a] || function () {
        (c[a].q = c[a].q || []).push(arguments)
    };
    t = l.createElement(r);
    t.async = 1;
    t.src = "https://www.clarity.ms/tag/" + i;
    y = l.getElementsByTagName(r)[0];
    y.parentNode.insertBefore(t, y);
})(window, document, "clarity", "script", "4zmkonp2tw");


function slugify(text) {
    return text.toString().toLowerCase()
        .replace(/\s+/g, '-')           // Replace spaces with -
        .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
        .replace(/\-\-+/g, '-')         // Replace multiple - with single -
        .replace(/^-+/, '')             // Trim - from start of text
        .replace(/-+$/, '');            // Trim - from end of text
}

function nubesGen() {
    return {
        projectName: 'demo',
        projectSlug: 'demo',
        region: 'eastus',
        region2: {id: 'eastus', name: 'USA - East (Virginia)', flag: './img/us.svg'},
        regionName: 'USA - East',
        regionFlag: './img/us.svg',
        iaCToolId: 'TERRAFORM',
        iaCTool: { id: 'TERRAFORM', name: 'Terraform', flag: './img/terraform.png' },
        iaCToolName: 'Terraform',
        iaCToolFlag: './img/terraform.png',
        components: {frontApp: {type: 'APP_SERVICE', size: 'free'}, database: {type: 'NONE', size: 'free'}},
        addons: [],
        runtime: "DOCKER",
        network: '',
        gitops: false,
        curlScript: '',
        poshScript: '',
        copySuccessful: false,
        updatePageParameters() {
            const url = new URL(window.location.href);
            url.searchParams.set('frontApp.type', this.components.frontApp.type);
            url.searchParams.set('frontApp.size', this.components.frontApp.size);
            history.pushState(null, document.title, url.toString());

        },
        updateAppType(appType) {
            this.components.frontApp.type = appType;
            if (appType === 'APP_SERVICE') {
                this.components.frontApp.size = 'free'
            } else if (appType === 'FUNCTION') {
                this.components.frontApp.size = 'consumption'
            } else {
                this.components.frontApp.size = 'basic'
            }
        },
        generateScriptUrl() {
            var url = this.generateUrl();
            this.curlScript = `curl "${window.location.protocol}//${window.location.host}/${this.projectSlug}.tgz${url}" | tar -xzvf -`;
            this.poshScript = `Invoke-WebRequest -Uri "${window.location.protocol}//${window.location.host}/${this.projectSlug}.zip${url}" -OutFile ${this.projectSlug}.zip; Expand-Archive ${this.projectSlug}.zip -DestinationPath '.' -Force; Remove-Item ${this.projectSlug}.zip`;

        },
        async copyScript(scriptContents, $dispatch) {
            this.copySuccessful = await navigator.clipboard.writeText(scriptContents);
            $dispatch('downloadpackage', { hasGitops: this.gitops});
        },
        download($dispatch) {
            var url = `${this.projectSlug}.zip${this.generateUrl()}`;
            window.open(url, '_blank');
            $dispatch('downloadpackage', { hasGitops: this.gitops});
        },
        generateUrl() {
            if (this.projectSlug.trim() === '') {
                document.getElementById('projectNameInput').focus();
                return;
            }
            this.updatePageParameters();
            var url = `?iactool=${this.iaCToolId}&region=${this.region}&application=${this.components.frontApp.type}.${this.components.frontApp.size}&runtime=${this.runtime}&database=${this.components.database.type}.${this.components.database.size}`;

            if (this.addons.length > 0) {
                url += `&addons=${this.addons.join(",")}`;
            }
            if (this.gitops === true) {
                url += "&gitops=true";
            }
            if (this.network === 'VIRTUAL_NETWORK') {
                url += "&network=VIRTUAL_NETWORK";
            }
            return url;
        },
        allRegions:
            [
                {id: 'eastus', name: 'USA - East (Virginia)', flag: './img/us.svg'},
                {id: 'eastus2', name: 'USA - East 2 (Virginia)', flag: './img/us.svg'},
                {id: 'centralus', name: 'USA - Central (Iowa)', flag: './img/us.svg'},
                {id: 'northcentralus', name: 'USA - North Central (Illinois)', flag: './img/us.svg'},
                {id: 'southcentralus', name: 'USA - South Central (Texas)', flag: './img/us.svg'},
                {id: 'westus', name: 'USA - West (California)', flag: './img/us.svg'},
                {id: 'westus2', name: 'USA - West 2 (California)', flag: './img/us.svg'},
                {
                    id: 'brazilsouth',
                    name: 'South America - Brazil South (Sã²o Paulo State)',
                    flag: './img/br.svg'
                },
                {id: 'francecentral', name: 'Europe - France Central (Paris)', flag: './img/fr.svg'},
                {id: 'francesouth', name: 'Europe - France South (Marseille)', flag: './img/fr.svg'},
                {id: 'northeurope', name: 'Europe - North Europe (Ireland)', flag: './img/eu.svg'},
                {id: 'westeurope', name: 'Europe - West Europe (Netherlands)', flag: './img/eu.svg'},
                {id: 'australiaeast', name: 'Asia Pacific - Australia East (Sydney)', flag: './img/au.svg'},
                {
                    id: 'australiasoutheast',
                    name: 'Asia Pacific - Australia Southeast (Melbourne)',
                    flag: './img/au.svg'
                },
            ],
        appServiceRuntimes: [
            {id: 'DOCKER', name: 'Docker with a Dockerfile'},
            {id: 'JAVA', name: 'Java (generic) with Maven'},
            {id: 'JAVA_GRADLE', name: 'Java (generic) with Gradle'},
            {id: 'DOTNET', name: '.NET'},
            {id: 'NODEJS', name: 'JavaScript (Node.js)'},
            {id: 'PYTHON', name: 'Python'},
            {id: 'SPRING', name: 'Spring Boot: Java build with Maven'},
            {id: 'SPRING_GRADLE', name: 'Spring Boot: Java build with Gradle'},
            {id: 'DOCKER_SPRING', name: 'Spring Boot: Docker build with Maven'},
            {id: 'QUARKUS', name: 'Quarkus: Java build with Maven'},
            {id: 'QUARKUS_NATIVE', name: 'Quarkus: Docker build (native) with Maven'},
            {id: 'MICRONAUT', name: 'Micronaut: Java build with Maven'},
            {id: 'MICRONAUT_GRADLE', name: 'Micronaut: Java build with Gradle'},
            {id: 'DOCKER_MICRONAUT', name: 'Micronaut: Docker build with Maven'},
            {id: 'DOCKER_MICRONAUT_GRADLE', name: 'Micronaut: Docker build with Gradle'}
        ],
        functionsRuntimes: [
            {id: 'JAVA', name: 'Java (generic) with Maven'},
            {id: 'JAVA_GRADLE', name: 'Java (generic) with Gradle'},
            {id: 'DOTNET', name: '.NET'},
            {id: 'NODEJS', name: 'JavaScript (Node.js)'},
            {id: 'PYTHON', name: 'Python'},
            {id: 'SPRING', name: 'Spring Boot: Java build with Maven'},
            {id: 'SPRING_GRADLE', name: 'Spring Boot: Java build with Gradle'}
        ],
        ascRuntimes:[
            {id: 'SPRING', name: 'Spring Boot: Java build with Maven'},
            {id: 'SPRING_GRADLE', name: 'Spring Boot: Java build with Gradle'}
        ],
        acaRuntimes:[
            {id: 'DOCKER', name: 'Docker App'},
            {id: 'SPRING', name: 'Spring Boot: Java build with Maven'},
            {id: 'SPRING_GRADLE', name: 'Spring Boot: Java build with Gradle'},
        ],
        networkConfigurations:[
            {id: 'PUBLIC', name: 'Public'},
            {id: 'VIRTUAL_NETWORK', name: 'Virtual Network (VNet) - Preview'}
        ],
        regionSelectorOpened: false,
        selectRegion(selectedRegion) {
            this.region2 = selectedRegion;
            this.region = selectedRegion.id;
            this.regionSelectorOpened = false;
        },
        allIaCTools: [
                { id: 'TERRAFORM', name: 'Terraform', flag: './img/terraform.png' },
                { id: 'BICEP', name: 'Bicep (preview)', flag: './img/bicep.png' },
            ],
        selectIaCTool(selectedIaCTool) {
            this.iaCTool = selectedIaCTool;
            this.iaCToolId = selectedIaCTool.id;
            this.iaCToolSelectorOpened = false;
        },
        iaCToolSelectorOpened: false,
    }
}

function replaceState(value) {
    const url = new URL(window.location.href);
    url.searchParams.set('frontApp.type', value.frontApp.type);
    url.searchParams.set('frontApp.size', value.frontApp.size);
    url.searchParams.set('database.type', value.database.type);
    url.searchParams.set('database.size', value.database.size);
    history.pushState(null, document.title, url.toString());
}
