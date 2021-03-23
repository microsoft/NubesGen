# Using Spring Boot with NubesGen

## Create a Spring Boot application

Create a sample Spring Boot application using [https://start.spring.io/](https://start.spring.io/).

For example, using PostgreSQL:
```
curl https://start.spring.io/starter.tgz -d dependencies=web,data-jdbc,postgresql -d baseDir=nubesgen -d bootVersion=2.4.2.RELEASE -d javaVersion=11 | tar -xzvf -
```

## Generate the infrastructure with NubesGen

Go to the Nubesgen website and create an infrastructure configuration using the same technologies (in this example, PostgreSQL):

```
curl "http://localhost:8080/nubesgen-test-1234.tgz?region=westeurope&runtime=spring&database=postgresql"  | tar -xzvf -
```
_Change `nubesgen-test-1234` by a unique name that you will use for your application_

Apply the Terraform configuration:

```
cd terraform && terraform init && terraform apply -auto-approve
```

## Configure the application to use the infrastructure

Deploy your Spring Boot application to Azure, for example using the Maven plugin:

Add in your `pom.xml`:

```
<plugin>
    <groupId>com.microsoft.azure</groupId>
	<artifactId>azure-webapp-maven-plugin</artifactId>
	<version>1.12.0</version>
	<configuration>
	<schemaVersion>V2</schemaVersion>
	<resourceGroup>rg-nubesgen-test-1234-001</resourceGroup>
	<appName>app-nubesgen-test-1234-001</appName>
	<deployment>
		<resources>
			<resource>
				<directory>${project.basedir}/target</directory>
				<includes>
					<include>*.jar</include>
				</includes>
			</resource>
		</resources>
	</deployment>
	</configuration>
</plugin>
```
_In the `<resourceGroup>` and `<appName>` sections, change `nubesgen-test-1234` by 
the unique name you used above to generate your application_

## Deploy

Deploy your application (without running tests):
```
./mvnw package azure-webapp:deploy -DskiptTests
```

__Congratulations, you have deployed your Spring Boot application to Azure!__