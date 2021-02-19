# GitOps with NubesGen

[Do you want to use GitOps with NubesGen? Here is the quickstart](gitops-quick-start.md)

## What is GitOps?

GitOps is the practice of automating infrastructure management using Git as a single source of truth.

## How is GitOps implemented in NubesGen?

NubesGen uses Git, Terraform and GitHub Actions together to automate infrastructure creation and management.

The best way to describe it is through an example:

- Alice, a new developer, joins the project.
- Alice creates a new branch, called `env-alice`.
- As soon as the branch is pushed, Alice automatically has a working environment.
- If Alice wants to modifies the environment, for example upgrading the database to another tier, the only command to do is `git push` and the environment is automatically updated.

## What are the benefits of GitOps with NubesGen?

### A unique source of truth

In each environment branch (`env-alice`, `env-bob`, `env-test`, `env-prod`...), Git is the single source of truth. It is easy to understand, test and modify the environments.

### Developers are empowered to use the cloud

Even without knowing anything about the underlying cloud provider, developers are able to quickly and automatically create, modify and destroy cloud resources. This makes them far more efficient.

### Advanced users still have the full power of Terraform

Advanced users can still benefit from Terraform, a well-known and beloved tool used by Ops people worldwide.

## How does this work behind-the scenes?

When choosing the `GitOps` option, NubesGen generates a GitHub Action next to its normal Terraform configuration.

Once committed into Git, this GitHub Action works in the following way:

- When a new branch is created, with a name starting with `env-` (like `env-alice` in our previous example), the GitHub Actions runs the Terraform configuration, using the environment name.
- As a result, a specific resource group is created, with specific resources inside: they all use the environment name inside their name, and they are also tagged with it.
- When someone pushes code to the `env-` branch, the Terraform configuration is applied again, so the cloud resources are updated accordingly.

## What happens if something does bad?

NubesGen uses the `backend` configuration of Terraform: it stores the state of each cloud infrastructure inside an Azure Blob container.

As a result, you can run Terraform locally and interact with your cloud resources: you can use the usual Terraform commands to modify or apply the state that you need. Then, next time a commit is pushed to the branch, Terraform will apply its configuration again.

In any case, NubesGen creates all cloud infrastructures in specific resource groups, named after the project name and the environment name that you created: if you want to destroy everything, you can just destroy the resource groups, and everything will disappear.

## Next step: go to the quickstart

[GitOps with NubesGen quickstart](gitops-quick-start.md)
