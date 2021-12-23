# NubesGen templates documentation

NubesGen templates are stored in this directory.

## Template packs

Each sub-directory is a "template pack". For example, "terraform" is a template pack, 
containing all templates for supporting Terraform.

## Composition of a template pack

All templates have the `.mustache` suffix, as they are Mustache templates.

The template pack consists of two sets of files:

- The root template directory contains the main templates. 
  For example, `terraform/main.tf.mustache` is the template for the `main.tf`
  Terraform configuration.
- The `modules` directory contains the optional templates, used 
  depending on the end-user's choices. For example, `terraform/modules/mysql/main.tf.mustache`
  is the template for the `main.tf` Terraform configuration, when the MySQL option is selected.
  
## Mustache variables and conditional statements

Mustache has variables and conditional statements, which are detailed in the 
`io.github.nubesgen.configuration.NubesgenConfiguration.java` Java file.
