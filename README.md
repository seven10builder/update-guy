update-guy version 0.1
======================

> Alan: Hey, where'd you get that update? 

>  Alanzo: I got 'a guy'.

**Update-guy** 
is a utility allows developers to push updates to a central repository (FTP or local file system) that consumers can then retrieve and use automatically in a long-running environment.

Update-guy works via client-server communication. The server maintains a webservice that provides a REST api. The server also provides a cache of the current 'version' of the served application.

The client uses the api provided by the server to determine if published content has been updated, and if so, it retrieves the content and then, if possible, executes it. This process is repeated by the client each time the executed process terminates, until the client itself is terminated.

Update-guy Server
-----------------
**Defaults**

The server will, at start, attempt to load the repo configuration file. By default, the server will look for this file in *./local/repos.json*. This location can be changed by setting `update-guy.localPath` to the desired folder path  or `update-guy.repoFileName` to the file name.

By default, the server listens to port *7519*. This can be changed by setting the property `update-guy.serverPort`.

**Repo Configuration File**

The repo configuration file tells update-guy how to connect to the published repository. The server can connect to both an FTP archive or to a file mounted in the local file system. 
The following is an example configuration file that contains two repos.

    [
      {
        "repoAddress": "192.168.99.91",
        "port": 21,
        "user": "user_1",
        "password": "password_1",
        "manifestPath": "/users/aturing/manifests",
        "description": "This is a regular FTP repo",
        "repoType": "ftp"
      },
      {
        "repoAddress": "localhost",
        "port": 0,
        "user": "",
        "password": "",
        "manifestPath": "/home/achurch/myRepos/manifests",
        "description": "This is a regular local repo",
        "repoType": "local"
      }
    ]
    

 - repoAddress -  This is a DNS resolvable name or IP for the address of the repo that is being configured. Use `localhost` for local repos
 - port - This is the repo's port to connect to. This value is ignored for local repos. For most FTP servers this value is probably 21.
 - user - This field is the username for the repository and supports ftp-convention email addresses. This field is not used for local repos.
 - password - The password associated with the above user name. This value is not used for local repos
 - manifestPath - The path, on the repo, where the manifest files (See below) can be found. For local repos, this is an ordinary folder path. For FTP repos, this is an FTP-style path.
 - description - A human-readable description of this repository.
 - repoType - The type of the repo this configuration is. Valid values are `local` and `ftp`.

**Release Families and Manifests**

Update-guy uses the concept of "release families" to distinguish between one published bundle and another. A release family might represent a product stack. It could also represent different branches of a product, such as a QA release family and a Production family.
The Release Family concept is implemented with "manifest" files. These json files list the versions available for a given release family and provide some meta information on the family. The naming convention for manifest files is *releaseFamily*.manifest.
Below is an example of a manifest file named my-product.manifest.

    {
      "releaseFamily": "my-product",
      "created": "2014-06-06 2:07:18.200",
      "retrieved": "2016-06-06 12:24:19.200",
      "versions": {
        "1.0": {
          "releaseFamily": "my-product",
          "version": "1.0",
          "publishDate": "2014-06-06 2:08:33.200",
          "fileMap": {
            "web-service": "/users/aturing/1.0/server.war",
            "db": "/users/aturing/1.0/mongo.jar",
            "monitor": "/users/aturing/1.0/monitor.jar",
            "admin": "/users/aturing/1.0/admin.jar"
          }
        },
        "1.1": {
          "releaseFamily": "my-product",
          "version": "1.1",
          "publishDate": "2014-07-16 14:10:20.200",
          "fileMap": {
            "web-service": "/users/aturing/1.1/server.war",
            "db": "/users/aturing/1.1/redis.jar",
            "monitor": "/users/aturing/1.1/monitor.jar",
            "admin": "/users/aturing/1.1/admin.jar"
          }
        }        
    }
    
- releaseFamily - This is the name of the release family, and should be the same as the first part of the filename.
- created - This is the date that the initial release family was created
- retrieved - This is the date the manifest was retrieved from the repository
- versions - This is a list of the version entries (see next). Each entry is a key-value pair. The key (eg. "1.0"), is a string reflecting the version for that entry.

**Version Entries**

A version entry is a block of information relating to a specific release of a product. A manifest file can contain several entries under the versions block. Referring to the example manifest file showed previously, the following fields are found in the version entry.

 - version - This value must be the same as the key for this entry.
 - publishDate - The date this version was published
 - releaseFamily - This is the same value as in the top-level of the manifest file.
 - fileMap - This list of key-value pairs maps roles (see next) to a specific file path. This path is the path where the file can be found on the repository.

**Roles**

Because each product (Release Family) may have several executables or other versioned modules, it is important to track what each module does. Thus, update-guy has the concept of "Roles". A role is simply an identifier that differentiates a module. A version entry may have one or more roles associated with it. The role key can be any text key.

**Active version Id**

One final concept update-guy uses for the server is an "active version id". An active version id is a string that identifies an active version to follow. For example, an active version id my-install might be created and set to version 1.8 of the product. Any clients that use my-install will then use 1.8. If at a later date, my-install is set to version 1.9, then all clients that use that id will automatically retrieve 1.9 and use that.
This version is set by the administrator via the rest API (see below).

**REST API**

Update-guy provides a rest API for clients to consume. See the document 'API.md' for full information.

Update-guy Client
-----------------
The update-guy client is responsible for running a process using the modules retrieved from the server. This process is typically an executable jar file launched with something like `java -jar myExecutable.jar`. When a client is launched, it is configured to use some active version id (see above). Update-guy will attempt to relauch the application with whatever the current active version is set to.

**Parameters**

The update-guy client can print help for its cli interface when executed with -h (or --help).
The client requires one parameter, -f (or --file) followed by the file path of the client settings file. (see below)
Any additional parameters passed to update-guy client will be forwarded to the executed application.

**Client Settings File**

The client config file is a json file responsible for configuring the client for its connection to the server. An example client config file is shown here.

    {
      "serverAddress": "my.update-guy.com",
      "serverPort": 7519,
      "repoId": "c9b85ad3348a7d4350ad50d4f9f45ed1",
      "roleName": "db",
      "releaseFamily": "myProduct",
      "cachePath": "cache/"
    }

 - serverAddress - This is the dns-resolvable name or IP of the update-guy server.
 - serverPort - This is the port that the update-guy server is listening on. By default, this is 7519.
 - repoId - This is the id of the repo to use. Because update-guy can support multiple repos at once, this identifies the specific repo this client is to draw from.
 - roleName - This is the role to retrieve the module for.
 - releaseFamily - This is the release family to draw from.
 - cachePath - This is the folder path to use as the local cache for the client.
