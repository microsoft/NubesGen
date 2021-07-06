param location string
param applicationName string

var appServicePlanName = 'plan-${applicationName}-001'

resource appServicePlan 'Microsoft.Web/serverFarms@2020-12-01' = {
  name: appServicePlanName
  location: location
  kind: 'linux'
  properties: {
    reserved: true
  }
  sku: {
    name: 'S1'
    tier: 'Standard'
  }
}

resource appServiceApp 'Microsoft.Web/sites@2020-12-01' = {
  name: applicationName
  location: location
  properties: {
    serverFarmId: appServicePlan.id
    httpsOnly: true
    siteConfig: {
      linuxFxVersion: 'JAVA|11-java11'
      alwaysOn: false
      use32BitWorkerProcess: true
      ftpsState: 'FtpsOnly'
      appSettings: [
        {
          name: 'WEBSITES_ENABLE_APP_SERVICE_STORAGE'
          value: 'false'
        }
      ]
    }
  }
}
