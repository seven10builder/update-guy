**Show all configured repository information**
----
  Returns json data listing all configured repositories.

* **URL**

  `/repository/show`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
  `None`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />
    **Content:**       
    [
        {
            "repoAddress": "192.168.99.91",
            "port": 21,
            "user": "user_1",
            "manifestPath": "/users/aturing/manifests",
            "description": "This is a regular FTP repo",
            "repoType": "ftp"
        },
        {
            "repoAddress": "localhost",
            "port": 0,
            "user": "",
            "manifestPath": "/home/achurch/myRepos/manifests",
            "description": "This is a regular local repo",
            "repoType": "local"
        }
    ]
    
    Note: The user's password is not shown when this information is retrieved from the server.
 
* **Error Response:**

  * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
    **Content:** `{ error : "Could not read repo config file"}`

**Show Specific Repository Information**
----
  Returns json data about a specific repo.

* **URL**

 `/repository/show/:repoId`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
   `repoId=[MD5 Hash]`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />
    **Content:** 
  
      {
                "repoAddress": "localhost",
                "port": 0,
                "user": "",
                "manifestPath": "/home/achurch/myRepos/manifests",
                "description": "This is a regular local repo",
                "repoType": "local"
            }
    
    Note: The user's password is not shown when this information is retrieved from the server.

 
* **Error Response:**

  * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
    **Content:** `{ error : "Could not read repo config file"}`
    
**Create repository information Entry**
----
  Creates a repository entry in the update-guy server that can subsequently be used.

* **URL**

 `/repository/create`

* **Method:**

  `POST`
  
*  **URL Params**

   **Required:**
 
   `None`

* **Data Params**

  `repoInfo=[json data representing the Repository Info structure]`
  See readme.md for more information.
  **example:**
      {
        "repoAddress": "192.168.99.91",
        "port": 21,
        "user": "user_1",
        "password": "password_1",
        "manifestPath": "/users/aturing/manifests",
        "description": "This is a regular FTP repo",
        "repoType": "ftp"
      }

* **Success Response:**

  * **Code:** `200 OK`<br />
    **Content:** 
    a string containing the repository id for the new information

 
* **Error Response:**

  * **Code:** `304 NOT_MODIFIED` <br />
    **Content:** `{ error : "repo info already exists (or matching hash found). Delete first."}`
    
**Delete existing repository**
----
  Removes a repository information entry from the configuration.

* **URL**

 `/repository/delete/:repoId`

* **Method:**

  `DELETE`
  
*  **URL Params**

   **Required:**
 
   `repoId=[MD5 Hash]`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 O`K<br />

  * **Content:** `None`
 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "Could not write repository information file"}`

  OR

   * **Code:** `404 NOT_FOUND` <br />
      **Content:** `{ error : "Repository entry does not exist"}`


----------


**Show manifest(s)**
----
  Returns json data about manifests in a given repository. If a release family is not provided, all manifests will be listed.

