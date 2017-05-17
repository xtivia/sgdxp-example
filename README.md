## Using Service Guard for Liferay DXP
This is a project that demonstrates how to use [Service Guard for Liferay DXP](https://github.com/xtivia/sgdxp). Service Guard allows us to use simple annotations to mark JAX-RS based REST services that we wish to protect using Liferay 7 DXP's Role and Permissions system. 

Note that while the focus here is on Liferay 7 DXP Service Guard also works just fine using Liferay CE as well.

### JAX-RS Services in Liferay 7 DXP

This project extends from another GitHub [project](https://github.com/xtivia/dxp-rest-example) that demonstrates the basics associated with creating JAX-RS based REST services in DXP. The reader is strongly encouraged to visit that project first and become familiar with the described approach for the construction of JAX-RS services in DXP using the newly added features in version 7.

Here we will leverage the service for "people" first developed in the that project to create derived versions of the service and then demonstrate how to use Service Guard to control access to each one of those derived endpoints.

### Prerequisites

- Install Gradle from http://gradle.org/gradle-download. (**NOTE**: this project's build script uses features introduced in Gradle 2.12 so that is the **minimum** required version).

- Install and perform basic configuration for Liferay DXP or Liferay 7 CE.

- Configure the CXF and REST Extender system settings as described in the "DXP Configuration" section of the referenced project

### Service Guard Distribution

Service Guard is distributed as an OSGi module (JAR). It is available via the standard bintray/JCenter repository.

To compile your code that uses Service Guard you will need to do the following (as seen in the build.gradle file for this project):
- reference jcenter() in the repositories section of the Gradle build script
- add a dependency for group:'com.xtivia.tools', name:'sgdxp',                                    version:'1.0.0'

For runtime use you will need to install the Service Guard module in your local DXP environment. The JAR file is available at http://jcenter.bintray.com/com/xtivia/tools/sgdxp/1.0.0/sgdxp-1.0.0.jar. The simplest approach for installing the JAR is to  download it using the URL above and then copy it into the *deploy* directory of your DXP installation. (It can also be installed using the DXP gogo shell using the URL above).

### Using Service Guard

To use Service Guard you will need to do the following two things in your code:
1. Extend your JAX-RS Application class from *SgDxpApplication*
2. In your application's *getSingletons()* method invoke and use the returned object from *super.getSingletons()* method to allow Service Guard to register its own JAX-RS providers in addition to those of your application
3. Annotate your JAX-RS services as required to add access control for them based on DXP's roles and organizations

NOTE: All of the steps described above can be seen in the supplied source code for this project. In this project we have created multiple JAX-RS resources where each such resource extends the base *People* resource and then adds a single GET-based method to demonstrate one of the ServiceGuard annotations.

### Service Guard Annotations

Service Guard provides a rich set of annotations that allow you to leverage DXP role assignments and/or organization memberships for protecting access to your services. **Note that these annotations can be applied either at the class level, or at the individual method level**. If the annotation is applied at the class level, then all JAX-RS resource endpoints in the class will have the effect of the annotation applied to them without further need to annotate individual methods.

| Annotation   |      Example      |  Notes |
|----------|:-------------:|:------|
| @Authenticated |@Authenticated| Ensures that the user attempting to access the endpoint has been logged in to DXP |
| @Omniadmin |@Omniadmin| Ensures that the user attempting to access the endpoint is a Portal Adminstrator  |
| @RegularRole |@RegularRole("RegRole1")| Ensures that the user attempting to access the endpoint has been assigned to the named Regular (portal-scoped) role |
| @OrgMember | @OrgMember("Org1") |  Ensures that the user attempting to access the endpoint is a member of the named organization  |
| @OrgRole | @OrgRole(org="Org1",role="OrgRole1") |  Ensures that the user attempting to access the endpoint has been assigned to the named Organization role for the specified organization  |
| @Authorized | @Authorized |Indicates that a custom authorization scheme is implemented for the annotated class/method (see below)**  |
  
###Custom Authorization

Even with the annotations described above, there are often still cases where we require customized authorization techniques that leverage other aspects of the DXP environment and/or things like the specific request being made, HTTP headers, etc. Or perhaps we need more fine-grained access and want to know if the user has access to a specific Liferay permission.

To address these needs Service Guard provides the **@Authorized** annotation. To implement a custom authorization approach using Service Guard you will need to perfomr the following steps:

1. Add an *@Authorized* annotaton to the class/method that you wish to protect
2. Implement/override the *getAuthorizer()* method in your JAX-RS Application sub-class (derived from SgDxpApplication as described above). Your custom *getAuthorizer()* method should return an instance of a class that implements the *IAuthorizer* interface.

Providing a class that implements *IAuthorizer* is the key to the custom authorization system for Service Guard. The Service Guard framework will invoke the *authorize()* method in this class any time an attempt is made to access a JAX-RS endpoint that has been protected with the *@Authorized* annotation described above. The *authorize()* method accepts a single *IContext* parameter provided by the framework which is a an object that provides a variety of useful fields and objects upon which a custom authorization decision can be made (see below). The authorize() method should return true to indicate that authorization is successful, and false otherwise. 

### IContext

The IContext object extends the Map interface and provides a set of objects that can be used by your custom authorization function. At present the following keys can be used:

| Identifier   |  Description |
|:----------|:-------------|	
| ICommandKeys.HTTP_SESSION | Returns the HttpSession associated with the current request.|
| ICommandKeys.HTTP_REQUEST | Returns the HttpRequest associated with the current request.|
| ICommandKeys.SERVLET_CONTEXT | Returns the ServletContext associated with the current request.|
| ICommandKeys.PATH_PARAMETERS | Returns a map of the path parameters associated with the curent request. For example, if the Route associated with the current request had a URI like /hello/world/{last}/{first}, this map would include keys for ‘last’ and ‘first’ with the values that had been supplied for each. |
| ICommandKeys.RESOURCE_CLASS | Returns a JAX-RS ResourceClass object that can be used to obtain information about the resource class that is about to be invoked.|
| ICommandKeys.RESOURCE_METHOD | Returns a JAX-RS ResourceMethod object that can be used to obtain information about the resource method that is about to be invoked|
| DxpCmmandKeys.LIFERAY_USER|Returns the Liferay User object associated with the current request|
| DxpCmmandKeys.LIFERAY_COMPANY_ID|Returns the Liferay company ID associated with the current user|
| DxpCmmandKeys.LIFERAY_PERMISSION_CHECKER|Returns an instance of a Liferay PermissionChecker object. Note that Service Guard optimizes the creation of these objects so that only one is created per request.|
