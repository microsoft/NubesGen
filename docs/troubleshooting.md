[[ << Frequently asked questions ](frequently-asked-questions.md) | [ Main documentation page >> ](README.md)]

# Troubleshooting

If you're encountering some oddities using _NubesGen_, here's a list of resolutions to some of the problems you may be experiencing.

## The subscription '1234-abcd-1234abcd' could not be found

NubesGen needs to connect to Azure so it can create and configure resources.
For that, it uses your Azure subscription.
But you might have several subscriptions, some might have expired, or your local account could be desynchronized with your remote account.
So you might have the following error message:

```shell
$ bash -c "$(curl -fsSL https://nubesgen.com/gitops/setup.sh)"

(1/8) Checking Azure CLI status...
(2/8) Checking GitHub CLI status...
(3/8) Create resource group "rg-terraform-001"
The subscription '1234-abcd-1234abcd' could not be found.
```

To solve this problem, first, check your account and the list of subscriptions you have locally:

```shell
$ az account show

$ az account list
[
  {
    "cloudName": "AzureCloud",
    "homeTenantId": "1234-abcd-1234abcd",
    "isDefault": true,
    "user": {
      "name": "foo.bar@team.com"
    }
  },
  {
    "cloudName": "AzureCloud",
    "homeTenantId": "zyxw-9876-9876zyxw",
    "isDefault": false,
    "user": {
      "name": "foo.bar@team.com"
    }
  }
]
```

In the list of subscription above, `1234-abcd-1234abcd` is the default one (`"isDefault": true`).
If you need to change it, and set the subscription `zyxw-9876-9876zyxw` to be the default, execute the following command:

```shell
$ az account set --subscription zyxw-9876-9876zyxw
```

The command `az account list` lists your local subscriptions, and you might be out of synch with your remote account.
To refresh the list of subscriptions, execute the following command:

```shell
$ az account list --refresh
```

## Logging is not enabled for this container

If you deploy your application with NubesGen on Azure and something goes wrong, you want to check the logs.
For that, you go to the Azure console, and click on the menus _Monitoring -> Log Stream_ (the URL of the log stream looks like `https://<url of the app service>/logStream`).
But in certain cases, you will not see the logs of your application.
Instead you will get the following message:

```
INFO  - Logging is not enabled for this container.Please use https://aka.ms/linux-diagnostics to enable logging to see container logs here.
```

To see the logs of your application, from the Azure console, you need to follow the menus _Development Tools -> Advanced Tools -> Go -> Current Docker logs_.
You will reach a page displaying JSON with several URLs pointing at several log files.
This JSON looks like this:

```
[{"machineName":"ABCD_default","size":198155,"href":"https://myapp-001.scm.azurewebsites.net/api/vfs/LogFiles/2021_06_04_ABCD_default_docker.log"},
{"machineName":"EFGH","size":277263,"href":"https://myapp-001.scm.azurewebsites.net/api/vfs/LogFiles/2021_06_04_EFGH_docker.log"}]
```

To see the logs, just follow the link displayed on the JSON (eg. `https://myapp-001.scm.azurewebsites.net/api/vfs/LogFiles/2021_06_04_ABCD_default_docker.log`).

[[ << Frequently asked questions ](frequently-asked-questions.md) | [ Main documentation page >> ](README.md)]
