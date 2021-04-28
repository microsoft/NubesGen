# See here for image contents: https://github.com/microsoft/vscode-dev-containers/tree/v0.155.1/containers/java/.devcontainer/base.Dockerfile

# [Choice] Java version: 11, 15
ARG VARIANT="11"
FROM mcr.microsoft.com/vscode/devcontainers/java:0-${VARIANT}

# Install Maven
ARG INSTALL_MAVEN="true"
ARG MAVEN_VERSION=""

# Install apt-add-repository
RUN apt-get update && apt-get install -y software-properties-common && \
    apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Install Terraform
RUN curl -fsSL https://apt.releases.hashicorp.com/gpg | (OUT=$(apt-key add - 2>&1) || echo $OUT) && \
    apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main" && \
    apt-get update && export DEBIAN_FRONTEND=noninteractive && apt-get install -y terraform && terraform -install-autocomplete && \
    apt-get clean -y && rm -rf /var/lib/apt/lists/*

# [Option] Install Node.js
ARG INSTALL_NODE="false"
ARG NODE_VERSION="lts/*"
RUN if [ "${INSTALL_NODE}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi

# [Option] Install Azure CLI
ARG INSTALL_AZURE_CLI="false"
COPY library-scripts/azcli-debian.sh /tmp/library-scripts/
RUN if [ "$INSTALL_AZURE_CLI" = "true" ]; then bash /tmp/library-scripts/azcli-debian.sh; fi \
    && apt-get clean -y && rm -rf /var/lib/apt/lists/* /tmp/library-scripts

# Install GitHub CLI
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-key C99B11DEB97541F0 && \
    apt-add-repository https://cli.github.com/packages && \
    apt-get update && \
    apt-get install gh && \
    apt-get clean -y && rm -rf /var/lib/apt/lists/*

# [Optional] Uncomment this section to install additional OS packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1