* **URL**

 `/manifest/:repoId/show/:releaseFamily`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
   `repoId=[MD5 Hash]`

   **Optional:**
   `releaseFamily=[String]`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />

  * **Content:** 
 
      {
      "releaseFamily": "my-product",
      "created": "2014-06-06 2:07:18.200",
      "retrieved": "2016-06-06 12:24:19.200",
      "versions": {
        "1.0": {
          "releaseFamily": "my-product",
          "version": "1.0",
          "publishDate": "2014-06-06 2:08:33.200",
          "roleMap": {
            "web-service": {
              "filePath": "/users/aturing/1.0/server.war",
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "a923e2165a954ab5b909259116696d39"
              },
            "db": {
              "filePath": "/users/aturing/1.0/mongo.jar",
              "commandLine": [
                "/sbin/startDatabases",
                "bdname"
              ],
              "fingerPrint": "5b909259116696da923e2165a954ab39"
              },
            "monitor": {
              "filePath": "/users/aturing/1.0/monitor.jar",
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "54ab5b909a923e2165a9259116696d39"
              },
            "admin": {
              "filePath": "/users/aturing/1.0/admin.jar"
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "165a96696259113e2d394ab5b909"
              }
          }
        },
        "1.1": {
          "releaseFamily": "my-product",
          "version": "1.1",
          "publishDate": "2014-07-16 14:10:20.200",
          "roleMap": {

            "web-service": {
              "filePath": "/users/aturing/1.1/server.war",
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "a923e2165a954ab5b909259116696d39"
              },
            "db": {
              "filePath": "/users/aturing/1.1/redis.jar",
              "commandLine": [
                "/sbin/startDatabases",
                "bdname"
              ],
              "fingerPrint": "5b909259116696da923e2165a954ab39"
              },
            "monitor": {
              "filePath": "/users/aturing/1.0/monitor.jar",
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "54ab5b909a923e2165a9259116696d39"
              },
            "admin": {
              "filePath": "/users/aturing/1.0/admin.jar"
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "165a96696259113e2d394ab5b909"
              }
          }
        }        
    }
  }

 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "Could not open manifest"}`

  OR

   * **Code:** `404 NOT_FOUND` <br />
      **Content:** `{ error : "could not find manifest for given release family"}`

**Get or Set active release**
----
  Gets or sets the active release for an active version id. 

* **URL**

 `/manifest/:repoId/active-release/:releaseFamily/:activeVersId?newVersion=:newVersion`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
   `repoId=[MD5 Hash]` - The identifier for the source repository
   `releaseFamily=[String]` - The name of the release family
   `activeVersionId=[String]` - The target active version id

   **Optional:**
   `newVersion=[String]` - If set, this value will be assigned as the new version

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />

  * **Content:** 
 
        {
            "web-service": {
              "filePath": "/users/aturing/1.0/server.war",
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "a923e2165a954ab5b909259116696d39"
              },
            "db": {
              "filePath": "/users/aturing/1.0/mongo.jar",
              "commandLine": [
                "/sbin/startDatabases",
                "bdname"
              ],
              "fingerPrint": "5b909259116696da923e2165a954ab39"
              },
            "monitor": {
              "filePath": "/users/aturing/1.0/monitor.jar",
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "54ab5b909a923e2165a9259116696d39"
              },
            "admin": {
              "filePath": "/users/aturing/1.0/admin.jar"
              "commandLine": [
                "java",
                "jar",
                "start"
              ],
              "fingerPrint": "165a96696259113e2d394ab5b909"
              }
          }
 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "could not read active version entry"}`

  OR

   * **Code:** `404 NOT_FOUND` <br />
      **Content:** `{ error : "Could not find manifest file"}`

----------


**Show all roles**
----
  Returns a list of role names available for a given release

* **URL**

 `/release/:repoId/:releaseFamily/roles?version=:version`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
   `version=[String]` - identifies the version to get the roles for

   **Optional:**
   `None`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />

  * **Content:** 

    {
        "web-service",
        "db",
        "monitor",
        "admin"
    }
 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "could not get version entry"}`

**Get role information for role**
----
  Returns the information for the role file, including a unique fingerprint, for the file assigned to the given role. This fingerprint is used to determine if the version of the file the client has needs to be updated.
  
* **URL**

 `/release/:repoId/:releaseFamily/roleInfo/:roleName?version=:version`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
    `version=[String]` - identifies the version to get the fingerprint for
    `roleName=[String]` - identifies the role to get the fingerprint for

   **Optional:**
   `None`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />

  * **Content:** 
     {
        "commandLine": [
            "java",
            "jar",
            "start"
          ],
          "fingerPrint": "54ab5b909a923e2165a9259116696d39"
      }
 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "could not get version entry"}`

  OR

   * **Code:** `404 NOT_FOUND` <br />
      **Content:** `{ error : "Could not find file for role name"}`

**Download file for role**
----
  Initiates the http transfer of the file for the specified role, relative to the repository, release family and version.
  
* **URL**

 `/release/:repoId/:releaseFamily/download/:roleName?version=:version`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
    `version=[String]` - identifies the version to get the file for
   `roleName=[String]` - identifies the role to get the file for

   **Optional:**
   `None`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />

  * **Content:** file attachment, octet stream
 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "could not get version entry"}`

  OR

   * **Code:** `404 NOT_FOUND` <br />
      **Content:** `{ error : "Could not find file for role name"}`

**Update server cache**
----
  Initiates the caching of the relevent version files to the update-guy server. This allows caching to be done before cutting over to a new version.
  
* **URL**

 `/release/:repoId/:releaseFamily/update-cache?version=:version`

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
    `version=[String]` - identifies the version to get the file for

   **Optional:**
   `None`

* **Data Params**

  `None`

* **Success Response:**

  * **Code:** `200 OK`<br />

  * **Content:** `file attachment, octet stream`
 
* **Error Response:**

   * **Code:** `500 INTERNAL_SERVER_ERROR` <br />
     **Content:** `{ error : "could not get version entry"}`